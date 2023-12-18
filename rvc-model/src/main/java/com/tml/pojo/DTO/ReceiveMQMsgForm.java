package com.tml.pojo.DTO;

import lombok.Data;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/7 16:32
 */
@Data
public class ReceiveMQMsgForm {

    private String status;
    private String id;
    private String violationInformation;
    private String name;

}
