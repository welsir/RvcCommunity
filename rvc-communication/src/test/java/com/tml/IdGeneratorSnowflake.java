package com.tml;
 
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import com.tml.utils.Uuid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
 
import javax.annotation.PostConstruct;
 
@Slf4j
@Component
public class IdGeneratorSnowflake {
 
    private long workerId = 0;  //第几号机房
    private final long datacenterId = 1;  //第几号机器
    private final Snowflake snowflake = new Snowflake(workerId, datacenterId);
 
    @PostConstruct  //构造后开始执行，加载初始化工作
    public void init() {
        try {
            //获取本机的ip地址编码
            workerId = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());
            log.info("当前机器的workerId: " + workerId);
        } catch (Exception e) {
            log.warn("当前机器的workerId获取失败 ----> " + e);
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
    public static void main(String[] args) {
        System.out.println( Uuid.getUuid());
    }
}
 