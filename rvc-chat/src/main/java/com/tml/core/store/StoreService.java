package com.tml.core.store;

import java.util.List;
import java.util.Set;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/22 14:50
 */
public interface StoreService {

    /**
     * 订阅
     * @param consumerId 订阅者id
     * @param producerIds 被订阅者id 列表
     */
    void subscribe(String consumerId, String... producerIds);
    /**
     * 订阅
     * @param consumerId 订阅者id
     * @param producerIds 被订阅者id 列表
     */
    void subscribe(String consumerId, Set<String> producerIds);

    /**
     * 取消订阅
     * @param consumerId 订阅者id
     * @param producerId 被订阅者id
     */
    void unsubscribe(String consumerId, String producerId);

    /**
     * 被订阅者取消所有订阅者
     * @param producerId 被订阅者id
     */
    void removeAllSubscribeByBeSubscribe(String producerId);

    /**
     * 列出订阅者
     * @param producerId 被订阅者id
     * @return 订阅者列表
     */
    List<String> listBySubscribe(String producerId);

}
