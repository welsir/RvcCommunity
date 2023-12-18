package com.tml.pojo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginInfoDTO {
    private String id;
    private String username;
}
