package com.tml.domain.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/17 14:58
 */
@Data
@TableName("rvc_model_comment")
public class CommentDO {

    private Long id;
    private String content;
    private String uid;
    private String modelId;
    private String parentId;
    private String likesNum;
    private String hasShow;
    private String createTime;
    private String updateTime;

}
