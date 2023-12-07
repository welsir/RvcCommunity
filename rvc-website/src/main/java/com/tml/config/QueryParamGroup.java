package com.tml.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

@Component
public class QueryParamGroup {


    @Value("#{${rvc.query}}")
    private Map<String,String> query;

    public String getQueryParams(String queryType){

        return Optional.ofNullable(query.get(queryType)).orElse("");
    }
}
