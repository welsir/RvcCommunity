package com.tml.mapper.common;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface CommonMapper<T> extends BaseMapper<T> {

    boolean existsRecord(String tableName, String key, String value);

}
