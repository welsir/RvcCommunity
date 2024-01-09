package com.tml.service.Impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tml.client.FileServiceClient;
import com.tml.client.UserServiceClient;
import com.tml.common.DetectionStatusEnum;
import com.tml.common.constant.ModelConstant;
import com.tml.common.exception.AbstractAssert;
import com.tml.common.exception.BaseException;
import com.tml.common.log.AbstractLogger;
import com.tml.config.SystemConfig;
import com.tml.core.async.AsyncService;

import com.tml.mapper.*;
import com.tml.pojo.DO.*;
import com.tml.pojo.DTO.AsyncDetectionForm;
import com.tml.pojo.DTO.DetectionTaskDTO;
import com.tml.pojo.DTO.ReceiveUploadFileDTO;
import com.tml.pojo.ResultCodeEnum;
import com.tml.pojo.VO.*;
import com.tml.service.ModelService;
import com.tml.utils.ConcurrentUtil;
import com.tml.utils.DateUtil;
import com.tml.utils.FileUtil;
import com.tml.utils.WrapperUtil;
import io.github.common.web.Result;
import io.github.id.snowflake.SnowflakeGenerator;
import io.github.id.snowflake.SnowflakeRegisterException;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.tml.common.DetectionStatusEnum.DETECTION_SUCCESS;
import static com.tml.common.DetectionStatusEnum.UN_DETECTION;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/4 16:48
 */
@Service
public class ModelServiceImpl implements ModelService {

    @Resource
    ModelMapper mapper;
    @Resource
    LabelMapper labelMapper;
    @Resource
    TypeMapper typeMapper;
    @Resource
    ModelUserMapper modelUserMapper;
    @Resource
    CommentMapper commentMapper;
    @Resource
    FileServiceClient fileServiceClient;
    @Resource
    UserServiceClient userServiceClient;
    @Resource
    AbstractLogger logger;
    @Resource
    AsyncService asyncService;
//    @Resource
//    ModelListener listener;
    @Resource
    SystemConfig systemConfig;
    @Resource
    SnowflakeGenerator snowflakeGenerator;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(20);

    /**
     * @description: 分页查询所有模型集合
     * @param: size
     * @param page
     * @param sortType
     * @param uid
     * @return: Page<ModelVO>
     **/
    @Override
    public Page<ModelVO> getModelList(Long size, Long page,String sortType,String uid) {
        try {
            QueryWrapper<ModelDO> wrapper = new QueryWrapper<>();
            wrapper = WrapperUtil.setWrappers(wrapper,Map.of("has_show",DetectionStatusEnum.DETECTION_SUCCESS.getStatus().toString(),
                    "has_delete",ModelConstant.UN_DELETE,"sort",sortType));
            return getModelListCommon(wrapper, page, size, uid);
        }catch (BaseException e){
            throw new BaseException(ResultCodeEnum.QUERY_MODEL_LIST_FAIL);
        }
    }

    /**
     * @description: 分页、类别模型集合
     * @param: typeId
     * @param page
     * @param size
     * @param order
     * @param uid
     * @return: Page<ModelVO>
     **/
    @Override
    public Page<ModelVO> getModelList(String typeId,Long page,Long size,String order,String uid) {
        AbstractAssert.isNull(typeMapper.selectById(typeId), ResultCodeEnum.TYPE_NOT_EXIT);
        try {
            QueryWrapper<ModelDO> wrapper = new QueryWrapper<>();
            wrapper = WrapperUtil.setWrappers(wrapper,Map.of("has_show",DetectionStatusEnum.DETECTION_SUCCESS.getStatus().toString(),
                    "has_delete",ModelConstant.UN_DELETE,"sort",order,"type_id",typeId));
            return getModelListCommon(wrapper, page, size, uid);
        }catch (BaseException e){
            throw new BaseException(ResultCodeEnum.QUERY_MODEL_LIST_FAIL);
        }
    }

