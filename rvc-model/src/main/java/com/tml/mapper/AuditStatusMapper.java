package com.tml.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tml.domain.DO.AuditStatusDO;
import org.apache.ibatis.annotations.Insert;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/21 23:25
 */
public interface AuditStatusMapper extends BaseMapper<AuditStatusDO> {


    @Insert("insert into rvc_model_status (id,filed,status) values (#{id},#{filed}.#{status})")
    int insertAuditStatus(String id,String filed,String status);
}
