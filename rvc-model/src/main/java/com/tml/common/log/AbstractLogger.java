package com.example.filesystem.common.log;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description
 * @Author welsir
 * @Date 2023/11/23 18:48
 */
@Component
public class AbstractLogger {

    @Resource(name="${file.logger.handler}")
    private BaseLoggerInterface loggerHandler;

    public void info(String msg,Object... args){
        loggerHandler.info(msg,args);
    }

    public void error(String msg,Object... args){
        loggerHandler.error(msg,args);
    }

    public void warn(String msg,Object... args){
        loggerHandler.warn(msg,args);
    }

    public void debug(String msg,Object... args){
        loggerHandler.debug(msg,args);
    }


}
