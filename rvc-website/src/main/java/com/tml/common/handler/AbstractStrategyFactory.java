package com.tml.common.handler;

import com.google.protobuf.ServiceException;
import io.github.common.logger.CommonLogger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class AbstractStrategyFactory implements InitializingBean {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private CommonLogger commonLogger;

    private final Map<String,HandlerStrategy> strategyMap = new HashMap<>();
    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, HandlerStrategy> beansOfType = applicationContext.getBeansOfType(HandlerStrategy.class);
        beansOfType.forEach((k,v)->{
            String name = v.name();
            if (strategyMap.containsKey(name)) {
                commonLogger.error("Duplicate handler strategy name:{}", name);
            }else{
                strategyMap.put(name,v);
            }
        });
    }

    public HandlerStrategy choose(String name) throws ServiceException {
        return Optional.ofNullable(strategyMap.get(name)).orElseThrow(() -> new ServiceException(String.format("[%s] 策略未定义", name)));
    }

    public <PARAMS> void handler(String name,PARAMS params) throws ServiceException {
        choose(name).handler(params);
    }

    public  <PARAMS,RES> RES handlerRes(String name,PARAMS params) throws ServiceException{
        return (RES) choose(name).handlerRes(params);
    }
}
