package com.tml.filter;

import io.github.common.logger.CommonLogger;
import io.github.util.time.TimeUtil;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URL;
import java.util.HashSet;

@Component
@Data
@RefreshScope
@ConfigurationProperties("api")
public class InternalFilter implements GlobalFilter, Ordered, InitializingBean {

    private HashSet<String> internalApi = new HashSet<>();

    @Resource
    CommonLogger commonLogger;
    @Override
    public void afterPropertiesSet() throws Exception {
        commonLogger.info("internalApi=%s",internalApi);
    }

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        URL url = new URL(request.getURI().toString());
        String requestUrl = url.getFile();
        if (internalApi.contains(requestUrl)){
            //7. 响应中放入返回的状态吗, 没有权限访问
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //8. 返回
            return response.setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

}
