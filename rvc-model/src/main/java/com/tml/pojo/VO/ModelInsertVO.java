package com.tml.pojo.VO;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    @Size(max = 19, message = "ID长度不能超过19")
    private String authorId;
    @NotBlank(message = "模型类型不能为空")
    private String type;
    @NotNull(message = "不能传入空字符")
    private String description;
    @NotBlank(message = "不能传入空字符")
    private String picture;
    private String bucket;
    private String path;
}
