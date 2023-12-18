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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.channels.Channel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

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

    private final ConcurrentHashMap<String,Integer> auditCountMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> lockMap = new ConcurrentHashMap<>();

    public void setMap(String id,int count){
        this.auditCountMap.put(id,count);
    }
    public void setMap(String id,String value){
        this.lockMap.put(id,value);
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
        synchronized (lockMap.get(lock)){
            try {
                if(auditCountMap.get(lock)==-1){
                    Object entity;
                    BaseMapper mapper;
                    Class<?> clazz = Class.forName(table);
                    entity = clazz.getDeclaredConstructor().newInstance();
                    mapper = getMapperByEntityType(clazz);
                    UpdateWrapper wrapper;
                    logger.info("审核[%s]失败",statusDTO.getId());
                    try {
                        wrapper = new UpdateWrapper<>(entity);
                        wrapper.eq("id",statusDTO.getId());
                        wrapper.setSql("has_show="+DetectionStatusEnum.DETECTION_FAIL.getStatus());
                        Objects.requireNonNull(mapper).update(null,wrapper);
                    }catch (RuntimeException e){
                        logger.error(e);
                        throw new BaseException(e.toString());
                    }

                } else if(auditCountMap.get(lock)==1){
                    Object entity;
                    BaseMapper mapper;
                    Class<?> clazz = Class.forName(table);
                    entity = clazz.getDeclaredConstructor().newInstance();
                    mapper = getMapperByEntityType(clazz);
                    UpdateWrapper wrapper;
                    logger.info("[%s]审核完毕,更新数据库",statusDTO.getId());
                    wrapper = new UpdateWrapper<>(entity);
                    if(checkLabelIsLegal(statusDTO.getLabels())){
                        wrapper.setSql("has_show="+ DetectionStatusEnum.DETECTION_SUCCESS.getStatus());
                        wrapper.eq("id",statusDTO.getId());
                    }else {
                        wrapper.eq("id",statusDTO.getId());
                        wrapper.setSql("has_show="+DetectionStatusEnum.DETECTION_FAIL.getStatus());
                    }
                    if(mapper==null){
                        throw new BaseException();
                    }
                    mapper.update(null,wrapper);
                }else if(auditCountMap.get(lock)>0){
                    //优化判断逻辑
                    if(!checkLabelIsLegal(statusDTO.getLabels())){
                        auditCountMap.compute(statusDTO.getId(), (key, value) -> -1);
                        return;
                    }
                    auditCountMap.compute(statusDTO.getId(), (key, value) -> {
                        if (value == null) {
                            throw new BaseException(ResultCodeEnum.PARAM_ID_IS_ERROR);
                        }
                        return value - 1;
                    });
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
