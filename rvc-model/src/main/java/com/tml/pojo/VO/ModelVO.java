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
    private String description;
    private String picture;
    private String name;
    private String likesNum;
    private String collectionNum;
    private String isLike;
    private String isCollection;
    private String viewNum;

    public static ModelVO modelDOToModelVO(ModelDO modelDO){
        ModelVO modelVO = new ModelVO();
        modelVO.setId(String.valueOf(modelDO.getId()));
        modelVO.setName(modelDO.getName());
        modelVO.setPicture(modelVO.getPicture());
        modelVO.setLikesNum(modelDO.getLikesNum());
        modelVO.setCollectionNum(modelDO.getCollectionNum());
        modelVO.setDescription(modelDO.getDescription());
        modelVO.setViewNum(modelVO.getViewNum());
        return modelVO;
    }
}
