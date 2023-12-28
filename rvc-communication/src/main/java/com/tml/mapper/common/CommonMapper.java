package com.tml.mapper.common;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface CommonMapper<T> extends BaseMapper<T> {

    boolean existsRecord(String tableName, String key, String value);

    List pages(Integer page,Integer limit,String tableName);

}
