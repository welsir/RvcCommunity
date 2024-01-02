package com.tml.pojo.VO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

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
    private Long likesNum;
    private LocalDateTime commentTime;
    private String modelId;
    private boolean isLikes;
}
