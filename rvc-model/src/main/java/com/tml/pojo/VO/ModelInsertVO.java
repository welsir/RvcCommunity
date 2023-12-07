package com.tml.pojo.VO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/4 14:34
 */
@Data
public class ModelInsertVO {

    @TableId(type = IdType.AUTO)
    private Long id;
    @NotBlank(message = "模型文件不能为空")
    private MultipartFile file;
    @NotBlank(message = "名称不能为空")
    @Length(max = 10,message = "模型名称不能超过5个字")
    private String name;
    @Max(value = 12,message = "ID长度不能超过12")
    private String authorId;
    @NotBlank(message = "模型类型不能为空")
    private String type;
    private String collectionNum;
    private String likesNum;
    @NotNull(message = "不能传入空字符")
    private String description;
    @NotBlank(message = "不能传入空字符")
    private String picture;
    private Date createTime;
    private Date updateTime;
    private boolean hasShow;
}
