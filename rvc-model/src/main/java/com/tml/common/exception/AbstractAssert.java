package com.tml.common.exception;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.tml.pojo.ResultCodeEnum;

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

}
