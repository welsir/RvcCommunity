package com.tml.core.factory.mqFactory;

import com.tml.domain.DTO.DetectionTaskDTO;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/15 8:13
 */
public interface BaseMqProductorInterface<T extends DetectionTaskDTO> {

    void sendMsgToMq(T task,String type);
}
