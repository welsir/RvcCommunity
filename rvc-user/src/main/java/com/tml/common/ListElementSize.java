package com.tml.common;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @Date 2023/12/14
 * @Author xiaochun
 */
@Documented
@Constraint(validatedBy = ListElementSizeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ListElementSize {
    String message() default "元素长度错误";

    int max() default 0;

    int min() default Integer.MAX_VALUE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
