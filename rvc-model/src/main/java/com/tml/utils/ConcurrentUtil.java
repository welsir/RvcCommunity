package com.tml.utils;

import com.tml.common.exception.BaseException;
import com.tml.common.log.AbstractLogger;

import javax.annotation.Resource;
import java.util.concurrent.*;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/18 14:27
 */
public class ConcurrentUtil {

    public static <T> Future<T> doJob(ExecutorService executorService, Callable<T> callable) {
        return executorService.submit(callable);
    }

    public static <T> T futureGet(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new BaseException(e.toString());
        }
    }
}
