package com.tml.common;

import com.tml.domain.ResultCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.tml.domain.ResultCodeEnum.SUCCESS;
import static com.tml.domain.ResultCodeEnum.SYSTEM_ERROR;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/4 14:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> implements Serializable {

    /**
     * 返回状态
     */
    private Boolean flag;

    /**
     * 返回码
     */
    private Integer code;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    public Result(Integer code, String s) {
    }

    public static <T> Result<T> success() {
        return restResult(true, null, SUCCESS.getCode(), SUCCESS.getMsg());
    }

    public static <T> Result<T> success(T data) {
        return restResult(true, data, SUCCESS.getCode(), SUCCESS.getMsg());
    }

    public static <T> Result<T> success(T data, String message) {
        return restResult(true, data, SUCCESS.getCode(), message);
    }


    public static <T> Result<T> fail(ResultCodeEnum resultCodeEnum) {
        return restResult(false, null, resultCodeEnum.getCode(), resultCodeEnum.getMsg());
    }

    public static <T> Result<T> fail(String message) {
        return restResult(false, message);
    }


    public static <T> Result<T> fail(Integer code, String message) {
        return restResult(false, null, code, message);
    }

    private static <T> Result<T> restResult(Boolean flag, String message) {
        Result<T> apiResult = new Result();
        apiResult.setFlag(flag);
        apiResult.setCode(flag ? SUCCESS.getCode() : SYSTEM_ERROR.getCode());
        apiResult.setMessage(message);
        return apiResult;
    }

    private static <T> Result<T> restResult(Boolean flag, T data, Integer code, String message) {
        Result apiResult = new Result();
        apiResult.setFlag(flag);
        apiResult.setData(data);
        apiResult.setCode(code);
        apiResult.setMessage(message);
        return apiResult;
    }
}
