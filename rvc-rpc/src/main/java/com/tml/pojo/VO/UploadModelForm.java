package com.tml.pojo.VO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/18 10:23
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
