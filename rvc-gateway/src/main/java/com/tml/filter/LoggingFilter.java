package com.tml.filter;

import com.tml.mapper.RequestRecordMapper;
import com.tml.pojo.DO.RequestRecordDO;
import com.tml.service.RequestRecordService;
import com.tml.utils.SnowFlakeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RefreshScope
public class LoggingFilter implements GlobalFilter, Ordered {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

    @Resource
    RequestRecordService requestRecordService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        String url = request.getURI().toString();
        LocalDateTime dateTime = LocalDateTime.now();
        requestRecordService.insertRequestRecord(new RequestRecordDO(String.valueOf(SnowFlakeUtil.getNextId()),remoteAddress.getHostName(),url,dateTime.format(formatter)));
        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return -1;
    }
}

