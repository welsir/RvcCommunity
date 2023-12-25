package com.tml.domain.DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Date 2023/12/12
 * @Author xiaochun
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthUser {
    private String uid;

    private String username;
}