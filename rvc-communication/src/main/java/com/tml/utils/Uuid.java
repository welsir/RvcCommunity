package com.tml.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @NAME: Uuid
 * @USER: yuech
 * @Description:
 * @DATE: 2023/11/29
 */
@Component
public class Uuid {
    private Integer workerId = 0;
    private Integer datacenterId = 1;
    private final Snowflake snowflake = new Snowflake(workerId, datacenterId);
    private static volatile Uuid instance = null;

    private Uuid() {
    };
    public static synchronized Uuid getInstance() {
        if (instance == null) {
            synchronized (Uuid.class) {
                if (instance == null) {
                    instance = new Uuid();
                }
            }
        }
        return instance;
    }


    @PostConstruct  //构造后开始执行，加载初始化工作
    public void init() {
        try {
            //获取本机的ip地址编码
            workerId = Math.toIntExact(NetUtil.ipv4ToLong(NetUtil.getLocalhostStr()));
//            log.info("当前机器的workerId: " + workerId);
        } catch (Exception e) {
//            log.warn("当前机器的workerId获取失败 ----> " + e);
            workerId = NetUtil.getLocalhostStr().hashCode();
        }
    }

    public synchronized long snowflakeId() {
        return snowflake.nextId();
    }

    public synchronized long snowflakeId(long workerId, long datacenterId) {
        Snowflake snowflake = new Snowflake(workerId, datacenterId);
        return snowflake.nextId();
    }

    //测试
    public static String getUuid() {
        return new String(String.valueOf((getInstance().snowflakeId())));
        //1277896081711169536
    }
}