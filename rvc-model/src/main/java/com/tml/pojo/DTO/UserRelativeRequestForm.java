package com.tml.pojo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/11 14:36
 */
@Data
@AllArgsConstructor
public class UserRelativeRequestForm {
    private String userId;
    private String modelId;

}
