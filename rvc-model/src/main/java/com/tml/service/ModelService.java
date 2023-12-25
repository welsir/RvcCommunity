package com.tml.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.tml.domain.DTO.ReceiveUploadFileDTO;
import com.tml.domain.VO.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/4 14:29
 */
public interface ModelService {

    Page<ModelVO> getModelList(String size, String page,String sortType,String uid);

    Page<ModelVO> getModelList(String type,String page,String size,String sortType,String uid);

    ModelVO queryOneModel(String modelId, String uid);

    void insertOneModel(ModelInsertVO model,String uid);

    String downloadModel(String modelId,String uid);

    Boolean editModelMsg(ModelUpdateFormVO modelUpdateFormVO,String uid);

    List<ReceiveUploadFileDTO> uploadModel(MultipartFile[] file,String uid);

    ReceiveUploadFileDTO uploadImage(MultipartFile file,String uid);

    String insertLabel(String label,String uid);

    Page<ModelVO> getUserLikesList(String uid,String page,String limit,String order);

    Page<ModelVO> getUserCollectionList(String uid,String page,String limit,String order);

    Boolean delSingleModel(String modelId,String uid);

    Page<ModelVO> queryUserModelList(String uid,String page,String limit);

    String commentModel(CommentFormVO commentFormVO,String uid);

    Boolean likeComment(String uid,String commentId,String type);

    Page<FirstCommentVO> queryFirstCommentList(String modelId, String page, String limit, String sortType,String uid);

    Page<SecondCommentVO> querySecondCommentList(String parentCommentId,String page,String limit,String sortType,String uid);

    Boolean userLikesModel(String status,String modelId,String uid);

    Boolean userCollectionModel(String status,String modelId,String uid);

    List<LabelVO> getLabelList();
}
