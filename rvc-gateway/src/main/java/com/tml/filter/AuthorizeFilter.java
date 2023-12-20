package com.tml.filter;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.cloud.commons.lang.StringUtils;

import io.github.common.logger.CommonLogger;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URL;
import java.util.HashSet;

import static com.tml.constant.GatewayConstantPool.AUTHORIZE_TOKEN;

@Component
@Data
@RefreshScope
@ConfigurationProperties("api")
public class AuthorizeFilter implements GlobalFilter, Ordered, InitializingBean {

    private HashSet<String> whiteApi = new HashSet<>();

    @Resource
    CommonLogger commonLogger;

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {


        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        URL url = new URL(request.getURI().toString());
        String requestUrl = url.getFile();

        if(whiteApi.contains(requestUrl)){
            // 获取请求头
            HttpHeaders headers = request.getHeaders();
            // 请求头中获取令牌
            String token = headers.getFirst(AUTHORIZE_TOKEN);
            // 判断请求头中是否有令牌
            if (StringUtils.isEmpty(token)) {
                //7. 响应中放入返回的状态吗, 没有权限访问
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                //8. 返回
                return response.setComplete();
            }
            try {
                ServerHttpRequest newRequest = null;
                String loginID = (String) StpUtil.getLoginIdByToken(token);
                if (StpUtil.isLogin(loginID)) {
                    String[] vars = loginID.split("\\|");
                    if(vars.length==2){
                        String uid = vars[0];
                        String username = vars[1];
                        commonLogger.info("uid:%s,username:%s",uid,username);
                        newRequest = request.mutate().header("uid",uid).header("username",username).build();
                        return chain.filter(exchange.mutate().request(newRequest).build());
                    }
                }
            } catch (NotLoginException e) {
                e.printStackTrace();
                //10. 解析jwt令牌出错, 说明令牌过期或者伪造等不合法情况出现
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                //11. 返回
                return response.setComplete();
            }
        }

        // 放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        commonLogger.info("whiteApi="+whiteApi);
    }
}
