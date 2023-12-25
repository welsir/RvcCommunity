package com.tml.domain.DTO;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/11 15:00
 */
@Data
public class DetectionStatusDTO implements Serializable {

    private String id;
    private String labels;
    private String name;

}
