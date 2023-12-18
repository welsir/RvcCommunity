package com.tml.common.handler;

import com.baomidou.mybatisplus.extension.api.R;

public interface HandlerStrategy<PARAMS,RESULT> {

    String name();

    default void handler(PARAMS params){

    }

    default RESULT handlerRes(PARAMS params){
        return null;
    }
}
