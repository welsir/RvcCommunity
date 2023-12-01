package com.tml.utils;


import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuech
 * @version 1.0
 * @description: TODO
 * @date 2022/11/30 19:21
 */
public class BeanCopyUtils {
    private BeanCopyUtils(){
    }

    public static <V> V  copyBean(Object source,Class<V> clazz){
        //创建目标对象
        V result = null;
        try {
            result = clazz.newInstance();
            //实现属性copy
            BeanUtils.copyProperties(source,result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
        //返回结果
    }


    public static <O,V> List<V> copyBeanList(List<O> list,Class<V> clazz){
        return list.stream()
                .map(o -> copyBean(o, clazz))
                .collect(Collectors.toList());
    }

//    public static void main(String[] args) {
//        Article article = new Article();
//        article.setId(1L);
//        article.setTitle("ss");
//    }
}