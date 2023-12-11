package com.tml.util;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

/**
 * @Date 2023/12/12
 * @Author xiaochun
 */
public class CopyUtil{
    public static <T, W> void copyPropertiesForList(List<T> source, List<W> target, Class<W> clazz){
        if (source == null || target == null || source.size() != target.size()) {
            throw new IllegalArgumentException("Lists are null or have different sizes");
        }

        try {
            for (T sourceElement : source) {
                W targetElement = clazz.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(sourceElement, targetElement);
                target.add(targetElement);
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
