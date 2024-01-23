package com.tml.mq.handler;

import com.tml.mq.ReceiveHandler;
import com.tml.pojo.dto.DetectionStatusDto;
import com.tml.designpattern.strategy.DetectionProcessStrategy;
import com.tml.designpattern.strategy.impl.CommentProcessStrategy;
import com.tml.designpattern.strategy.impl.CoverProcessStrategy;
import com.tml.designpattern.strategy.impl.PostProcessStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.tml.enums.ContentDetectionEnum.*;

/**
 * @NAME: ProcessReceive
 * @USER: yuech
 * @Description:
 * @DATE: 2023/12/19
 */
@Component
@Slf4j
public class ProcessReceive extends ReceiveHandler {

    private final Map<String, DetectionProcessStrategy> strategyMap = new HashMap<>();

    @Autowired
    public ProcessReceive(CoverProcessStrategy coverProcessStrategy, CommentProcessStrategy commentProcessStrategy, PostProcessStrategy postProcessStrategy) {
        strategyMap.put(POST_COVER.getFullName() ,coverProcessStrategy);
        strategyMap.put(COMMENT.getFullName(),commentProcessStrategy);
        strategyMap.put(POST_CONTENT.getFullName(),postProcessStrategy);
    }

    @Override
    public void process(DetectionStatusDto detectionTaskDto) {
        ////处理逻辑  更新数据库
        DetectionProcessStrategy detectionProcessStrategy = strategyMap.get(detectionTaskDto.getName());
        //如果没有的话就是其他服务的处理  直接放行
        if(Objects.isNull(detectionProcessStrategy)){
            return;
        }
////处理逻辑  更新数据库
        detectionProcessStrategy.process(detectionTaskDto);
    }
}