package com.tml.common;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ListEmptyValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ListNotEmpty {
    String message() default "list不能为空";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

