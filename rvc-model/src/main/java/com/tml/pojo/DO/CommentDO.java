package com.tml.pojo.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tml.pojo.VO.CommentFormVO;
import lombok.Builder;
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
    private String replyId;
    private String likesNum;
    private String hasShow;
    private String createTime;
    private String updateTime;

}
