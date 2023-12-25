package com.tml.pojo.VO;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/4 14:34
 */
@Data
public class ModelInsertVO {
    @Size(max = 19, message = "ID长度不能超过19")
    private String fileId;
    @NotBlank(message = "名称不能为空")
    @Length(max = 10,message = "模型名称不能超过5个字")
    private String name;
    @NotBlank(message = "模型类型id不能为空")
    private String typeId;
    private List<String> label;
    @NotBlank(message = "模型描述不能为空")
    private String description;
    @NotBlank(message = "模型注意事项不能为空")
    private String note;
    @NotBlank(message = "模型图片不能为空")
    private String picture;
}
