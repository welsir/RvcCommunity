package com.tml.pojo.VO;

import lombok.Builder;
import lombok.Data;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/17 17:58
 */
@Data
@Builder
public class SecondCommentVO {

    private String id;
    private String uid;
    private String nickname;
    private String picture;
    private String content;
    private String likesNum;
    private String commentTime;
    private String parentId;
    private String isLikes;

}
