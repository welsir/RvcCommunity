package com.tml.strategy.impl;

import com.tml.pojo.vo.PostVo;
import com.tml.strategy.SortStrategy;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LikeSortStrategy implements SortStrategy {
    @Override
    public void sort(List<PostVo> posts) {
        // 根据点赞排序
//        posts.sort(Comparator.comparingInt(PostVo::getLikeNum).reversed());
//        Collections.sort(posts, Comparator.comparingInt(PostVo::getLikeNum).reversed());
        posts.sort(Comparator.comparing(PostVo::getLikeNum));
    }
}