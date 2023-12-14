package com.tml.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.tml.pojo.DO.*;

import com.tml.pojo.DTO.*;
import com.tml.pojo.ResultCodeEnum;
import com.tml.pojo.VO.ModelInsertVO;
import com.tml.pojo.VO.ModelUpdateFormVO;
import com.tml.pojo.VO.ModelVO;
import com.tml.pojo.VO.SingleModelVO;
import com.tml.service.ModelService;
import com.tml.utils.DateUtil;
import com.tml.utils.FileUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
    DateUtil dateUtil;
    private HashMap<String,String> modelStatus = new HashMap<>();

    @Override
    public Page<ModelVO> getModelList(String size, String page,String sortType,String uid) {
        try {
            QueryWrapper<ModelDO> queryWrapper = new QueryWrapper<ModelDO>()
                    .eq("has_show", DetectionStatusEnum.DETECTION_SUCCESS.getStatus());
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
                    .eq("has_show", DetectionStatusEnum.DETECTION_SUCCESS)
                    .eq("type",type);
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
            ModelVO modelVO = ModelVO.builder().build();
            BeanUtils.copyProperties(model,modelVO);
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
        }catch (BaseException e){
            throw new BaseException(ResultCodeEnum.QUERY_MODEL_FAIL);
        }
    }

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
        ModelUserDO modelUserDO = new ModelUserDO();
        modelUserDO.setModelId(String.valueOf(modelDO.getId()));
        modelUserDO.setUid(uid);
        modelUserMapper.insert(modelUserDO);
        try {
            asyncService.processModelAsync(modelDO);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String downloadModel(String modelId) {
        Result<String> result = fileServiceClient.downloadModel(
                DownloadModelForm.builder().fileId(modelId).isPrivate("true").bucket(ModelConstant.DEFAULT_BUCKET).build());
        return result.getData();
    }

    @Override
    public Boolean editModelMsg(ModelUpdateFormVO modelUpdateFormVO) {
        return null;
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
        ModelLabelDO modelLabelDO = new ModelLabelDO();
        modelLabelDO.setLabel(label);
        modelLabelDO.setCreateTime(dateUtil.formatDate());
        labelMapper.insert(modelLabelDO);
        return String.valueOf(modelLabelDO.getId());
    }

    private ModelVO convertToModelVO(ModelDO model) {
        ModelVO modelVO;
        String uid;
        try {
            ModelUserDO modelUserDO = modelUserMapper.selectById(model.getId());
            uid = modelUserDO.getUid();
            Result<UserInfoDTO> userInfo = userServiceClient.getUserInfo(uid);
            modelVO = ModelVO.modelDOToModelVO(model,userInfo.getData());
        }catch (RuntimeException e){
            logger.error("%s:%s",e.getMessage(),e.getStackTrace()[0]);
            throw new RuntimeException(e);
        }
        if(uid==null){
            modelVO.setIsLike("0");
            modelVO.setIsCollection("0");
            return modelVO;
        }
        modelVO.setIsLike(mapper.queryUserModelLikes(uid,modelVO.getFileId())==null?"0":"1");
        modelVO.setIsCollection(mapper.queryUserModelCollection(uid,modelVO.getFileId())==null?"0":"1");
        return modelVO;
    }

    private Page<ModelVO> getModelListCommon(QueryWrapper<ModelDO> queryWrapper, String page, String size, String uid) {
        Page<ModelDO> modelPage = mapper.selectPage(new Page<>(Long.parseLong(page), Long.parseLong(size),false), queryWrapper);
        Date date = new Date(System.currentTimeMillis());
        List<ModelVO> modelVOList = modelPage.getRecords().stream()
                .map(this::convertToModelVO)
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
                // 默认排序逻辑
                break;
        }
    }

}
