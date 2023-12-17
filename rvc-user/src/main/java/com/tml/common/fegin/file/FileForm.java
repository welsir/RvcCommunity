package com.tml.common.fegin.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Date 2023/12/17
 * @Author xiaochun
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileForm {
    private String bucket;

    private MultipartFile file;

    private String md5;

    private String path;
}
