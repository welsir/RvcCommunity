package com.tml.designpattern.strategy.impl;

import com.tml.domain.vo.PostVo;
import com.tml.designpattern.strategy.SortStrategy;

import java.util.Comparator;
import java.util.List;

public class ViewSortStrategy implements SortStrategy {
    @Override
    public void sort(List<PostVo> posts) {
        // 根据浏览量排序
        posts.sort(Comparator.comparing(PostVo::getWatchNum));
    }
}