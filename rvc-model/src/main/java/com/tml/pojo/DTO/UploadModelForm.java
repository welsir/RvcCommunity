package com.tml.pojo.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/6 16:14
 */
@Data
@Builder
public class UploadModelForm {
    @JsonIgnore
    private MultipartFile file;
    private String bucket;
    private String md5;
    private String path;
}
