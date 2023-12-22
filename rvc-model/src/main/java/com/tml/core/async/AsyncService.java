package com.tml.core.async;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.common.constant.ModelConstant;
import com.tml.common.exception.BaseException;
import com.tml.common.log.AbstractLogger;
import com.tml.core.rabbitmq.ModelListener;
import com.tml.mapper.LabelMapper;
import com.tml.mapper.ModelMapper;
import com.tml.pojo.DO.LabelDO;
import com.tml.pojo.DO.ModelDO;
import com.tml.pojo.DTO.AsyncDetectionForm;
import com.tml.pojo.DTO.DetectionTaskDTO;
import com.tml.pojo.ResultCodeEnum;
import com.tml.utils.DateUtil;
import io.github.id.snowflake.SnowflakeGenerator;
import io.github.id.snowflake.SnowflakeRegisterException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/7 22:13
 */
@Service
public class AsyncService {

    @Resource
    AbstractLogger logger;
    @Resource
    ModelListener listener;
    @Resource
    ModelMapper mapper;
    @Resource
    LabelMapper labelMapper;
    @Resource
    SnowflakeGenerator snowflakeGenerator;

    private  static final ConcurrentHashMap<String,Integer> modelViews = new ConcurrentHashMap<>();
    @Async
    public void processModelAsync(ModelDO modelDO,List<String> labels) {
        try {
            ArrayList<String> labelAudit = new ArrayList<>();
            //判断label是否存在
            for (String label : labels) {
                LabelDO labelDO = new LabelDO();
                try {
                    labelDO.setId(snowflakeGenerator.generate());
                    labelDO.setLabel(label);
                    labelDO.setHasShow("0");
                    labelDO.setCreateTime(DateUtil.formatDate());
                    labelMapper.labelIsExit(labelDO);
                    labelAudit.add(label);
                } catch (SnowflakeRegisterException e) {
                    throw new BaseException(e.toString());
                } catch (DuplicateKeyException e){
                }
            }

            String auditId = String.valueOf(modelDO.getId());
            String name = ModelConstant.SERVICE_NAME +"-com.tml.pojo.DO.ModelDO";
            DetectionTaskDTO build = DetectionTaskDTO.builder()
                    .id(auditId)
                    .content(modelDO.getDescription())
                    .name(name+"-description")
                    .build();
            listener.setMap(auditId,4+labelAudit.size());
            listener.setMap(auditId,auditId);
            listener.sendMsgToMQ(build,"text");
            build.setContent(modelDO.getName());
            build.setName(name+"-name");
            listener.sendMsgToMQ(build,"text");
            build.setContent(modelDO.getNote());
            build.setName(name+"-note");
            listener.sendMsgToMQ(build,"text");
            build.setContent(modelDO.getPicture());
            build.setName(name+"-picture");
            listener.sendMsgToMQ(build,"image");
            String labelName = ModelConstant.SERVICE_NAME+"-com.tml.pojo.DO.labelDO";
            //审核用户自建label
            for (String label : labelAudit) {
                DetectionTaskDTO audit = DetectionTaskDTO.builder()
                        .id(auditId)
                        .content(label)
                        .name(labelName + "-label")
                        .build();
                listener.sendMsgToMQ(audit,"text");
            }
            logger.info("异步审核完毕");
        } catch (Exception e) {
            // 异常处理
            logger.error("调用审核服务失败%s:%s ", e.getMessage(), e);
            throw new BaseException();
        }

    }

    @Async
    public void asyncAddModelViewNums(String modelId){
        try {
            modelViews.compute(modelId,(key,val)->{
                if(val==null){
                    val=1;
                    return val;
                }
                return val+1;
            });
        }catch (RuntimeException e){
            logger.error(e);
            throw new BaseException(ResultCodeEnum.UPDATE_MODEL_VIEWS_FAIL);
        }
    }

    @Async
    public void listenerMq(List<AsyncDetectionForm> listenList){
        for (AsyncDetectionForm asyncdetectionForm : listenList) {
            String type = asyncdetectionForm.getType();
            listener.setMap(asyncdetectionForm.getTaskDTO().getId(),listenList.size());
            listener.setMap(asyncdetectionForm.getTaskDTO().getId(),ModelConstant.SERVICE_NAME+"-"+asyncdetectionForm.getTaskDTO().getName());
            listener.sendMsgToMQ(asyncdetectionForm.getTaskDTO(),type);
        }
    }

}
