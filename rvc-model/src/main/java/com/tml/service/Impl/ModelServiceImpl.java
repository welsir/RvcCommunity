package com.tml.service.Impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tml.common.DetectionStatusEnum;
import com.tml.common.Result;
import com.tml.common.constant.ModelConstant;
import com.tml.common.exception.AbstractAssert;
import com.tml.common.exception.BaseException;
import com.tml.common.log.AbstractLogger;
import com.tml.config.SystemConfig;
import com.tml.core.async.AsyncService;
import com.tml.core.client.FileServiceClient;
import com.tml.core.client.UserServiceClient;
import com.tml.core.rabbitmq.ModelListener;
import com.tml.mapper.*;
import com.tml.pojo.DO.*;

import com.tml.pojo.DTO.*;
import com.tml.pojo.ResultCodeEnum;
import com.tml.pojo.VO.*;
import com.tml.service.ModelService;
import com.tml.utils.ConcurrentUtil;
import com.tml.utils.DateUtil;
import com.tml.utils.FileUtil;
import com.tml.utils.WrapperUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.tml.common.DetectionStatusEnum.UN_DETECTION;
import static com.tml.pojo.ResultCodeEnum.MODEL_FILE_ILLEGAL;

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
    @Resource
    ModelListener listener;
    @Resource
    SystemConfig systemConfig;
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
    public Page<ModelVO> getModelList(String size, String page,String sortType,String uid) {
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
    public Page<ModelVO> getModelList(String typeId,String page,String size,String order,String uid) {
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
        try {
            ModelDO model = mapper.selectById(modelId);
            AbstractAssert.isNull(model,ResultCodeEnum.MODEL_NOT_EXITS);
            Callable<String> typeTask = () -> typeMapper.selectTypeById(model.getTypeId());
            Callable<String> likeTask = () -> mapper.queryUserModelLikes(uid, modelId) == null ? "0" : "1";
            Callable<String> collectionTask = () -> mapper.queryUserModelCollection(uid, modelId) == null ? "0" : "1";
            Callable<List<String>> labelTask = () -> labelMapper.selectListById(modelId);

            Future<String> typeFuture = ConcurrentUtil.doJob(executorService, typeTask);
            String type = ConcurrentUtil.futureGet(typeFuture);
            Future<String> likeFuture = ConcurrentUtil.doJob(executorService, likeTask);
            String isLike = ConcurrentUtil.futureGet(likeFuture);
            Future<String> collectionFuture = ConcurrentUtil.doJob(executorService, collectionTask);
            String isCollection = ConcurrentUtil.futureGet(collectionFuture);
            Future<List<String>> labelFuture = ConcurrentUtil.doJob(executorService, labelTask);
            List<String> labelList = ConcurrentUtil.futureGet(labelFuture);

            UserInfoDTO dto = null;
            if(uid!=null){
                Result<UserInfoDTO> userInfo = userServiceClient.getUserInfo(uid);
                dto = userInfo.getData();
                AbstractAssert.isNull(dto,ResultCodeEnum.GET_USER_INFO_FAIL);
            }
            ModelVO modelVO = ModelVO.modelDOToModelVO(model, dto,labelList, type, isLike,isCollection);
            asyncService.asyncAddModelViewNums(modelId);
            return modelVO;
        }catch (RuntimeException e){
            logger.error(e);
            throw new BaseException(ResultCodeEnum.QUERY_MODEL_FAIL);
        }
    }

    @Transactional
    @Override
    public void insertOneModel(ModelInsertVO model,String uid) {
        //todo:需要等待用户模块提供查询用户是否存在接口
        AbstractAssert.isNull(typeMapper.selectTypeById(model.getTypeId()),ResultCodeEnum.TYPE_NOT_EXIT);
        if(model.getLabelId()!=null&&!model.getLabelId().isEmpty()){
            AbstractAssert.isNull(labelMapper.getLabels(model.getLabelId()),ResultCodeEnum.LABEL_NOT_EXIT);
        }
        ModelDO modelDO = new ModelDO();
        BeanUtils.copyProperties(model,modelDO);
        modelDO.setUpdateTime(DateUtil.formatDate());
        modelDO.setCreateTime(DateUtil.formatDate());
        modelDO.setLikesNum("0");
        modelDO.setCollectionNum("0");
        modelDO.setViewNum("0");
        modelDO.setHasShow(String.valueOf(DetectionStatusEnum.UN_DETECTION.getStatus()));
        mapper.insert(modelDO);
        AbstractAssert.isBlank(modelDO.getId().toString(),ResultCodeEnum.ADD_MODEL_FAIL);
        if(model.getLabelId()!=null&&!model.getLabelId().isEmpty()){
            try{
                labelMapper.insertLabel(modelDO.getId().toString(),model.getLabelId());
            }catch (RuntimeException e){
                logger.error(e);
                throw new BaseException(ResultCodeEnum.ADD_MODEL_LABEL_FAIL);
            }
        }
        String typeId = model.getTypeId();
        typeMapper.insertModelType(modelDO.getId().toString(),typeId);
        ModelUserDO modelUserDO = new ModelUserDO();
        modelUserDO.setModelId(String.valueOf(modelDO.getId()));
        modelUserDO.setUid(uid);
        try {
            modelUserMapper.insert(modelUserDO);
        }catch (RuntimeException e){
            logger.error(e);
            throw new BaseException(ResultCodeEnum.INSERT_MODEL_USER_RELATIVE_FAIL);
        }
        asyncService.processModelAsync(modelDO);
    }

    @Override
    public String downloadModel(String modelId,String uid) {
        //todo:需要等待用户模块提供查询用户是否存在接口

        Result<String> result = fileServiceClient.downloadModel(
                DownloadModelForm.builder().fileId(modelId).isPrivate("true").bucket(ModelConstant.DEFAULT_BUCKET).build());
        return result.getData();
    }

    @Override
    public Boolean editModelMsg(ModelUpdateFormVO modelUpdateFormVO,String uid) {
        //todo:需要等待用户模块提供查询用户是否存在接口
        AbstractAssert.isNull(mapper.selectById(modelUpdateFormVO.getId()),ResultCodeEnum.MODEL_NOT_EXITS);
        String name = ModelConstant.SERVICE_NAME + "-com.tml.pojo.DO.ModelDO";
        List<DetectionTaskDTO> dtos = Arrays.asList(
                DetectionTaskDTO.createDTO(modelUpdateFormVO.getId(), modelUpdateFormVO.getDescription(), name),
                DetectionTaskDTO.createDTO(modelUpdateFormVO.getId(), modelUpdateFormVO.getName(), name),
                DetectionTaskDTO.createDTO(modelUpdateFormVO.getId(), modelUpdateFormVO.getNote(), name),
                DetectionTaskDTO.createDTO(modelUpdateFormVO.getId(), modelUpdateFormVO.getPicture(), name)
        );
        List<String> types = Arrays.asList(
                ModelConstant.TEXT_TYPE,
                ModelConstant.TEXT_TYPE,
                ModelConstant.TEXT_TYPE,
                ModelConstant.IMAGE_TYPE
        );
        List<AsyncDetectionForm> forms = IntStream.range(0, dtos.size())
                .mapToObj(i -> DetectionTaskDTO.createAsyncDetectionForm(dtos.get(i), types.get(i)))
                .collect(Collectors.toList());
        HashMap<String, String> map = new HashMap<>();
        map.put("description",modelUpdateFormVO.getDescription());
        map.put("name",modelUpdateFormVO.getName());
        map.put("note",modelUpdateFormVO.getNote());
        map.put("picture",modelUpdateFormVO.getPicture());
        listener.setMap(modelUpdateFormVO.getId(),map);
        asyncService.listenerMq(forms);
        return true;
    }

    //todo:需要重新判断文件逻辑(.index and .pth（可单文件上传）)
    @Override
    public ReceiveUploadFileDTO uploadModel(MultipartFile file,String uid) {
        //todo:需要等待用户模块提供查询用户是否存在接口
        if(FileUtil.checkModelFileIsAvailable(file)){
            throw new BaseException(ResultCodeEnum.MODEL_FILE_ILLEGAL);
        }
        try {
            UploadModelForm form = UploadModelForm.builder()
                    .file(file)
                    .path(ModelConstant.DEFAULT_MODEL_PATH)
                    .bucket(ModelConstant.DEFAULT_BUCKET)
                    .md5(FileUtil.getMD5Checksum(file.getInputStream()))
                    .build();
            Result<ReceiveUploadFileDTO> res = fileServiceClient.uploadModel(form);
            return res.getData();
        } catch (NoSuchAlgorithmException | IOException e) {
            logger.error("%s:"+e.getStackTrace()[0],e);
            throw new BaseException(e.toString());
        }
    }

    @Override
    public ReceiveUploadFileDTO uploadImage(MultipartFile file,String uid) {
        //todo:需要等待用户模块提供查询用户是否存在接口
        if(FileUtil.isImageFile(file.getOriginalFilename())&&!FileUtil.imageSizeIsAvailable(file)){
            try {
                UploadModelForm form = UploadModelForm.builder()
                        .file(file)
                        .path(ModelConstant.DEFAULT_MODEL_PATH)
                        .bucket(ModelConstant.DEFAULT_BUCKET)
                        .md5(FileUtil.getMD5Checksum(file.getInputStream()))
                        .build();
                Result<ReceiveUploadFileDTO> res = fileServiceClient.uploadModel(form);
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
    public String insertLabel(String label, String uid) {
        //todo:需要等待用户模块提供查询用户是否存在接口

        LabelDO labelDO = new LabelDO();
        try {
            labelDO.setLabel(label);
            labelDO.setCreateTime(DateUtil.formatDate());
            labelDO.setHasShow(UN_DETECTION.getStatus().toString());
            labelMapper.insert(labelDO);
            DetectionTaskDTO dto = DetectionTaskDTO.builder()
                    .id(String.valueOf(labelDO.getId()))
                    .name("model-com.tml.pojo.DO.LabelDO").content(labelDO.getLabel()).
                    build();
            AsyncDetectionForm form = new AsyncDetectionForm();
            form.setTaskDTO(dto);
            form.setType("text");
            asyncService.listenerMq(List.of(form));
        }catch (RuntimeException e){
            logger.error(e);
            throw new BaseException(ResultCodeEnum.ADD_MODEL_LABEL_FAIL);
        }
        return String.valueOf(labelDO.getId());
    }

    @Override
    public List<UserLikesModelVO> getUserLikesList(String uid) {
        //todo:需要等待用户模块提供查询用户是否存在接口

        List<ModelLikeDO> modelLikeDOList = mapper.getUserLikesModel(uid);
        List<String> modelIds = modelLikeDOList.stream()
                .map(ModelLikeDO::getModelId)
                .collect(Collectors.toList());

        Map<Long, ModelDO> modelDOMap = mapper.selectBatchIds(modelIds)
                .stream()
                .collect(Collectors.toMap(ModelDO::getId, Function.identity()));

        List<UserLikesModelVO> list = new ArrayList<>();
        for (ModelLikeDO modelLikeDO : modelLikeDOList) {
            ModelDO modelDO = modelDOMap.get(Long.parseLong(modelLikeDO.getModelId()));
            if (modelDO != null) {
                UserLikesModelVO modelVO = new UserLikesModelVO();
                modelVO.setId(modelDO.getId().toString());
                modelVO.setName(modelDO.getName());
                modelVO.setPicture(modelDO.getPicture());
                modelVO.setLikesNum(modelDO.getLikesNum());
                modelVO.setCollectionNum(modelDO.getCollectionNum());
                list.add(modelVO);
            }
        }
        return list;
    }

    @Override
    public List<UserCollectionModelVO> getUserCollectionList(String uid) {
        List<ModelCollectionDO> modelCollectionDOList = mapper.getUserCollectionModel(uid);
        List<String> modelIds = modelCollectionDOList.stream()
                .map(ModelCollectionDO::getModelId)
                .collect(Collectors.toList());
        Map<Long, ModelDO> modelDOMap = mapper.selectBatchIds(modelIds)
                .stream()
                .collect(Collectors.toMap(ModelDO::getId, Function.identity()));

        List<UserCollectionModelVO> list = new ArrayList<>();
        for (ModelCollectionDO modelCollectionDO : modelCollectionDOList) {
            ModelDO modelDO = modelDOMap.get(Long.parseLong(modelCollectionDO.getModelId()));
            if (modelDO != null) {
                UserCollectionModelVO modelVO = new UserCollectionModelVO();
                modelVO.setName(modelDO.getName());
                modelVO.setPicture(modelDO.getPicture());
                modelVO.setLikesNum(modelDO.getLikesNum());
                modelVO.setCollectionNum(modelDO.getCollectionNum());
                list.add(modelVO);
            }
        }
        return list;
    }

    @Transactional
    @Override
    public Boolean delSingleModel(String modelId,String uid) {
        //todo:需要等待用户模块提供查询用户是否存在接口
        AbstractAssert.isTrue(!Objects.equals(modelUserMapper.selectById(modelId).getUid(), uid),ResultCodeEnum.QUERY_MODEL_FAIL);
        UpdateWrapper<ModelDO> wrapper = new UpdateWrapper<>();
        wrapper.eq("id",modelId);
        wrapper.set("has_delete",ModelConstant.DELETE);
        mapper.deleteLikesByModelId(modelId);
        mapper.deleteCollectionByModelId(modelId);
        return mapper.update(null,wrapper)==1;
    }


    @Override
    public Page<ModelVO> queryUserModelList(String uid,String page,String limit) {
        //todo:需要等待用户模块提供查询用户是否存在接口

        List<String> modelIds = modelUserMapper.selectModelIdByUid(uid);
        long maxLimit = limit ==null?Long.parseLong(systemConfig.getPageSize()):Math.min(Long.parseLong(limit), Long.parseLong(systemConfig.getPageSize()));
        QueryWrapper<ModelDO> wrapper = new QueryWrapper<>();
        wrapper.in("id",modelIds);
        wrapper = WrapperUtil.setWrappers(wrapper,Map.of("sort",""));
        Page<ModelDO> modelPage = mapper.selectPage(new Page<>(Long.parseLong(page), maxLimit,false),wrapper);
        List<ModelVO> modelVOList = modelPage.getRecords().stream()
                .map(modelDO -> convertToModelVO(modelDO,uid))
                .collect(Collectors.toList());
        return new Page<ModelVO>().setRecords(modelVOList);
    }

    @Transactional
    @Override
    public String commentModel(CommentFormVO commentFormVO,String uid) {
        //todo:需要等待用户模块提供查询用户是否存在接口

        AbstractAssert.isNull(mapper.selectById(commentFormVO.getModelId()),ResultCodeEnum.MODEL_NOT_EXITS);
        if(StringUtils.isBlank(commentFormVO.getReplyId())){
            AbstractAssert.notNull(commentMapper.selectById(commentFormVO.getReplyId()),ResultCodeEnum.COMMENT_NOT_EXITS);
        }else {
            AbstractAssert.isNull(commentMapper.selectById(commentFormVO.getReplyId()),ResultCodeEnum.COMMENT_NOT_EXITS);
        }
        try {
            CommentDO commentDO = new CommentDO();
            commentDO.setContent(commentFormVO.getContent());
            commentDO.setModelId(commentFormVO.getModelId());
            commentDO.setUid(uid);
            commentDO.setHasShow(UN_DETECTION.getStatus().toString());
            commentDO.setUpdateTime(DateUtil.formatDate());
            commentDO.setCreateTime(DateUtil.formatDate());
            commentDO.setParentId(commentFormVO.getReplyId());
            commentDO.setLikesNum("0");
            commentMapper.insert(commentDO);
            if(commentDO.getParentId()==null||"".equals(commentDO.getParentId())){
                commentMapper.insertFirstModelComment(commentDO.getModelId(),commentDO.getId().toString());
            }
            DetectionTaskDTO dto = DetectionTaskDTO.builder()
                    .id(commentDO.getId().toString())
                    .content(commentFormVO.getContent())
                    .name(ModelConstant.SERVICE_NAME + "-com.tml.pojo.DO.CommentDO").build();
            AsyncDetectionForm form = new AsyncDetectionForm();
            form.setTaskDTO(dto);
            form.setType(ModelConstant.TEXT_TYPE);
            asyncService.listenerMq(List.of(form));
            return commentDO.getId().toString();
        }catch (RuntimeException e){
            logger.error(e);
            throw new BaseException(ResultCodeEnum.ADD_COMMENT_FAIL);
        }
    }

    @Transactional
    @Override
    public Boolean likeComment(String uid, String commentId,String type) {
        //todo:需要等待用户模块提供查询用户是否存在接口
        AbstractAssert.isNull(commentMapper.selectById(commentId),ResultCodeEnum.COMMENT_NOT_EXITS);
        if(ModelConstant.FLAG.equals(type)){
            AbstractAssert.notNull(commentMapper.selectDOById(commentId,uid),ResultCodeEnum.USER_LIKES_ERROR);
            UpdateWrapper<CommentDO> wrapper = new UpdateWrapper<>();
            wrapper.eq("uid",uid).eq("id",commentId).setSql("likes_num = likes_num+1");
            commentMapper.update(null,wrapper);
            return commentMapper.insertUserCommentRelative(commentId,uid)==1;
        }
        if(ModelConstant.UN_FLAG.equals(type)){
            commentMapper.delUserCommentLikes(commentId,uid);
            return true;
        }
        throw new BaseException(ResultCodeEnum.PARAMS_ERROR);
    }

    @Override
    public Page<FirstCommentVO> queryFirstCommentList(String modelId, String page, String limit, String sortType,String uid) {
        AbstractAssert.isNull(mapper.selectById(modelId),ResultCodeEnum.MODEL_NOT_EXITS);
        List<String> firsComments  = commentMapper.queryCommentIds(modelId);
        QueryWrapper<CommentDO> wrapper = new QueryWrapper<>();
        wrapper.in("id",firsComments);
        wrapper = WrapperUtil.setWrappers(wrapper,Map.of("has_show",DetectionStatusEnum.DETECTION_SUCCESS.getStatus().toString(),"sort",""));
        return getFirstComment(wrapper,page,limit,uid);
    }

    @Override
    public Page<SecondCommentVO> querySecondCommentList(String parentCommentId,String page,String limit,String sortType, String uid) {
        List<String> secondComments  = commentMapper.querySecondComments(parentCommentId);
        QueryWrapper<CommentDO> wrapper = new QueryWrapper<>();
        wrapper.in("id",secondComments);
        wrapper = WrapperUtil.setWrappers(wrapper,Map.of("has_show",DetectionStatusEnum.DETECTION_SUCCESS.getStatus().toString(),"sort",sortType));
        return getSecondCommentList(wrapper,page,limit,uid);
    }

    @Override
    public Boolean userLikesModel(String status, String modelId, String uid) {
        //todo:需要等待用户模块提供查询用户是否存在接口
        AbstractAssert.isNull(mapper.selectById(modelId),ResultCodeEnum.QUERY_MODEL_FAIL);
        if(ModelConstant.FLAG.equals(status)){
            AbstractAssert.notNull(mapper.queryUserModelLikes(uid,modelId),ResultCodeEnum.USER_LIKES_ERROR);
            ModelLikeDO build =  ModelLikeDO.builder()
                    .modelId(modelId)
                    .uid(uid)
                    .build();
            return mapper.insertModelUserLikes(
                    build
            )==1;
        }else if(ModelConstant.UN_FLAG.equals(status)) {
            return mapper.delModelLikes(uid,modelId)==1;
        }
        throw new BaseException(ResultCodeEnum.PARAMS_ERROR);
    }

    @Override
    public Boolean userCollectionModel(String status, String modelId, String uid) {
        //todo:需要等待用户模块提供查询用户是否存在接口
        AbstractAssert.isNull(mapper.selectById(modelId),ResultCodeEnum.QUERY_MODEL_FAIL);
        if(ModelConstant.FLAG.equals(status)){
            AbstractAssert.notNull(mapper.queryUserModelCollection(uid,modelId),ResultCodeEnum.USER_COLLECTION_ERROR);
            ModelCollectionDO build = ModelCollectionDO.builder()
                    .modelId(modelId)
                    .uid(uid)
                    .build();
            return mapper.insertModelUserCollection(
                    build)==1;
        }else if(ModelConstant.UN_FLAG.equals(status)){
            return mapper.delModelCollection(uid,modelId)==1;
        }
        throw new BaseException(ResultCodeEnum.PARAMS_ERROR);
    }

    private Page<FirstCommentVO> getFirstComment(QueryWrapper<CommentDO> queryWrapper,String page,String limit,String uid){
        limit = (limit==null|| "".equals(limit))? systemConfig.getPageSize():Long.parseLong(limit)>Long.parseLong(systemConfig.getPageSize())?systemConfig.getPageSize():limit;
        Page<CommentDO> commentDOPage = commentMapper.selectPage(new Page<>(Long.parseLong(page), Long.parseLong(limit), false), queryWrapper);
        List<FirstCommentVO> firstCommentVOList = commentDOPage.getRecords().stream()
                .map(commentDO -> convertToFirstCommentVO(commentDO, uid))
                .collect(Collectors.toList());
        return new Page<FirstCommentVO>().setRecords(firstCommentVOList).setSize(firstCommentVOList.size());
    }

    private FirstCommentVO convertToFirstCommentVO(CommentDO commentDO,String uid){
        FirstCommentVO.FirstCommentVOBuilder builder = FirstCommentVO.builder();
        if(uid==null||"".equals(uid)){
            builder.isLikes("0");
        }else{
            builder.isLikes(commentMapper.selectDOById(commentDO.getId().toString(),uid)==null?"0":"1");
        }
        String commentUid = commentMapper.queryUidByCommentId(commentDO.getId().toString());
        Result<UserInfoDTO> userInfo = userServiceClient.getUserInfo(commentUid);
        builder.uid(commentUid)
                .id(commentDO.getId().toString())
                .commentTime(commentDO.getCreateTime())
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
        Result<UserInfoDTO> userInfo = userServiceClient.getUserInfo(commentUid);
        builder.uid(commentUid)
                .parentId(commentDO.getParentId())
                .commentTime(commentDO.getCreateTime())
                .content(commentDO.getContent())
                .likesNum(commentDO.getLikesNum())
                .picture(userInfo.getData().getAvatar())
                .nickname(userInfo.getData().getNickname());
        return builder.build();
    }

    private ModelVO convertToModelVO(ModelDO model,String myUid) {
        ModelVO modelVO;
        String uid;
        Result<UserInfoDTO> userInfo;
        Callable<List<String>> labelTask = () -> labelMapper.selectListById(model.getId().toString());
        Callable<String> typeTask = () -> typeMapper.selectById(model.getTypeId()).getType();
        Callable<String> likeTask = () -> mapper.queryUserModelLikes(myUid,model.getId().toString())==null?"0":"1";
        Callable<String> collectionTask = () -> mapper.queryUserModelCollection(myUid,model.getId().toString())==null?"0":"1";

        Future<String> typeFuture = ConcurrentUtil.doJob(executorService, typeTask);
        Future<String> likeFuture = ConcurrentUtil.doJob(executorService, likeTask);
        Future<String> collectionFuture = ConcurrentUtil.doJob(executorService, collectionTask);
        Future<List<String>> labelFuture = ConcurrentUtil.doJob(executorService, labelTask);

        AbstractAssert.isBlank(ConcurrentUtil.futureGet(typeFuture),ResultCodeEnum.GET_TYPE_ERROR);
        AbstractAssert.isNull(ConcurrentUtil.futureGet(likeFuture),ResultCodeEnum.SYSTEM_ERROR);
        AbstractAssert.isNull(ConcurrentUtil.futureGet(collectionFuture),ResultCodeEnum.SYSTEM_ERROR);

        String type = ConcurrentUtil.futureGet(typeFuture);
        String isLike = ConcurrentUtil.futureGet(likeFuture);
        String isCollection = ConcurrentUtil.futureGet(collectionFuture);
        List<String> labelList = ConcurrentUtil.futureGet(labelFuture);
        try {
            ModelUserDO modelUserDO = modelUserMapper.selectById(model.getId());
            uid = modelUserDO.getUid();
            userInfo = userServiceClient.getUserInfo(uid);
            AbstractAssert.isNull(userInfo,ResultCodeEnum.GET_USER_INFO_FAIL);
            modelVO = ModelVO.modelDOToModelVO(model,userInfo.getData(),labelList,type,isLike,isCollection);
        }catch (RuntimeException e){
            logger.error(e);
            throw new BaseException(ResultCodeEnum.GET_USER_INFO_FAIL);
        }
        return modelVO;
    }

    private Page<SecondCommentVO> getSecondCommentList(QueryWrapper<CommentDO> queryWrapper,String page,String limit,String uid){
        limit = (limit==null|| "".equals(limit))? systemConfig.getPageSize():Long.parseLong(limit)>Long.parseLong(systemConfig.getPageSize())?systemConfig.getPageSize():limit;
        Page<CommentDO> commentDOPage = commentMapper.selectPage(new Page<>(Long.parseLong(page), Long.parseLong(limit), false), queryWrapper);
        List<SecondCommentVO> secondCommentVOS = commentDOPage.getRecords().stream()
                .map(commentDO -> convertToSecondCommentVO(commentDO, uid))
                .collect(Collectors.toList());
        return new Page<SecondCommentVO>().setRecords(secondCommentVOS).setSize(secondCommentVOS.size());
    }

    private Page<ModelVO> getModelListCommon(QueryWrapper<ModelDO> wrapper, String page, String size, String uid) {
        size = (size==null|| "".equals(size))? systemConfig.getPageSize():Long.parseLong(size)>Long.parseLong(systemConfig.getPageSize())?systemConfig.getPageSize():size;
        Page<ModelDO> modelPage = mapper.selectPage(new Page<>(Long.parseLong(page), Long.parseLong(size),false), wrapper);
        List<ModelVO> modelVOList = modelPage.getRecords().stream()
                .map(modelDO -> convertToModelVO(modelDO,uid))
                .collect(Collectors.toList());
        return new Page<ModelVO>().setRecords(modelVOList).setSize(modelVOList.size());
    }

}
