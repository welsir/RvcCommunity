package com.tml.filter;

import com.tml.domain.DO.RequestRecordDO;
import com.tml.service.RequestRecordService;
import io.github.common.logger.CommonLogger;
import io.github.id.snowflake.SnowflakeGenerator;
import io.github.id.snowflake.SnowflakeRegisterException;
import io.github.util.time.TimeUtil;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import javax.annotation.Resource;
import java.net.InetSocketAddress;

@Component
@RefreshScope
public class LoggingFilter implements GlobalFilter, Ordered {
    @Resource
    RequestRecordService requestRecordService;

    @Resource
    SnowflakeGenerator snowflakeGenerator;
    @Resource(name = "taskExecutor")
    ThreadPoolTaskExecutor taskExecutor;

    @Resource
    CommonLogger logger;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        String url = request.getURI().getPath();
        String dateTime = TimeUtil.nowDateTimeFormat();
        logger.info("request url: %s, remote address: %s,time: %s", url, remoteAddress.getHostName(), dateTime);
        taskExecutor.submit(()->{
            try {
                requestRecordService.insertRequestRecord(new RequestRecordDO(String.valueOf(snowflakeGenerator.generate()),remoteAddress.getHostName(),url,dateTime));
            } catch (SnowflakeRegisterException e) {
                logger.error("snowflake register exception %s",e);
            }
        });
        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return 0;
    }

}

