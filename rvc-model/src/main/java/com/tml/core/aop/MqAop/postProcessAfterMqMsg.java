package com.tml.core.aop.MqAop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/15 9:06
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface postProcessAfterMqMsg {

    String value() default "";

}
