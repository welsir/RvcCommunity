package com.tml.strategy;

import com.tml.pojo.vo.PostVo;

import java.util.List;

public interface SortStrategy {
    void sort(List<PostVo> posts);
}
