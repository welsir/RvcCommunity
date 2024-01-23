package com.tml.filter;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.cloud.nacos.client.NacosPropertySourceBuilder;
import io.github.common.logger.CommonLogger;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

import static com.tml.constant.GatewayConstantPool.AUTHORIZE_TOKEN;

@Component
@Data
@RefreshScope
@ConfigurationProperties("api")
public class LaxAuthorizeFilter implements GlobalFilter, Ordered, InitializingBean {

    private HashSet<String> laxTokenApi = new HashSet<String>();

    @Resource
    CommonLogger commonLogger;
    @Override
    public void afterPropertiesSet() throws Exception {
        commonLogger.info("laxTokenApi="+laxTokenApi);
    }

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if(laxTokenApi.contains(path)){
            // 获取请求头
            HttpHeaders headers = request.getHeaders();
            // 请求头中获取令牌
            String token = headers.getFirst(AUTHORIZE_TOKEN);
            ServerHttpRequest newRequest = null;
            // 判断请求头中是否有令牌
            if (StringUtils.isEmpty(token)) {
                newRequest = request.mutate().header("uid","").header("username","").build();
                return chain.filter(exchange.mutate().request(newRequest).build());
            }
            try {
                String loginID = (String) StpUtil.getLoginIdByToken(token);
                if (StpUtil.isLogin(loginID)) {
                    String[] vars = loginID.split("\\|");
                    if(vars.length==2){
                        String uid = vars[0];
                        String username = vars[1];
                        newRequest = request.mutate().header("uid",uid).header("username",username).build();
                        return chain.filter(exchange.mutate().request(newRequest).build());
                    }
                }
            } catch (NotLoginException e) {
                newRequest = request.mutate().header("uid","").header("username","").build();
                return chain.filter(exchange.mutate().request(newRequest).build());
            }
        }

        // 放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}

