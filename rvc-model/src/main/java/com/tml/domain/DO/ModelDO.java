package com.tml.domain.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long id;
    private String fileId;
    private String name;
    private String typeId;
    private String collectionNum;
    private String likesNum;
    private String viewNum;
    private String note;
    private String description;
    private String picture;
    private String createTime;
    private String updateTime;
    private String hasShow;
    private boolean hasDelete;
}
