package com.tml.core.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tml.common.DetectionStatusEnum;
import com.tml.common.constant.ModelConstant;
import com.tml.common.constant.RabbitMQconstant;
import com.tml.common.exception.BaseException;
import com.tml.common.log.AbstractLogger;
import com.tml.config.SystemConfig;
import com.tml.mapper.AuditStatusMapper;
import com.tml.mapper.ModelMapper;
import com.tml.pojo.DO.ModelDO;
import com.tml.pojo.DTO.DetectionStatusDTO;
import com.tml.pojo.DTO.DetectionTaskDTO;
import com.tml.pojo.ResultCodeEnum;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.channels.Channel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/7 16:03
 */
@Component
public class ModelListener implements ListenerInterface{

    @Resource
    AbstractLogger logger;
    @Resource
    RabbitTemplate rabbitTemplate;
    @Resource
    RabbitMQConfig config;
    @Resource
    SystemConfig systemConfig;
    @Resource
    ApplicationContext applicationContext;
    @Resource
    AuditStatusMapper auditStatusMapper;

    private final ConcurrentHashMap<String,Integer> auditCountMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> lockMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String,Map<String,String>> auditParams = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<String>> auditBindMap = new ConcurrentHashMap<>();

    public void setMap(String id,int count){
        this.auditCountMap.put(id,count);
    }
    public void setMap(String id,String value){
        this.lockMap.put(id,value);
    }

    public void setMap(String id,Map<String,String> params){
        this.auditParams.put(id,params);
    }

