package com.shidao.dao;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsCustomerServiceGroup;

public interface SdbsCustomerServiceGroupMapper extends IBatisMapper<SdbsCustomerServiceGroup>{
    void deleteByPrimaryKey(Integer id);
    
    void insertByforwardUuid(@Param("customerId") Integer customerId,@Param("forwardUuid") String forwardUuid);
    
    void updateByReplaceCustomerId(@Param("newCustomerId") Integer newCustomerId,@Param("oldCustomerId") Integer oldCustomerId);
}