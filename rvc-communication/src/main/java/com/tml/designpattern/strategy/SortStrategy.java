package com.tml.designpattern.strategy;

import com.tml.domain.vo.PostVo;

import java.util.List;

public interface SortStrategy {
    void sort(List<PostVo> posts);
}