    /**
     * @description: 查询某个模型详细信息
     * @param: modelId
     * @param uid
     * @return: ModelVO
     **/
    @Override
    public ModelVO queryOneModel(String modelId, String uid) {
        ModelDO model = mapper.selectById(modelId);
        AbstractAssert.isNull(model,ResultCodeEnum.MODEL_NOT_EXITS);
        try {
            Callable<String> typeTask = () -> typeMapper.selectTypeById(model.getTypeId());
            Callable<String> likeTask = () -> mapper.queryUserModelLikes(uid, modelId) == null ? "0" : "1";
            Callable<String> collectionTask = () -> mapper.queryUserModelCollection(uid, modelId) == null ? "0" : "1";
            Callable<List<LabelVO>> labelTask = () -> labelMapper.selectListById(modelId);

            Future<String> typeFuture = ConcurrentUtil.doJob(executorService, typeTask);
            String type = ConcurrentUtil.futureGet(typeFuture);
            Future<String> likeFuture = ConcurrentUtil.doJob(executorService, likeTask);
            String isLike = ConcurrentUtil.futureGet(likeFuture);
            Future<String> collectionFuture = ConcurrentUtil.doJob(executorService, collectionTask);
            String isCollection = ConcurrentUtil.futureGet(collectionFuture);
            Future<List<LabelVO>> labelFuture = ConcurrentUtil.doJob(executorService, labelTask);
            List<LabelVO> labelList = ConcurrentUtil.futureGet(labelFuture);

            UserInfoVO dto;
            ModelVO modelVO;
            if(!uid.isBlank()||!uid.isEmpty()){
                io.github.common.web.Result<UserInfoVO> userInfo = userServiceClient.one(uid);
                dto = userInfo.getData();
                AbstractAssert.isNull(dto,ResultCodeEnum.GET_USER_INFO_FAIL);
                modelVO = ModelVO.modelDOToModelVO(model, dto,labelList,type,isLike,isCollection);
            }else {
                modelVO = ModelVO.modelDOToModelVO(model, null,labelList,type,isLike,isCollection);
            }
            asyncService.asyncAddModelViewNums(modelId);
            return modelVO;
        }catch (BaseException e){
            logger.error(e);
            throw new BaseException(ResultCodeEnum.QUERY_MODEL_FAIL);
        }
    }

    @Transactional
    @Override
    public void insertOneModel(ModelInsertVO model,String uid) {
        //todo:默认只提供一个链接，需要上传用户自行将模型相关文件放入一个文件夹
        AbstractAssert.isNull(typeMapper.selectTypeById(model.getTypeId()),ResultCodeEnum.TYPE_NOT_EXIT);
        ModelDO modelDO = new ModelDO();
        BeanUtils.copyProperties(model,modelDO);
        try {
            modelDO.setId(snowflakeGenerator.generate());
        } catch (SnowflakeRegisterException e) {
            throw new BaseException(e.toString());
        }
        LocalDateTime today = LocalDateTime.now();
        modelDO.setUpdateTime(today);
        modelDO.setCreateTime(today);
        modelDO.setLikesNum(0L);
        modelDO.setCollectionNum(0L);
        modelDO.setViewNum(0L);
        modelDO.setHasDelete(false);
        modelDO.setHasShow(String.valueOf(DetectionStatusEnum.UN_DETECTION.getStatus()));
        mapper.insert(modelDO);
        AbstractAssert.isBlank(modelDO.getId().toString(),ResultCodeEnum.ADD_MODEL_FAIL);
        String typeId = model.getTypeId();
        typeMapper.insertModelType(modelDO.getId().toString(),typeId);
        ModelUserDO modelUserDO = new ModelUserDO();
        modelUserDO.setModelId(String.valueOf(modelDO.getId()));
        modelUserDO.setUid(uid);
        try {
            modelUserMapper.insert(modelUserDO);
            mapper.insertModelFileRelative(modelDO.getId().toString(),model.getFileUrl());
        }catch (RuntimeException e){
            logger.error(e);
            throw new BaseException(ResultCodeEnum.INSERT_MODEL_USER_RELATIVE_FAIL);
        }
        asyncService.processModelAsync(modelDO, model.getLabel());
    }

