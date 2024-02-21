package com.tml.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.domain.entity.RvcLevelTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
* @author yuech
* @description 针对表【rvc_level_task】的数据库操作Mapper
* @createDate 2024-01-30 20:58:52
* @Entity com.tml.domain.entity.RvcLevelTask
*/
@Mapper
public interface RvcLevelTaskMapper extends RvcBaseMapper<RvcLevelTask> {
    RvcLevelTask getOne(@Param("ew") QueryWrapper<RvcLevelTask> wrapper);

}
