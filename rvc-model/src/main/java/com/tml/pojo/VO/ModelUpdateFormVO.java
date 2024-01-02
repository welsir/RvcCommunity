package com.tml.pojo.VO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/4 17:55
 */
@Data
public class ModelUpdateFormVO {
    @NotBlank(message = "模型id不能为空")
    private String id;
    @NotBlank(message = "模型名称不能为空")
    private String name;
    @NotBlank(message = "模型描述不能为空")
    private String description;
    private MultipartFile picture;
    @NotBlank(message = "模型注意事项不能为空")
    private String note;
}
