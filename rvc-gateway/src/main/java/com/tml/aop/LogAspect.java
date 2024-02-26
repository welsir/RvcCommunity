package com.tml.aop;

import com.tml.anno.LogTime;
import io.github.common.logger.CommonLogger;
import io.github.common.web.Result;
import io.github.util.time.TimeUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

@Component
@Aspect
public class LogAspect {

    @Resource
    CommonLogger commonLogger;

    @Pointcut("@annotation(com.tml.anno.LogTime)")
    public void LogTime(){

    }

    @Around("LogTime()")
    private Mono<Void> logGatewayHandleTime(final ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            Mono<Void> result = (Mono<Void>) point.proceed();
            return result;
        }finally {
            long endTime = System.currentTimeMillis();
            Method targetMethod = ((MethodSignature) point.getSignature()).getMethod();
            Class<?>[] paramTypes = targetMethod.getParameterTypes();
            LogTime anno = point.getTarget().getClass().getDeclaredMethod(point.getSignature().getName(), paramTypes)
                    .getAnnotation(LogTime.class);

            ServerHttpRequest request = ((ServerWebExchange)(point.getArgs()[0])).getRequest();
            InetSocketAddress remoteAddress = request.getRemoteAddress();
            String url = request.getURI().getPath();
            String funcName = anno.funcName();
            commonLogger.info("[Filter Time] request url: %s, remote address: %s,funcName: %s,time: %sms", url, remoteAddress.getHostName(),funcName, endTime-startTime);
        }


    }
}
