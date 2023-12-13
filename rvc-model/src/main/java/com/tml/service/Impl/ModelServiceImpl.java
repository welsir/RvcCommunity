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
import com.tml.mapper.ModelMapper;
import com.tml.mapper.ModelTypeMapper;
import com.tml.pojo.DO.ModelCollectionDO;
import com.tml.pojo.DO.ModelDO;

import com.tml.pojo.DO.ModelLikeDO;
import com.tml.pojo.DO.ModelTypeDO;
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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
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
    ModelTypeMapper typeMapper;
    @Resource
    FileServiceClient fileServiceClient;
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
                    .eq("has_show", DetectionStatusEnum.DETECTION_SUCCESS);
            setSortingCriteria(queryWrapper, sortType);
            return getModelListCommon(queryWrapper, page, size, uid);
        }catch (BaseException e){
            throw new BaseException(ResultCodeEnum.QUERT_MODEL_LIST_FAIL);
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
            throw new BaseException(ResultCodeEnum.QUERT_MODEL_LIST_FAIL);
        }
    }

    @Override
    public SingleModelVO queryOneModel(String modelId, String uid) {
        try {
            ModelDO model = mapper.selectById(modelId);
            SingleModelVO singleModel = new SingleModelVO();
            BeanUtils.copyProperties(model,singleModel);
            singleModel.setIsLike(mapper.queryUserModelLikes(uid,modelId)==null?"0":"1");
            singleModel.setIsCollection(mapper.queryUserModelCollection(uid,modelId)==null?"0":"1");
            asyncService.asyncAddModelViewNums(singleModel.getModelId());
            return singleModel;
        }catch (BaseException e){
            throw new BaseException(ResultCodeEnum.QUERY_MODEL_FAIL);
        }
    }

    @Override
    public void insertOneModel(ModelInsertVO model) {
        ModelDO modelDO = new ModelDO();
        BeanUtils.copyProperties(model,modelDO);
        modelDO.setUpdateTime(dateUtil.formatDate());
        modelDO.setCreateTime(dateUtil.formatDate());
        modelDO.setLikesNum("0");
        modelDO.setCollectionNum("0");
        modelDO.setViewNum("0");
        modelDO.setHasShow(String.valueOf(DetectionStatusEnum.UN_DETECTION.getStatus()));
        int insert = mapper.insert(modelDO);
        typeMapper.insertModelTypeRelative(String.valueOf(modelDO.getId()),modelDO.getTypeId());
        if(insert!=1){
            throw new BaseException(ResultCodeEnum.ADD_MODEL_FAIL);
        }
        logger.info("方法执行完毕");
        try {
            asyncService.processModelAsync(modelDO);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String downloadModel(String modelId,String isPrivate,String bucket) {
        Result<String> result = fileServiceClient.downloadModel(
                DownloadModelForm.builder().fileId(modelId).isPrivate(isPrivate).bucket(bucket==null?"rvc2":bucket).build());
        return result.getData();
    }

    @Override
    public Boolean editModelMsg(ModelUpdateFormVO modelUpdateFormVO) {
        return null;
    }

    @Override
    public String uploadModel(MultipartFile file) {
        try {
            UploadModelForm form = UploadModelForm.builder()
                    .file(file)
                    .path(ModelConstant.DEFAULT_PATH)
                    .bucket(ModelConstant.DEFAULT_BUCKET)
                    .md5(fileUtil.getMD5Checksum(file.getInputStream()))
                    .build();
            Result<ReceiveUploadModelDTO> model = fileServiceClient.uploadModel(form);
            return model.getData().getFileId();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
    public void insertType(String type) {
        typeMapper.insert(ModelTypeDO.builder().type(type).build());
    }

    private ModelVO convertToModelVO(ModelDO model, String uid) {
        ModelVO modelVO = ModelVO.modelDOToModelVO(model);
        modelVO.setIsLike(mapper.queryUserModelLikes(uid,modelVO.getId())==null?"0":"1");
        modelVO.setIsCollection(mapper.queryUserModelCollection(uid,modelVO.getId())==null?"0":"1");
        return modelVO;
    }

    private Page<ModelVO> getModelListCommon(QueryWrapper<ModelDO> queryWrapper, String page, String size, String uid) {

        Page<ModelDO> modelPage = mapper.selectPage(new Page<>(Long.parseLong(page), Long.parseLong(size),false), queryWrapper);
        List<ModelVO> modelVOList = modelPage.getRecords().stream()
                .map(model -> convertToModelVO(model, uid))
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
