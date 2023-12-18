package com.tml.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tml.common.exception.BaseException;
import com.tml.pojo.ResultCodeEnum;

import java.util.Map;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/18 12:04
 */
public class WrapperUtil {

    public static QueryWrapper setWrappers(QueryWrapper wrapper, Map<String,Object> param) {
        for (String key : param.keySet()) {
            if("sort".equals(key)){
                Object v = param.get(key);
                if(v==null||"".equals(v)){
                    v = "1";
                }
                wrapper = sortWrapper((String) v,wrapper);
            }else {
                wrapper.eq(key,param.get(key));
            }
        }
        return wrapper;
    }

    private static QueryWrapper sortWrapper(String sortType,QueryWrapper wrapper){
        switch (sortType) {
            case "1":
                wrapper.orderByDesc("create_time");
                break;
            case "2":
                wrapper.orderByDesc("likes_num");
                break;
            case "3":
                wrapper.orderByDesc("view_num");
                break;
            default:
                throw new BaseException(ResultCodeEnum.SORT_FAIL);
        }
        return wrapper;
    }

}
