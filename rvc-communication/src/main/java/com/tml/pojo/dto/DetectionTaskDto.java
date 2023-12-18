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

    private String id;
//    内容
    private String content;

    //    业务名
    private String name;
}

