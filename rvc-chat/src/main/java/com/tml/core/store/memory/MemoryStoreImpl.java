package com.tml.core.store.memory;

import com.tml.core.store.StoreService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description
 * @Author welsir
 * @Date 2024/2/22 14:49
 */
@Component
public class MemoryStoreImpl implements StoreService {

    private final ConcurrentHashMap<String, Set<String>> producerCache = new ConcurrentHashMap<>();

    @Override
    public void subscribe(String consumerId, String... producerIds) {
        for (String producerId : producerIds) {
            producerCache.computeIfAbsent(producerId, (key) -> new HashSet<>()).add(consumerId);
        }
    }

    @Override
    public void subscribe(String consumerId, Set<String> producerIds) {
        for (String producerId : producerIds) {
            producerCache.computeIfAbsent(producerId, (key) -> new HashSet<>()).add(consumerId);
        }
    }

    @Override
    public void unsubscribe(String consumerId, String producerId) {
        producerCache.computeIfAbsent(producerId, (key) -> new HashSet<>()).remove(consumerId);
    }

    @Override
    public void removeAllSubscribeByBeSubscribe(String producerId) {
        producerCache.remove(producerId);
    }

    @Override
    public List<String> listBySubscribe(String producerId) {
        return null;
    }
}
