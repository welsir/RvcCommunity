package com.tml.pojo.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/4 19:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("rvc_model")
public class ModelDO {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String fileId;
    private String name;
    private String authorId;
    private String type;
    private String collectionNum;
    private String likesNum;
    private String viewNum;
    private String description;
    private String picture;
    private Date createTime;
    private Date updateTime;
    private boolean hasShow;
}
