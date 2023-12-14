package com.tml.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Copyright (C),2021-2023
 * All rights reserved.
 * FileName: PageInfo
 * @author NEKOnyako
 * Description: 分页查询参数数据
 * Date: 2023/09/05 0005 0:39
 */

@Data
public class PageInfo<T> implements Serializable {

    private T data;
    @NotNull(message = "参数不能为空")
    private Integer page;
    @NotNull(message = "参数不能为空")
    private Integer limit;
}
