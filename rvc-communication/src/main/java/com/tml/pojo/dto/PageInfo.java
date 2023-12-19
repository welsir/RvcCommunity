package com.tml.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
public class PageInfo<T> implements Serializable {

    private T data;
    @NotNull(message = "page 参数不能为空")
    private Integer page;
    @NotNull(message = " limit 参数不能为空")
    private Integer limit;
}
