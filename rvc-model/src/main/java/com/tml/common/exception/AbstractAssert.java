package com.tml.common.exception;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.tml.pojo.ResultCodeEnum;
import org.springframework.lang.Nullable;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/18 12:38
 */
public abstract class AbstractAssert {

    public static void isBlank(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw new BaseException(message);
        }
    }
    public static void isBlank(String str, ResultCodeEnum message) {
        if (StringUtils.isBlank(str)) {
            throw new BaseException(message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object == null) {
            throw new BaseException(message);
        }
    }

    public static void isNull(Object object, ResultCodeEnum message) {
        if (object == null) {
            throw new BaseException(message);
        }
    }

    public static void isNull(String str, ResultCodeEnum message) {
        if (StringUtils.isBlank(str)) {
            throw new BaseException(message);
        }
    }

    public static void notNull(@Nullable Object object, ResultCodeEnum message) {
        if (object != null) {
            throw new BaseException(message);
        }
    }
    public static void notNull(String str, ResultCodeEnum message) {
        if (StringUtils.isBlank(str)) {
            throw new BaseException(message);
        }
    }

    public static void isTrue(boolean expression,ResultCodeEnum message){
        if(expression){
            throw new BaseException(message);
        }
    }

}
