package com.tml.designpattern.strategy.impl;

import com.tml.designpattern.strategy.SortStrategy;
import com.tml.pojo.vo.PostVo;

import java.util.Comparator;
import java.util.List;

public class TimeSortStrategy implements SortStrategy {
    @Override
    public void sort(List<PostVo> posts) {
        // 根据时间排序
        posts.sort(Comparator.comparing(PostVo::getCreateAt));
    }
}