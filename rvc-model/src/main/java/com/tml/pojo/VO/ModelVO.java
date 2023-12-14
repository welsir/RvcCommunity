package com.tml.pojo.VO;

import com.tml.common.Result;
import com.tml.pojo.DO.ModelDO;
import com.tml.pojo.DTO.UserInfoDTO;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/4 14:46
 */
@Data
@Builder
public class ModelVO {

    private String fileId;
    private String name;
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
    private String username;
    private String nickname;
    private String avatar;

    public static ModelVO modelDOToModelVO(ModelDO modelDO, UserInfoDTO result){
        return ModelVO.builder()
                .fileId(String.valueOf(modelDO.getFileId()))
                .name(modelDO.getName())
                .picture(modelDO.getPicture())
                .likesNum(modelDO.getLikesNum())
                .collectionNum(modelDO.getCollectionNum())
                .description(modelDO.getDescription())
                .viewNum(modelDO.getViewNum())
                .note(modelDO.getNote())
                .typeId(modelDO.getTypeId())
                .labelId(modelDO.getLabelId())
                .uid(result.getUid())
                .avatar(result.getAvatar())
                .nickname(result.getNickname())
                .username(result.getUsername())
                .build();
    }
}