    @Override
    public String downloadModel(String modelId, String uid) {
        ModelFileDO modelFileDO = mapper.queryModelFile(modelId);
        AbstractAssert.isNull(modelFileDO,ResultCodeEnum.MODEL_NOT_EXITS);
        return modelFileDO.getUrl();
    }
    @Override
    public Boolean editModelMsg(ModelUpdateFormVO modelUpdateFormVO,String uid) {
        AbstractAssert.isNull(mapper.selectById(modelUpdateFormVO.getId()),ResultCodeEnum.MODEL_NOT_EXITS);
        if(!FileUtil.isImageFile(modelUpdateFormVO.getPicture().getOriginalFilename())){
            throw new BaseException(ResultCodeEnum.UPLOAD_IMAGE_FAIL);
        }
        com.tml.pojo.Result<ReceiveUploadFileDTO> res;
        try {
            res = fileServiceClient.uploadModel(
                    UploadModelForm.builder()
                            .file(modelUpdateFormVO.getPicture())
                            .path(ModelConstant.DEFAULT_MODEL_PATH)
                            .bucket(ModelConstant.DEFAULT_BUCKET)
                            .md5(FileUtil.getMD5Checksum(modelUpdateFormVO.getPicture().getInputStream()))
                            .build());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LocalDateTime lt = LocalDateTime.now();
        UpdateWrapper<ModelDO> wrapper = new UpdateWrapper<>();
        wrapper.eq("id",modelUpdateFormVO.getId())
                .set("name",modelUpdateFormVO.getName())
                .set("description",modelUpdateFormVO.getDescription())
                .set("note",modelUpdateFormVO.getNote())
                .set("picture",res.getData().getUrl())
                .set("update_time",lt);
        mapper.update(null,wrapper);
        //todo:走审核
        return true;
    }

    @Override
    public List<ReceiveUploadFileDTO> uploadModel(MultipartFile[] file,String uid) {
        if(!FileUtil.checkModelFileIsAvailable(file)){
            throw new BaseException(ResultCodeEnum.MODEL_FILE_ILLEGAL);
        }
        try {
            com.tml.pojo.Result<List<ReceiveUploadFileDTO>> res = fileServiceClient.uploadModelList(
                    List.of(file[0],file[1]),
                    List.of(ModelConstant.DEFAULT_MODEL_PATH,ModelConstant.DEFAULT_MODEL_PATH),
                    List.of(FileUtil.getMD5Checksum(file[0].getInputStream()),FileUtil.getMD5Checksum(file[1].getInputStream())),
                    ModelConstant.DEFAULT_BUCKET);
            return res.getData();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }catch (Exception e){
            logger.error(e);
            throw new BaseException(e.toString());
        }
    }

    @Override
    public ReceiveUploadFileDTO uploadImage(MultipartFile file,String uid) {
        if(FileUtil.checkImageFileIsAvailable(file)){
            try {
                UploadModelForm form = UploadModelForm.builder()
                        .file(file)
                        .path(ModelConstant.DEFAULT_IMAGE_PATH)
                        .bucket(ModelConstant.DEFAULT_BUCKET)
                        .md5(FileUtil.getMD5Checksum(file.getInputStream()))
                        .build();
                com.tml.pojo.Result<ReceiveUploadFileDTO> res = fileServiceClient.uploadModel(form);
                return res.getData();
            } catch (NoSuchAlgorithmException | IOException e) {
                logger.error("%s:"+e.getStackTrace()[0],e);
                throw new BaseException(e.toString());
            }
        }else{
            throw new BaseException(ResultCodeEnum.UPLOAD_IMAGE_FAIL);
        }
    }

    @Override
    public ReceiveUploadFileDTO uploadAudio(MultipartFile file, String uid) {
        if(FileUtil.checkAudioFileIsAvailable(file)){
            try {
                UploadModelForm form = UploadModelForm.builder()
                        .file(file)
                        .path(ModelConstant.DEFAULT_MODEL_PATH)
                        .bucket(ModelConstant.DEFAULT_BUCKET)
                        .md5(FileUtil.getMD5Checksum(file.getInputStream()))
                        .build();
                com.tml.pojo.Result<ReceiveUploadFileDTO> res = fileServiceClient.uploadModel(form);
                return res.getData();
            } catch (NoSuchAlgorithmException | IOException e) {
                logger.error("%s:"+e.getStackTrace()[0],e);
                throw new BaseException(e.toString());
            }
        }else{
            throw new BaseException(ResultCodeEnum.UPLOAD_AUDIO_FAIL);
        }
    }

    @Override
    public String insertLabel(String label, String uid) {
        AbstractAssert.notNull(labelMapper.queryLabelIsExits(label),ResultCodeEnum.LABEL_IS_EXIT);
        LabelDO labelDO = new LabelDO();
        LocalDateTime lt = LocalDateTime.now();
        try {
            labelDO.setId(snowflakeGenerator.generate());
            labelDO.setLabel(label);
            labelDO.setCreateTime(lt);
            labelDO.setHasShow(DETECTION_SUCCESS.getStatus().toString());
            labelMapper.insert(labelDO);
            //todo:走审核
        }catch (RuntimeException e){
            logger.error(e);
            throw new BaseException(ResultCodeEnum.ADD_MODEL_LABEL_FAIL);
        } catch (SnowflakeRegisterException e) {
            throw new RuntimeException(e);
        }
        return String.valueOf(labelDO.getId());
    }

    @Override
    public Page<ModelVO> getUserLikesList(String uid,Long page,Long limit,String order) {
        long systemPage = Long.parseLong(systemConfig.getPageSize());
        limit = limit==null? systemPage :limit>systemPage? systemPage :limit;
        List<String> modelIds = mapper.getUserLikesModel(uid);
        if(modelIds.isEmpty()){
            return new Page<ModelVO>(page, limit, 0).setSize(0);
        }
        QueryWrapper<ModelDO> wrapper = new QueryWrapper<>();
        wrapper.in("id",modelIds);
        wrapper = WrapperUtil.setWrappers(wrapper,Map.of("has_show",DetectionStatusEnum.DETECTION_SUCCESS.getStatus().toString(),
                "has_delete",ModelConstant.UN_DELETE,"sort",order));
        Page<ModelDO> modelDOPage = mapper.selectPage(new Page<>(page, limit), wrapper);
        List<ModelVO> modelVOList = modelDOPage.getRecords().stream().map(modelDO -> convertToModelVO(modelDO, uid, null))
                .collect(Collectors.toList());
        return new Page<ModelVO>().setRecords(modelVOList).setSize(modelVOList.size());
    }

    @Override
    public Page<ModelVO> getUserCollectionList(String uid,Long page,Long limit,String order) {
        long systemPage = Long.parseLong(systemConfig.getPageSize());
        limit = limit==null? systemPage :limit>systemPage? systemPage :limit;
        List<String> modelIds = mapper.getUserCollectionModel(uid);
        if(modelIds.isEmpty()){
            return new Page<ModelVO>(page, limit, 0).setSize(0);
        }
        QueryWrapper<ModelDO> wrapper = new QueryWrapper<>();

        wrapper.in("id",modelIds);
        wrapper = WrapperUtil.setWrappers(wrapper,Map.of("has_show",DetectionStatusEnum.DETECTION_SUCCESS.getStatus().toString(),
                "has_delete",ModelConstant.UN_DELETE,"sort",order));
        Page<ModelDO> modelDOPage = mapper.selectPage(new Page<>(page, limit), wrapper);
        List<ModelVO> modelVOList = modelDOPage.getRecords().stream().map(modelDO -> convertToModelVO(modelDO, uid, null))
                .collect(Collectors.toList());
        return new Page<ModelVO>().setRecords(modelVOList).setSize(modelVOList.size());
    }

    @Transactional
    @Override
    public Boolean delSingleModel(String modelId,String uid) {
        AbstractAssert.isNull(modelUserMapper.selectById(modelId),ResultCodeEnum.QUERY_MODEL_FAIL);
        AbstractAssert.isNull(modelUserMapper.queryModelUserRelative(uid,modelId),ResultCodeEnum.QUERY_MODEL_FAIL);
        UpdateWrapper<ModelDO> wrapper = new UpdateWrapper<>();
        wrapper.eq("id",modelId);
        wrapper.set("has_delete",ModelConstant.DELETE);
        LocalDateTime lt = LocalDateTime.now();
        wrapper.set("update_time",lt);
        mapper.deleteLikesByModelId(modelId);
        mapper.deleteCollectionByModelId(modelId);
        return mapper.update(null,wrapper)>0;
    }

    @Override
    public Page<ModelVO> queryUserModelList(String uid,Long page,Long limit) {
        List<String> modelIds = modelUserMapper.selectModelIdByUid(uid);
        long maxLimit = limit ==null?Long.parseLong(systemConfig.getPageSize()):Math.min(limit, Long.parseLong(systemConfig.getPageSize()));
        QueryWrapper<ModelDO> wrapper = new QueryWrapper<>();
        wrapper.in("id",modelIds);
        wrapper = WrapperUtil.setWrappers(wrapper,Map.of("sort",""));
        Page<ModelDO> modelPage = mapper.selectPage(new Page<>(page, maxLimit,false),wrapper);
        List<ModelVO> modelVOList = modelPage.getRecords().stream().map(modelDO -> convertToModelVO(modelDO, uid, null))
                .collect(Collectors.toList());
        return new Page<ModelVO>().setRecords(modelVOList).setSize(modelVOList.size());
    }

    //todo:换成细粒度更小的事务控制
    @Transactional
    @Override
    public String commentModel(CommentFormVO commentFormVO,String uid) {
        AbstractAssert.isNull(mapper.selectById(commentFormVO.getModelId()),ResultCodeEnum.MODEL_NOT_EXITS);
        if(StringUtils.isBlank(commentFormVO.getReplyId())){
            AbstractAssert.notNull(commentMapper.selectById(commentFormVO.getReplyId()),ResultCodeEnum.COMMENT_NOT_EXITS);
        }else {
            AbstractAssert.isNull(commentMapper.selectById(commentFormVO.getReplyId()),ResultCodeEnum.COMMENT_NOT_EXITS);
        }
        try {
            CommentDO commentDO = new CommentDO();
            commentDO.setId(snowflakeGenerator.generate());
            commentDO.setContent(commentFormVO.getContent());
            commentDO.setModelId(commentFormVO.getModelId());
            commentDO.setUid(uid);
            commentDO.setHasShow(DETECTION_SUCCESS.getStatus().toString());
            LocalDateTime lt = LocalDateTime.now();
            commentDO.setUpdateTime(lt);
            commentDO.setCreateTime(lt);
            commentDO.setParentId(commentFormVO.getReplyId());
            commentDO.setLikesNum(0L);
            commentMapper.insert(commentDO);
            if(commentDO.getParentId()==null||"".equals(commentDO.getParentId())){
                commentMapper.insertFirstModelComment(commentDO.getModelId(),commentDO.getId().toString());
            }
            //todo:走审核
            return commentDO.getId().toString();
        }catch (RuntimeException e){
            logger.error(e);
            throw new BaseException(ResultCodeEnum.ADD_COMMENT_FAIL);
        } catch (SnowflakeRegisterException e) {
            throw new RuntimeException(e);
        }
    }

    //todo:换成细粒度更小的事务控制
    @Transactional
    @Override
    public Boolean likeComment(String uid, String commentId,String type) {
        CommentDO commentDO = commentMapper.selectById(commentId);
        AbstractAssert.isNull(commentDO,ResultCodeEnum.COMMENT_NOT_EXITS);
        if(ModelConstant.FLAG.equals(type)){
            AbstractAssert.notNull(commentMapper.selectDOById(commentId,uid),ResultCodeEnum.USER_LIKES_ERROR);
            UpdateWrapper<CommentDO> wrapper = new UpdateWrapper<>();
            wrapper.eq("id",commentId).setSql("likes_num = likes_num+1");
            commentMapper.update(null,wrapper);
            return commentMapper.insertUserCommentRelative(commentId,uid)==1;
        }
        if(ModelConstant.UN_FLAG.equals(type)){
            if(commentDO.getLikesNum()==0){
                return true;
            }
            UpdateWrapper<CommentDO> wrapper = new UpdateWrapper<>();
            wrapper.eq("id",commentId).setSql("likes_num = likes_num-1");
            commentMapper.update(null,wrapper);
            commentMapper.delUserCommentLikes(commentId,uid);
            return true;
        }
        throw new BaseException(ResultCodeEnum.PARAMS_ERROR);
    }

    @Override
    public Page<FirstCommentVO> queryFirstCommentList(String modelId, Long page, Long limit, String sortType,String uid) {
        AbstractAssert.isNull(mapper.selectById(modelId),ResultCodeEnum.MODEL_NOT_EXITS);
        List<String> firsComments  = commentMapper.queryCommentIds(modelId);
        AbstractAssert.isTrue(firsComments==null||firsComments.isEmpty(),ResultCodeEnum.COMMENT_NOT_EXITS);
        QueryWrapper<CommentDO> wrapper = new QueryWrapper<>();
        wrapper.in("id",firsComments);
        wrapper = WrapperUtil.setWrappers(
                wrapper,
                Map.of("has_show",DetectionStatusEnum.DETECTION_SUCCESS.getStatus().toString(),"sort",sortType));
        return getFirstComment(wrapper,page,limit,uid);
    }

    @Override
    public Page<SecondCommentVO> querySecondCommentList(String parentCommentId,Long page,Long limit,String sortType, String uid) {
        List<String> secondComments  = commentMapper.querySecondComments(parentCommentId);
        QueryWrapper<CommentDO> wrapper = new QueryWrapper<>();
        wrapper.in("id",secondComments);
        wrapper = WrapperUtil.setWrappers(
                wrapper,
                Map.of("has_show",DetectionStatusEnum.DETECTION_SUCCESS.getStatus().toString(),"sort",sortType));
        return getSecondCommentList(wrapper,page,limit,uid);
    }

    @Override
    @Transactional
    public Boolean userLikesModel(String status, String modelId, String uid) {
        ModelDO modelDO = mapper.selectById(modelId);
        AbstractAssert.isNull(modelDO,ResultCodeEnum.QUERY_MODEL_FAIL);
        if(ModelConstant.FLAG.equals(status)){
            AbstractAssert.notNull(mapper.queryUserModelLikes(uid,modelId),ResultCodeEnum.USER_LIKES_ERROR);
            ModelLikeDO build =  ModelLikeDO.builder()
                    .modelId(modelId)
                    .uid(uid)
                    .build();
            return mapper.insertModelUserLikes(
                    build
            )>0 &&
                    mapper.update(null, new UpdateWrapper<ModelDO>().eq("id",modelId).setSql("likes_num = likes_num+1"))>0;
        }else if(ModelConstant.UN_FLAG.equals(status)) {
            if(modelDO.getLikesNum()==0){
                return true;
            }
            return mapper.delModelLikes(uid,modelId)==1 &&
                    mapper.update(null,new UpdateWrapper<ModelDO>().eq("id",modelId).setSql("likes_num = likes_num - 1"))==1;
        }
        throw new BaseException(ResultCodeEnum.PARAMS_ERROR);
    }

    @Override
    public Boolean userCollectionModel(String status, String modelId, String uid) {
        ModelDO modelDO = mapper.selectById(modelId);
        AbstractAssert.isNull(modelDO,ResultCodeEnum.QUERY_MODEL_FAIL);
        if(ModelConstant.FLAG.equals(status)){
            AbstractAssert.notNull(mapper.queryUserModelCollection(uid,modelId),ResultCodeEnum.USER_COLLECTION_ERROR);
            ModelCollectionDO build = ModelCollectionDO.builder()
                    .modelId(modelId)
                    .uid(uid)
                    .build();
            return mapper.insertModelUserCollection(
                    build)>0 &&
                    mapper.update(null,new UpdateWrapper<ModelDO>().setSql("collection_num = collection_num +1"))>0;
        }else if(ModelConstant.UN_FLAG.equals(status)){
            if(modelDO.getCollectionNum()==0){
                return true;
            }
            return mapper.delModelCollection(uid,modelId)>0 &&
                    mapper.update(null,new UpdateWrapper<ModelDO>().setSql("collection_num = collection_num - 1"))>0;
        }
        throw new BaseException(ResultCodeEnum.PARAMS_ERROR);
    }

    @Override
    public List<LabelVO> getLabelList(Long limit,Long page) {
        long systemPage = Long.parseLong(systemConfig.getPageSize());
        limit = limit==null? systemPage :limit>systemPage? systemPage :limit;
        QueryWrapper<LabelDO> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("hot");
        Page<LabelDO> label = labelMapper.selectPage(new Page<>(page, limit, false), wrapper);
        return label.getRecords().stream()
                .map(labelDO -> new LabelVO(labelDO.getId().toString(), labelDO.getLabel()))
                .collect(Collectors.toList());
    }

//    @Override
//    public List<ModelFileVO> getModelFies(String modelId) {
//        AbstractAssert.isNull(mapper.selectById(modelId),ResultCodeEnum.QUERY_MODEL_FAIL);
//        ModelFileDO modelFileDO = mapper.queryModelFile(modelId);
//        com.tml.pojo.Result<String> index = fileServiceClient.downloadModel
//                (DownloadModelForm.builder().fileId(modelFileDO.getIndexFileId()).build());
//        ModelFileVO modelFileVO1 = new ModelFileVO();
//        modelFileVO1.setId(modelFileDO.getModelId());
//        modelFileVO1.setFileName(".index");
//        modelFileVO1.setUrl(index.getData());
//        com.tml.pojo.Result<String> pth = fileServiceClient.downloadModel(DownloadModelForm.builder().fileId(modelFileDO.getPthFileId()).build());
//        ModelFileVO modelFileVO2 = new ModelFileVO();
//        modelFileVO2.setId(modelFileDO.getModelId());
//        modelFileVO2.setFileName(".pth");
//        modelFileVO2.setUrl(pth.getData());
//        return List.of(modelFileVO1,modelFileVO2);
//    }

    @Override
    public List<TypeDO> queryTypeList() {
        return typeMapper.selectList(null);
    }

    private Page<FirstCommentVO> getFirstComment(QueryWrapper<CommentDO> queryWrapper,Long page,Long limit,String uid){
        long systemPage = Long.parseLong(systemConfig.getPageSize());
        limit = limit==null? systemPage :limit>systemPage? systemPage :limit;
        Page<CommentDO> commentDOPage = commentMapper.selectPage(new Page<>(page, limit, false), queryWrapper);
        List<FirstCommentVO> firstCommentVOList = commentDOPage.getRecords().stream()
                .map(commentDO -> convertToFirstCommentVO(commentDO, uid))
                .collect(Collectors.toList());
        return new Page<FirstCommentVO>().setRecords(firstCommentVOList).setSize(firstCommentVOList.size());
    }

    private FirstCommentVO convertToFirstCommentVO(CommentDO commentDO,String uid){
        FirstCommentVO.FirstCommentVOBuilder builder = FirstCommentVO.builder();
        if(uid==null||"".equals(uid)){
            builder.isLikes(false);
        }else{
            builder.isLikes(commentMapper.selectDOById(commentDO.getId().toString(),uid)!=null);
        }
        String commentUid = commentMapper.queryUidByCommentId(commentDO.getId().toString());
        io.github.common.web.Result<UserInfoVO> userInfo = userServiceClient.one(commentUid);
        AbstractAssert.isNull(userInfo.getData(), ResultCodeEnum.GET_USER_INFO_FAIL);
        builder.uid(commentUid)
                .id(commentDO.getId().toString())
                .commentTime(commentDO.getCreateTime())
                .content(commentDO.getContent())
                .likesNum(commentDO.getLikesNum())
                .modelId(commentDO.getModelId())
                .picture(userInfo.getData().getAvatar())
                .nickname(userInfo.getData().getNickname());
        return builder.build();
    }

    private SecondCommentVO convertToSecondCommentVO(CommentDO commentDO,String uid){
        SecondCommentVO.SecondCommentVOBuilder builder = SecondCommentVO.builder();
        if(uid==null||"".equals(uid)){
            builder.isLikes("0");
        }else {
            builder.isLikes(commentMapper.selectDOById(commentDO.getId().toString(),uid)==null?"0":"1");
        }
        String commentUid = commentMapper.queryUidByCommentId(commentDO.getId().toString());
        io.github.common.web.Result<UserInfoVO> userInfo = userServiceClient.one(commentUid);
        AbstractAssert.isNull(userInfo.getData(), ResultCodeEnum.GET_USER_INFO_FAIL);
        builder
                .id(commentDO.getId().toString())
                .uid(commentUid)
                .parentId(commentDO.getParentId())
                .commentTime(commentDO.getCreateTime())
                .content(commentDO.getContent())
                .likesNum(commentDO.getLikesNum())
                .picture(userInfo.getData().getAvatar())
                .nickname(userInfo.getData().getNickname());
        return builder.build();
    }

    private ModelVO convertToModelVO(ModelDO model, String myUid, UserInfoVO userInfoVO) {
        ModelVO modelVO;
        Callable<List<LabelVO>> labelTask = () -> labelMapper.selectListById(model.getId().toString());
        Callable<String> typeTask = () -> typeMapper.selectById(model.getTypeId()).getType();
        Callable<String> likeTask = () -> mapper.queryUserModelLikes(myUid,model.getId().toString())==null?"0":"1";
        Callable<String> collectionTask = () -> mapper.queryUserModelCollection(myUid,model.getId().toString())==null?"0":"1";

        Future<String> typeFuture = ConcurrentUtil.doJob(executorService, typeTask);
        Future<String> likeFuture = ConcurrentUtil.doJob(executorService, likeTask);
        Future<String> collectionFuture = ConcurrentUtil.doJob(executorService, collectionTask);
        Future<List<LabelVO>> labelFuture = ConcurrentUtil.doJob(executorService, labelTask);

        AbstractAssert.isBlank(ConcurrentUtil.futureGet(typeFuture),ResultCodeEnum.GET_TYPE_ERROR);
        AbstractAssert.isNull(ConcurrentUtil.futureGet(likeFuture),ResultCodeEnum.SYSTEM_ERROR);
        AbstractAssert.isNull(ConcurrentUtil.futureGet(collectionFuture),ResultCodeEnum.SYSTEM_ERROR);

        String type = ConcurrentUtil.futureGet(typeFuture);
        String isLike = ConcurrentUtil.futureGet(likeFuture);
        String isCollection = ConcurrentUtil.futureGet(collectionFuture);
        List<LabelVO> labelList = ConcurrentUtil.futureGet(labelFuture);

        if(userInfoVO==null){
            Result<UserInfoVO> userInfo = userServiceClient.one(myUid);
            userInfoVO = userInfo.getData();
            AbstractAssert.isNull(userInfoVO,ResultCodeEnum.GET_USER_INFO_FAIL);
        }
        try {
            modelVO = ModelVO.modelDOToModelVO(model,userInfoVO,labelList,type,isLike,isCollection);
        }catch (RuntimeException e){
            logger.error(e);
            throw new BaseException(ResultCodeEnum.GET_USER_INFO_FAIL);
        }
        return modelVO;
    }

    private Page<SecondCommentVO> getSecondCommentList(QueryWrapper<CommentDO> queryWrapper,Long page,Long limit,String uid){
        long systemPage = Long.parseLong(systemConfig.getPageSize());
        limit = limit==null? systemPage :limit>systemPage? systemPage :limit;
        Page<CommentDO> commentDOPage = commentMapper.selectPage(new Page<>(page, limit, false), queryWrapper);
        List<SecondCommentVO> secondCommentVOS = commentDOPage.getRecords().stream()
                .map(commentDO -> convertToSecondCommentVO(commentDO, uid))
                .collect(Collectors.toList());
        return new Page<SecondCommentVO>().setRecords(secondCommentVOS).setSize(secondCommentVOS.size());
    }

    private Page<ModelVO> getModelListCommon(QueryWrapper<ModelDO> wrapper, Long page, Long size, String uid) {
        long systemPage = Long.parseLong(systemConfig.getPageSize());
        size = size==null? systemPage :size > systemPage ? systemPage :size;
        //todo:建索引优化
        Page<ModelDO> modelPage = mapper.selectPage(new Page<>(page, size,false), wrapper);
        List<Long> modelIds = modelPage.getRecords().stream().map(ModelDO::getId).collect(Collectors.toList());
        List<String> uids = modelUserMapper.queryUidByModelIds(modelIds);
        try {
            Result<Map<String, UserInfoVO>> result = userServiceClient.list(uids);
            Map<String, UserInfoVO> userInfo = result.getData();
            List<ModelVO> modelVOList = IntStream.range(0, modelPage.getRecords().size())
                    .mapToObj(i -> convertToModelVO(modelPage.getRecords().get(i), uid, userInfo.get(uids.get(i))))
                    .collect(Collectors.toList());
            return new Page<ModelVO>().setRecords(modelVOList).setSize(modelVOList.size());
        }catch (RuntimeException e){
            throw new BaseException(e.toString());
        }
    }

}
