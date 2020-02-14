package com.shidao.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.CrmCustomerMaintaince;

public interface CrmCustomerMaintainceMapper extends IBatisMapper<CrmCustomerMaintaince>{
    void deleteByPrimaryKey(Integer id);
    
    void insertCustomerMaintaince();
        /**
     * @author 创建人:liupengyuan,时间:2018年6月29日
     * 功能:脱失统计
     * @return
     */
    List<CrmCustomerMaintaince> getLostStatistics(@Param("dateStart")Date dateStart,@Param("dateEnd")Date dateEnd);
}