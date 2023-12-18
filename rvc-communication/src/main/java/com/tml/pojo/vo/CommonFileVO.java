package com.tml.pojo.vo;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @NAME: CommonFileVO
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/17
 */
@Data
@Builder
public class CommonFileVO {
    private MultipartFile file;
    private String md5;
    private String bucket;
}