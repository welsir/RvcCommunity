package com.tml.common.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * @Date 2023/12/14
 * @Author xiaochun
 */
public class ListElementSizeValidator implements ConstraintValidator<ListElementSize, List<String>> {
    private int min;
    private int max;

    @Override
    public void initialize(ListElementSize constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        for (String element : value) {
            // 检查字符串是否只包含数字
            if (element == null || element.length() < min || element.length() > max) {
                return false;
            }
        }
        return true;
    }
}
