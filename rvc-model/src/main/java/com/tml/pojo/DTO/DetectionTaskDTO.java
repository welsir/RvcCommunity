package com.tml.pojo.DTO;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/7 23:04
 */
@Data
@Builder
public class DetectionTaskDTO implements Serializable {

    private String id;
    private String content;
    private String name;

}
