package com.tml.component.apiAuth;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.tml.annotation.apiAuth.WhiteApi;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Method;
import java.util.*;

@Component
@Conditional(WebEnvironmentCondition.class)
public class ApiAuthRegister implements InitializingBean {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    private final ArrayList<String> whiteApi = new ArrayList<>();

    @Value("${spring.cloud.nacos.server-addr}")
    private String NACOS_SERVER_ADDRESS;

    @Value("${spring.cloud.nacos.username}")
    private String NACOS_USERNAME;

    @Value("${spring.cloud.nacos.password}")
    private String NACOS_PASSWORD;

    @Value("${spring.cloud.nacos.config.namespace}")
    private String NACOS_NAMESPACE;

    private static final String NACOS_GROUP = "DEFAULT_GROUP";

    private static final String  NACOS_DATAID = "authority-api-test.yaml";

    @Autowired(required = false)
    public ApiAuthRegister(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(this.requestMappingHandlerMapping == null){
            return;
        }
//      获取存在配置
        Properties properties = new Properties();
        properties.put("username",NACOS_USERNAME);
        properties.put("password",NACOS_PASSWORD);
        properties.put("serverAddr", NACOS_SERVER_ADDRESS);
        properties.put("namespace",NACOS_NAMESPACE);
        ConfigService configService = NacosFactory.createConfigService(properties);
        String config = configService.getConfig(NACOS_DATAID, NACOS_GROUP, 3000);
        Yaml yaml = new Yaml();
        Map<String, Object> yamlMap = yaml.load(config);
        Map<String, Object> apiMap = (Map<String, Object>) yamlMap.get("api");
        List<String> whiteApiList = (List<String>) apiMap.get("whiteApi");
//      获取整个服务接口
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo requestMappingInfo = entry.getKey();
            Method method = entry.getValue().getMethod();
            if (method.isAnnotationPresent(WhiteApi.class)) {
//              对于占位符替换为*
                String replacedPattern = requestMappingInfo.getActivePatternsCondition().toString().replaceAll("\\{[^}]+\\}", "*");
                String apiUrl = replacedPattern.substring(1,replacedPattern.length()-1);
//              添加不存在的接口
                if (!whiteApiList.contains(apiUrl)) {
                    whiteApiList.add(apiUrl);
                }
            }
        }
        if (whiteApiList.size() == 0) {
            return;
        }
        StringBuilder sb = new StringBuilder("api:\n  whiteApi:\n");
        for (String value : whiteApiList) {
            sb.append("    - ").append(value).append("\n");
        }
        configService.publishConfig(NACOS_DATAID, NACOS_GROUP, sb.toString());
    }
}
