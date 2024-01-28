package com.tml.core.async;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tml.common.exception.BaseException;
import com.tml.common.log.AbstractLogger;
import com.tml.mapper.LabelMapper;
import com.tml.mapper.ModelMapper;
import com.tml.pojo.DO.LabelDO;
import com.tml.pojo.DO.ModelDO;
import com.tml.pojo.ResultCodeEnum;
import com.tml.utils.DateUtil;
import io.github.id.snowflake.SnowflakeGenerator;
import io.github.id.snowflake.SnowflakeRegisterException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.tml.common.DetectionStatusEnum.DETECTION_SUCCESS;
import static com.tml.common.DetectionStatusEnum.UN_DETECTION;

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
    ModelMapper mapper;
    @Resource
    LabelMapper labelMapper;
    @Resource
    SnowflakeGenerator snowflakeGenerator;

    private  static final ConcurrentHashMap<String,Integer> modelViews = new ConcurrentHashMap<>();
    @Async
    public void processModelAsync(ModelDO modelDO,List<String> labels) {
        try {
            //判断label是否存在
            for (String label : labels) {
                LabelDO labelDO = new LabelDO();
                try {
                    LocalDateTime lt = LocalDateTime.now();
                    labelDO.setId(snowflakeGenerator.generate());
                    labelDO.setLabel(label);
                    labelDO.setHasShow(DETECTION_SUCCESS.getStatus().toString());
                    labelDO.setHot(0L);
                    labelDO.setCreateTime(lt);
                    labelMapper.labelIsExit(labelDO);
                } catch (SnowflakeRegisterException e) {
                    throw new BaseException(e.toString());
                } catch (DuplicateKeyException e){
                    labelMapper.updateHot(label);
                    labelMapper.insertLabelModel(modelDO.getId().toString(),label);
                }
            }
        } catch (Exception e) {
            // 异常处理
            logger.error(e);
            throw new BaseException(e.toString());
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

    @Scheduled(fixedRate = 10000)
    public void clearMap() {
        if (modelViews.size() == 0) {
            return;
        }
        modelViews.forEach(
                (key,val)->{
                    Long nums = mapper.queryModelViesNums(key);
                    UpdateWrapper<ModelDO> wrapper = new UpdateWrapper<>();
                    wrapper.eq("id",key);
                    nums +=val;
                    wrapper.setSql("view_num = "+nums);
                    mapper.update(null,wrapper);
                }
        );
        modelViews.clear();
    }
}
