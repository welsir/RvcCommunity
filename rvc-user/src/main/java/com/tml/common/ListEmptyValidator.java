package com.tml.common;

/**
 * @Date 2023/12/14
 * @Author xiaochun
 */
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ListEmptyValidator implements ConstraintValidator<ListNotEmpty, List<String>> {
    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        return !value.isEmpty();
    }
}

