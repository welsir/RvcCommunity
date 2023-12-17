package com.tml.common.fegin.file;

import com.tml.common.fegin.captcha.Result;
import com.tml.config.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Date 2023/12/16
 * @Author xiaochun
 */
@Component
@FeignClient(name = "file-system-service0", configuration = MultipartSupportConfig.class)
@RequestMapping("/file/oss")
public interface FileService {
    @PostMapping(value = "/upload", consumes = MediaType.APPLICATION_JSON_VALUE)
    Result upload(@RequestPart("file") MultipartFile file, @RequestPart("bucket") String bucket, @RequestPart("md5") String md5, @RequestPart("path") String path);
}
