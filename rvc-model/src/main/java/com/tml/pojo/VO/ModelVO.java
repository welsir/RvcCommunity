package com.tml.pojo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.tml.pojo.DO.ModelDO;
import com.tml.pojo.DTO.UserInfoDTO;
import io.github.constant.TimeConstant;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
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
    private List<LabelVO> label;
    private String picture;
    private String description;
    private String note;
    private Long viewNum;
    private Long likesNum;
    private Long collectionNum;
    private String isLike;
    private String isCollection;
    private String uid;
    private String username;
    private String nickname;
    private String avatar;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = TimeConstant.YMD_HMS, timezone = "GMT+8")
    private LocalDateTime createTime;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = TimeConstant.YMD_HMS, timezone = "GMT+8")
    private LocalDateTime updateTime;
    private String isFollow;
    public static ModelVO modelDOToModelVO(ModelDO modelDO, UserInfoVO userInfo,List<LabelVO> labels,String... args){
        ModelVO modelVO = modelDOToModelVO(modelDO, userInfo, args);
        modelVO.setLabel(labels);
        return modelVO;
    }
    public static ModelVO modelDOToModelVO(ModelDO modelDO, UserInfoVO userInfo,String... args){
        ModelVO modelVO;
        modelVO = userInfo==null?modelDOToModelVO(modelDO):modelDOToModelVO(modelDO,userInfo);
        modelVO.setType(args[0]);
        modelVO.setIsLike("1".equals(args[1])?"true":"false");
        modelVO.setIsCollection("1".equals(args[2])?"true":"false");
        return modelVO;
    }
    public static ModelVO modelDOToModelVO(ModelDO modelDO, UserInfoVO userInfo){
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
                .createTime(modelDO.getCreateTime())
                .updateTime(modelDO.getUpdateTime())
                .build();
    }
    public static ModelVO modelDOToModelVO(ModelDO modelDO){
        return ModelVO.builder()
                .id(modelDO.getId().toString())
                .name(modelDO.getName())
                .picture(modelDO.getPicture())
                .likesNum(modelDO.getLikesNum())
                .collectionNum(modelDO.getCollectionNum())
                .description(modelDO.getDescription())
                .viewNum(modelDO.getViewNum())
                .note(modelDO.getNote())
                .updateTime(modelDO.getUpdateTime())
                .build();
    }
}
