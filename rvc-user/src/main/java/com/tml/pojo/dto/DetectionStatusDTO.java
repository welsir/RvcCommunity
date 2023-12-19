package com.tml.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Date 2023/12/17
 * @Author xiaochun
 */
@Data
public class DetectionStatusDTO implements Serializable {
    private String id;

    private String labels;

    private String name;
}
