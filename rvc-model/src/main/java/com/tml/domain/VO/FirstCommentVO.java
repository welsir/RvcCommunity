package com.tml.domain.VO;

import lombok.Builder;
import lombok.Data;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/17 16:52
 */
@Data
@Builder
public class FirstCommentVO {
    private String id;
    private String uid;
    private String nickname;
    private String picture;
    private String content;
    private String likesNum;
    private String commentTime;
    private String modelId;
    private String isLikes;
}
