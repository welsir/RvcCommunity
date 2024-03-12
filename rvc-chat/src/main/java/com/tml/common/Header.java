package com.tml.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author welsir
 * @Date 2024/3/3 1:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Header {

    private int type;

    private byte[] data;
}
