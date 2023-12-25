package com.tml.core.factory.mqFactory;

import com.tml.domain.DTO.DetectionTaskDTO;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/15 8:10
 */

public abstract class AbstractMqOperator {


    public abstract void sendMsgToMq(DetectionTaskDTO dto);


}
