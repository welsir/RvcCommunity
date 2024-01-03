package com.tml.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginInfoDTO {
    private String id;
    private String username;
}