    public void sendMsgToMQ(DetectionTaskDTO task,String routingKey){
        String exchange = config.getPreCommand()+"."+config.getExchangeType();
        routingKey = config.getPreCommand()+"."+routingKey;
        rabbitTemplate.convertAndSend(exchange,routingKey, JSON.toJSONString(task));
        logger.info(task.toString());
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(),
            exchange = @Exchange(name = "res.topic",type = ExchangeTypes.FANOUT),
            key = "res.text"
    ))
    @Override
    public void receiveText(Message message){
        String messageBody = new String(message.getBody());
        ObjectMapper mapper = new ObjectMapper();
        try {
            DetectionStatusDTO statusDTO = mapper.readValue(messageBody, DetectionStatusDTO.class);
            auditProcessor(statusDTO);
        } catch (JsonProcessingException e) {
            logger.error(e);
            throw new BaseException(e.toString());
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(),
            exchange = @Exchange(name = "res.topic",type = ExchangeTypes.FANOUT),
            key = "res.image"
    ))
    public void receiveImage(Message message){
        String messageBody = new String(message.getBody());
        ObjectMapper mapper = new ObjectMapper();
        try {
            DetectionStatusDTO statusDTO = mapper.readValue(messageBody, DetectionStatusDTO.class);
            auditProcessor(statusDTO);
        } catch (JsonProcessingException e) {
            logger.error(e);
            throw new BaseException(e.toString());
        }
    }

    public void auditProcessor(DetectionStatusDTO statusDTO){
        logger.info(String.valueOf(statusDTO));
        String service = statusDTO.getName().split("-")[0];
        if(!ModelConstant.SERVICE_NAME.equals(service)){
            return;
        }
        if(auditCountMap.get(statusDTO.getId())==null){
            return;
        }
        String lock = statusDTO.getId();
        String table = statusDTO.getName().split("-")[1];
        String filed = "";
        try {
            filed = statusDTO.getName().split("-")[2];
        }catch (RuntimeException e){
        }
        synchronized (lockMap.get(lock)){
            try {
                if(auditCountMap.get(lock)==-1){
                    Object entity;
                    BaseMapper mapper;
                    Class<?> clazz = Class.forName(table);
                    entity = clazz.getDeclaredConstructor().newInstance();
                    mapper = getMapperByEntityType(clazz);
                    UpdateWrapper wrapper;
                    logger.info("[%s]审核失败",statusDTO.getId());
                    try {
                        wrapper = new UpdateWrapper<>(entity);
                        wrapper.eq("id",statusDTO.getId());
                        wrapper.setSql("has_show="+DetectionStatusEnum.DETECTION_FAIL.getStatus());
                        Objects.requireNonNull(mapper).update(null,wrapper);
                        auditStatusMapper.insertAuditStatus(lock,filed,DetectionStatusEnum.DETECTION_FAIL.getStatus().toString());
                    }catch (RuntimeException e){
                        logger.error(e);
                        throw new BaseException(e.toString());
                    }
                } else if(auditCountMap.get(lock)==1){
                    //审核成功
                    Object entity;
                    BaseMapper mapper;
                    Class<?> clazz = Class.forName(table);
                    entity = clazz.getDeclaredConstructor().newInstance();
                    mapper = getMapperByEntityType(clazz);
                    if(mapper==null){
                        throw new BaseException(ResultCodeEnum.SYSTEM_ERROR);
                    }
                    UpdateWrapper wrapper;
                    wrapper = new UpdateWrapper<>(entity);
                    wrapper.eq("id",statusDTO.getId());
                    if(!checkLabelIsLegal(statusDTO.getLabels())){
                        logger.info("[%s]审核失败",statusDTO.getId());
                        wrapper.setSql("has_show="+DetectionStatusEnum.DETECTION_FAIL.getStatus());
                        return;
                    }
                    //判断是否有子审核
                    if(auditBindMap.get(lock)!=null){
                        //判断子审核是否审核完毕
                        CountDownLatch latch = new CountDownLatch(auditBindMap.get(lock).size());
                        for (String sonId : auditBindMap.get(lock)) {
                            if(auditCountMap.get(sonId)==-1){
                                logger.info("[%s]审核失败",statusDTO.getId());
                                wrapper.setSql("has_show="+DetectionStatusEnum.DETECTION_FAIL.getStatus());
                                auditStatusMapper.insertAuditStatus(lock,filed,DetectionStatusEnum.DETECTION_FAIL.getStatus().toString());
                                return;
                            }
                            if(auditCountMap.get(sonId)!=0){
                                try {
                                    while (!latch.await(1, TimeUnit.SECONDS)) {
                                        if (auditCountMap.get(sonId) == 0) {
                                            break;
                                        }
                                    }
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    logger.error("等待子审核被中断", e);
                                }
                            }
                        }

                    }
                    logger.info("[%s]审核成功,更新数据库",statusDTO.getId());
                    wrapper.setSql("has_show="+ DetectionStatusEnum.DETECTION_SUCCESS.getStatus());
                    //后置步骤，例如修改需要额外修改字段参数
                    if(auditParams.get(statusDTO.getId())!=null){
                        for (String s : auditParams.get(statusDTO.getId()).keySet()) {
                            wrapper.set(s,auditParams.get(statusDTO.getId()).get(s));
                        }
                    }
                    mapper.update(null,wrapper);
                    auditCountMap.compute(lock,(key,val)-> 0);
                }else if(auditCountMap.get(lock)>0){
                    //优化判断逻辑
                    if(!checkLabelIsLegal(statusDTO.getLabels())){
                        logger.info("[%s]审核失败:%s",statusDTO.getId(),statusDTO.getLabels());
                        auditCountMap.compute(statusDTO.getId(),(key, value)-> -1);
                        auditStatusMapper.insertAuditStatus(lock,filed,DetectionStatusEnum.DETECTION_FAIL.getStatus().toString());
                        return;
                    }
                    auditCountMap.compute(statusDTO.getId(), (key, value) -> value - 1);
                }
            } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                     IllegalAccessException | NoSuchMethodException e) {
                logger.error(e);
                throw new BaseException(e.toString());
            }
        }

    }

    public boolean checkLabelIsLegal(String label){
        String[] labels = systemConfig.getAllowLabels();
        logger.info(Arrays.toString(labels));
        for (String s : labels) {
            if(s.equals(label)){
                return true;
            }
        }
        return false;
    }


    private  <T> BaseMapper<T> getMapperByEntityType(Class<T> entityType) {
        Map<String, BaseMapper> mappers = applicationContext.getBeansOfType(BaseMapper.class);
        String name = entityType.getName();
        String entityDO = name.substring(name.lastIndexOf(".")+1);
        entityDO = entityDO.substring(0,1).toLowerCase()+entityDO.substring(1);
        entityDO = entityDO.replace("DO","");
        String entityDOMapper = entityDO+"Mapper";
        return mappers.get(entityDOMapper);
    }

}
