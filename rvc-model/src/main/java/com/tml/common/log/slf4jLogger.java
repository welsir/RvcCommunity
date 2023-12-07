package com.example.filesystem.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author welsir
 * @Date 2023/11/23 19:21
 */
@Component("slf4j")
public class slf4jLogger implements BaseLoggerInterface{

    private final static Logger FILE_SYSTEM = LoggerFactory.getLogger("fileSystem");

    @Override
    public void info(String msg, Object... args) {
        FILE_SYSTEM.info(String.format(msg, args));
    }

    @Override
    public void warn(String msg, Object... args) {
        FILE_SYSTEM.warn(String.format(msg, args));
    }

    @Override
    public void debug(String msg, Object... args) {
        FILE_SYSTEM.debug(String.format(msg, args));
    }

    @Override
    public void error(String msg, Object... args) {
        FILE_SYSTEM.error(String.format(msg, args));
    }
}
