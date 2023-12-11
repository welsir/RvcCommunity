package com.tml.pojo.DTO;

import lombok.Data;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/11 15:00
 */
@Data
public class DetectionStatusDTO {

    private String id;
    private Integer status;
    private String violationInformation;
    private String name;

}
