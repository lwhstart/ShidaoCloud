package com.shidao.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsCustomerInfo;
import com.shidao.util.ValidList.ValidListCategory;

public interface SdbsCustomerInfoMapper extends IBatisMapper<SdbsCustomerInfo> {
    int deleteByPrimaryKey(Integer id);

    int insert(SdbsCustomerInfo record);

    Integer insertSelective(SdbsCustomerInfo record);

    SdbsCustomerInfo selectByPrimaryKey(Integer id);

    Integer updateByPrimaryKeySelective(SdbsCustomerInfo record);

    int updateByPrimaryKey(SdbsCustomerInfo record);
    
    /**
     * 获得客户设置中，选择了validList表的哪些值。
     * @param customerId
     * @param clubId
     * @param category
     * @return, Created at 2019年8月13日
     * @auto yzl
     *
     */
    List<Map<String,Object>> listValidListOfCategory(@Param("customerUuid") String customerUuid, 
    		@Param("clubId") Integer clubId, @Param("category")ValidListCategory category);
    
}