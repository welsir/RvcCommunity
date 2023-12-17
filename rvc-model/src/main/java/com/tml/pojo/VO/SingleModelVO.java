package com.tml.pojo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Date 2023/12/17
 * @Author xiaochun
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleModelVO {

    private String modelId;
    private String name;
    private String uid;
    private String username;
    private String nickname;
    private String avatar;
    private String modelName;
    private String description;
    private String note;
    private String viewNum;
    private String collectionNum;
    private String likesNum;
    private String picture;
    private String isLike;
    private String isCollection;
    private String createTime;
}