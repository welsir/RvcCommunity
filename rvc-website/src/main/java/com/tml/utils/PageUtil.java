package com.tml.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.common.web.PageVO;

import java.util.ArrayList;

/**
 * @author Genius
 * @date 2023/10/06 19:38
 **/
public class PageUtil<T>{

    /**
     * 将MybatisPlus的IPage封装为 PageVO
     * @param page
     * @return
     */
    public PageVO<T> toPageVO(IPage<T> page){
        return new PageVO<T>(
                page.getRecords(),
                page.getCurrent(),
                page.getSize(),
                page.getTotal()
        );
    }

    public PageVO<T> emptyPageVO(){
        return new PageVO<T>(new ArrayList<T>(), -1, 0, 0);
    }

}
