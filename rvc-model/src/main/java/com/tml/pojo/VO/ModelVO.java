package com.tml.pojo.VO;

import com.tml.pojo.DO.ModelDO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/4 14:46
 */
@Data
public class ModelVO {

    private String id;
    private String modelName;
    private String typeId;
    private String labelId;
    private String picture;
    private String description;
    private String note;
    private String viewNum;
    private String likesNum;
    private String collectionNum;
    private String isLike;
    private String isCollection;
    private String uid;
    private String usernmae;
    private String nickname;
    private String avatar;

    public static ModelVO modelDOToModelVO(ModelDO modelDO){
        ModelVO modelVO = new ModelVO();
        modelVO.setId(String.valueOf(modelDO.getId()));
        modelVO.setModelName(modelDO.getName());
        modelVO.setPicture(modelVO.getPicture());
        modelVO.setLikesNum(modelDO.getLikesNum());
        modelVO.setCollectionNum(modelDO.getCollectionNum());
        modelVO.setDescription(modelDO.getDescription());
        modelVO.setViewNum(modelVO.getViewNum());
        modelVO.setNote(modelDO.getNote());
        modelVO.setTypeId(modelDO.getTypeId());
        modelVO.setLabelId(modelDO.getLabelId());
        return modelVO;
    }
}
