package com.tml.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.tml.pojo.DO.ModelFileDO;
import com.tml.pojo.DO.TypeDO;
import com.tml.pojo.DTO.ReceiveUploadFileDTO;
import com.tml.pojo.VO.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/4 14:29
 */
public interface ModelService {

    Page<ModelVO> getModelList(Long size, Long page,String sortType,String uid);

    Page<ModelVO> getModelList(String type,Long page,Long size,String sortType,String uid);

    ModelVO queryOneModel(String modelId, String uid);

    void insertOneModel(ModelInsertVO model,String uid);

    String downloadModel(String modelId,String uid);

    Boolean editModelMsg(ModelUpdateFormVO modelUpdateFormVO,String uid);

    List<ReceiveUploadFileDTO> uploadModel(MultipartFile[] file, String uid);

    ReceiveUploadFileDTO uploadImage(MultipartFile file,String uid);

    ReceiveUploadFileDTO uploadAudio(MultipartFile file,String uid);

    String insertLabel(String label,String uid);

    Page<ModelVO> getUserLikesList(String uid,Long page,Long limit,String order);

    Page<ModelVO> getUserCollectionList(String uid,Long page,Long limit,String order);

    Boolean delSingleModel(String modelId,String uid);

    Page<ModelVO> queryUserModelList(String uid,Long page,Long limit);

    String commentModel(CommentFormVO commentFormVO,String uid);

    Boolean likeComment(String uid,String commentId,String type);

    Page<FirstCommentVO> queryFirstCommentList(String modelId, Long page, Long limit, String sortType,String uid);

    Page<SecondCommentVO> querySecondCommentList(String parentCommentId,Long page,Long limit,String sortType,String uid);

    Boolean userLikesModel(String status,String modelId,String uid);

    Boolean userCollectionModel(String status,String modelId,String uid);

    List<LabelVO> getLabelList(Long limit,Long page);

//    List<ModelFileVO> getModelFies(String modelId);

    List<TypeDO> queryTypeList();

}
