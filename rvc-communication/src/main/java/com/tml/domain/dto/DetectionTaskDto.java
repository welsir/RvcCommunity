package com.tml.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @NAME: DetectionTaskDto
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetectionTaskDto implements Serializable {

    //审核 路由 key
    private String routerKey;

    private String type;

    private String id;
//    内容
    private String content;
}

