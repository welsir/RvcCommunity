package com.tml.pojo.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @NAME: DetectionTaskDto
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/29
 */
@Data
@Builder
public class DetectionTaskDto implements Serializable {
    private Long id;
//    内容
    private String content;
//    回调
    private String url;
}