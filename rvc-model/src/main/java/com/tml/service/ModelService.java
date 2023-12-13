package com.tml.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.tml.pojo.VO.ModelInsertVO;
import com.tml.pojo.VO.ModelUpdateFormVO;
import com.tml.pojo.VO.ModelVO;
import com.tml.pojo.VO.SingleModelVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/4 14:29
 */
public interface ModelService {

    Page<ModelVO> getModelList(String size, String page,String sortType,String uid);

    Page<ModelVO> getModelList(String type,String size,String page,String sortType,String uid);

    SingleModelVO queryOneModel(String modelId, String uid);

    void insertOneModel(ModelInsertVO model);

    String downloadModel(String modelId,String isPrivate,String bucket);

    Boolean editModelMsg(ModelUpdateFormVO modelUpdateFormVO);

    String uploadModel(MultipartFile file);

    void insertRelative(String type,String modelId,String uid,String isClick);

    void insertType(String type);

}
