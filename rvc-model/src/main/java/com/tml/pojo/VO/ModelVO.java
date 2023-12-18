package com.tml.pojo.VO;

import com.tml.common.Result;
import com.tml.pojo.DO.ModelDO;
import com.tml.pojo.DTO.UserInfoDTO;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/4 14:46
 */
@Data
@Builder
public class ModelVO {

    private String id;
    private String name;
    private String type;
    private List<String> label;
    private String picture;
    private String description;
    private String note;
    private String viewNum;
    private String likesNum;
    private String collectionNum;
    private String isLike;
    private String isCollection;
    private String uid;
    private String username;
    private String nickname;
    private String avatar;

    public static ModelVO modelDOToModelVO(ModelDO modelDO, UserInfoDTO userInfo,List<String> labels,String... args){
        ModelVO modelVO = modelDOToModelVO(modelDO, userInfo, args);
        modelVO.setLabel(labels);
        return modelVO;
    }
    public static ModelVO modelDOToModelVO(ModelDO modelDO, UserInfoDTO userInfo,String... args){
        ModelVO modelVO = modelDOToModelVO(modelDO, userInfo);
        modelVO.setType(args[0]);
        modelVO.setIsLike("".equals(args[1])||null==args[1]?"0":"1");
        modelVO.setIsCollection("".equals(args[2])||null==args[2]?"0":"1");
        return modelVO;
    }
    public static ModelVO modelDOToModelVO(ModelDO modelDO, UserInfoDTO userInfo){
        return ModelVO.builder()
                .id(modelDO.getId().toString())
                .name(modelDO.getName())
                .picture(modelDO.getPicture())
                .likesNum(modelDO.getLikesNum())
                .collectionNum(modelDO.getCollectionNum())
                .description(modelDO.getDescription())
                .viewNum(modelDO.getViewNum())
                .note(modelDO.getNote())
                .uid(userInfo.getUid())
                .avatar(userInfo.getAvatar())
                .nickname(userInfo.getNickname())
                .username(userInfo.getUsername())
                .build();
    }
}
