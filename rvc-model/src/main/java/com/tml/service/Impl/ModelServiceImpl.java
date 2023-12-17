package com.tml.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tml.common.DetectionStatusEnum;
import com.tml.common.Result;
import com.tml.common.constant.ModelConstant;
import com.tml.common.exception.BaseException;
import com.tml.common.log.AbstractLogger;
import com.tml.core.async.AsyncService;
import com.tml.core.client.FileServiceClient;
import com.tml.core.client.UserServiceClient;
import com.tml.core.rabbitmq.ModelListener;
import com.tml.mapper.LabelMapper;
import com.tml.mapper.ModelMapper;
import com.tml.mapper.ModelUserMapper;
import com.tml.mapper.TypeMapper;
import com.tml.pojo.DO.*;

import com.tml.pojo.DTO.*;
import com.tml.pojo.ResultCodeEnum;
import com.tml.pojo.VO.*;
import com.tml.service.ModelService;
import com.tml.utils.DateUtil;
import com.tml.utils.FileUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    FileServiceClient fileServiceClient;
    @Resource
    UserServiceClient userServiceClient;
    @Resource
    AbstractLogger logger;
    @Resource
    FileUtil fileUtil;
    @Resource
    AsyncService asyncService;
    @Resource
    ModelListener listener;
    @Resource
    DateUtil dateUtil;

    @Override
    public Page<ModelVO> getModelList(String size, String page,String sortType,String uid) {
        try {
            QueryWrapper<ModelDO> queryWrapper = new QueryWrapper<ModelDO>()
                    .eq("has_show", DetectionStatusEnum.DETECTION_SUCCESS.getStatus())
                    .eq("has_delete",ModelConstant.UN_DELETE);
            setSortingCriteria(queryWrapper, sortType);
            return getModelListCommon(queryWrapper, page, size, uid);
        }catch (BaseException e){
            throw new BaseException(ResultCodeEnum.QUERY_MODEL_LIST_FAIL);
        }
    }

    @Override
    public Page<ModelVO> getModelList(String type,String size,String page,String sortType,String uid) {
        try {
            QueryWrapper<ModelDO> queryWrapper = new QueryWrapper<ModelDO>()
                    .eq("has_show", DetectionStatusEnum.DETECTION_SUCCESS.getStatus())
                    .eq("has_delete",ModelConstant.UN_DELETE)
                    .eq("type_id",type);
            setSortingCriteria(queryWrapper, sortType);
            return getModelListCommon(queryWrapper, page, size, uid);
        }catch (BaseException e){
            throw new BaseException(ResultCodeEnum.QUERY_MODEL_LIST_FAIL);
        }
    }

    @Override
    public ModelVO queryOneModel(String modelId, String uid) {
        try {
            ModelDO model = mapper.selectById(modelId);
            if(model==null){
                throw new BaseException(ResultCodeEnum.QUERY_MODEL_FAIL);
            }
            ModelVO modelVO = ModelVO.builder().build();
            BeanUtils.copyProperties(model,modelVO);
            modelVO.setId(String.valueOf(model.getId()));
            modelVO.setType(typeMapper.selectById(model.getTypeId()).getType());
            List<String> labelList = labelMapper.selectListById(String.valueOf(model.getId()));
            if(labelList!=null){
                List<String> strings = new ArrayList<>();
                for (String s : labelList) {
                    String label = labelMapper.selectById(s).getLabel();
                    strings.add(label);
                    modelVO.setLabel(strings);
                }
            }else {
                modelVO.setLabel(null);
            }
            modelVO.setIsLike(mapper.queryUserModelLikes(uid,modelId)==null?"0":"1");
            modelVO.setIsCollection(mapper.queryUserModelCollection(uid,modelId)==null?"0":"1");
            Result<UserInfoDTO> userInfo = userServiceClient.getUserInfo(uid);
            UserInfoDTO dto = userInfo.getData();
            if(dto==null){
                throw new BaseException(ResultCodeEnum.GET_USER_INFO_FAIL);
            }
            modelVO.setUid(userInfo.getData().getUid());
            modelVO.setUsername(userInfo.getData().getUsername());
            modelVO.setNickname(userInfo.getData().getNickname());
            modelVO.setAvatar(userInfo.getData().getAvatar());
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
        ModelDO modelDO = new ModelDO();
        BeanUtils.copyProperties(model,modelDO);
        modelDO.setUpdateTime(dateUtil.formatDate());
        modelDO.setCreateTime(dateUtil.formatDate());
        modelDO.setLikesNum("0");
        modelDO.setCollectionNum("0");
        modelDO.setViewNum("0");
        modelDO.setHasShow(String.valueOf(DetectionStatusEnum.UN_DETECTION.getStatus()));
        int insert = mapper.insert(modelDO);
        if(insert!=1){
            throw new BaseException(ResultCodeEnum.ADD_MODEL_FAIL);
        }
        if(model.getLabelId()!=null){
            try{
                labelMapper.insertLabel(modelDO.getId().toString(),model.getLabelId());
            }catch (RuntimeException e){
                logger.error(e);
                throw new BaseException(ResultCodeEnum.ADD_MODEL_LABEL_FAIL);
            }
        }
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
    public String downloadModel(String modelId) {
        Result<String> result = fileServiceClient.downloadModel(
                DownloadModelForm.builder().fileId(modelId).isPrivate("true").bucket(ModelConstant.DEFAULT_BUCKET).build());
        return result.getData();
    }

    @Override
    public Boolean editModelMsg(ModelUpdateFormVO modelUpdateFormVO) {
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

    @Override
    public ReceiveUploadFileDTO uploadModel(MultipartFile file) {
        try {
            UploadModelForm form = UploadModelForm.builder()
                    .file(file)
                    .path(ModelConstant.DEFAULT_MODEL_PATH)
                    .bucket(ModelConstant.DEFAULT_BUCKET)
                    .md5(fileUtil.getMD5Checksum(file.getInputStream()))
                    .build();
            Result<ReceiveUploadFileDTO> res = fileServiceClient.uploadModel(form);
            return res.getData();
        } catch (NoSuchAlgorithmException | IOException e) {
            logger.error("%s:"+e.getStackTrace()[0],e);
            throw new BaseException();
        }
    }

    @Override
    public ReceiveUploadFileDTO uploadImage(MultipartFile file) {
        if(fileUtil.isImageFile(file.getOriginalFilename())&&fileUtil.imageSizeIsAviable(file)){
            return this.uploadModel(file);
        }else{
            throw new BaseException(ResultCodeEnum.UPLOAD_IMAGE_FAIL);
        }
    }

    @Override
    public void insertRelative(String type, String modelId,String uid,String isClick) {
        if("collection".equals(type)){
            if("false".equals(isClick)){
                ModelCollectionDO build = ModelCollectionDO.builder()
                        .modelId(modelId)
                        .uid(uid)
                        .build();
                mapper.insertModelUserCollection(
                        build);
            }else{
                mapper.delModelCollection(uid,modelId);
            }
        }else {
            if("false".equals(isClick)){
                ModelLikeDO build =  ModelLikeDO.builder()
                        .modelId(modelId)
                        .uid(uid)
                        .build();
                mapper.insertModelUserLikes(
                        build
                );
            }else{
                mapper.delModelLikes(uid,modelId);
            }
        }
    }

    @Override
    public String insertLabel(String label, String uid) {
        Assert.notNull(uid,"用户未登录");
        LabelDO labelDO = new LabelDO();
        try {
            labelDO.setLabel(label);
            labelDO.setCreateTime(dateUtil.formatDate());
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
    public Boolean delSingleModel(String modelId) {
        UpdateWrapper<ModelDO> wrapper = new UpdateWrapper<>();
        wrapper.eq("id",modelId);
        wrapper.set("has_delete",ModelConstant.DELETE);
        mapper.deleteLikesByModelId(modelId);
        mapper.deleteCollectionByModelId(modelId);
        return mapper.update(null,wrapper)==1;
    }

    private ModelVO convertToModelVO(ModelDO model,String myUid) {
        ModelVO modelVO;
        String uid;
        try {
            ModelUserDO modelUserDO = modelUserMapper.selectById(model.getId());
            uid = modelUserDO.getUid();
            Result<UserInfoDTO> userInfo = userServiceClient.getUserInfo(uid);
            modelVO = ModelVO.modelDOToModelVO(model,userInfo.getData());
        }catch (RuntimeException e){
            logger.error(e);
            throw new BaseException(ResultCodeEnum.GET_USER_INFO_FAIL);
        }
        Long modelId = model.getId();
        List<String> list = labelMapper.selectListById(modelId.toString());
        List<String> labels = labelMapper.getLabels(list);
        modelVO.setLabel(labels);
        if(myUid==null){
            modelVO.setIsLike("0");
            modelVO.setIsCollection("0");
            return modelVO;
        }
        modelVO.setType(typeMapper.selectById(model.getTypeId()).getType());
        modelVO.setIsLike(mapper.queryUserModelLikes(myUid,model.getId().toString())==null?"0":"1");
        modelVO.setIsCollection(mapper.queryUserModelCollection(myUid,model.getId().toString())==null?"0":"1");
        return modelVO;
    }

    private Page<ModelVO> getModelListCommon(QueryWrapper<ModelDO> queryWrapper, String page, String size, String uid) {
        Page<ModelDO> modelPage = mapper.selectPage(new Page<>(Long.parseLong(page), Long.parseLong(size),false), queryWrapper);
        List<ModelVO> modelVOList = modelPage.getRecords().stream()
                .map(modelDO -> convertToModelVO(modelDO,uid))
                .collect(Collectors.toList());
        return new Page<ModelVO>().setRecords(modelVOList);
    }

    private void setSortingCriteria(QueryWrapper<ModelDO> queryWrapper, String sortType) {
        switch (sortType) {
            case "time":
                queryWrapper.orderByDesc("create_time");
                break;
            case "likes":
                queryWrapper.orderByDesc("likes_num");
                break;
            case "views":
                queryWrapper.orderByDesc("view_num");
                break;
            default:
                throw new BaseException(ResultCodeEnum.SORT_FAIL);
        }
    }

}
