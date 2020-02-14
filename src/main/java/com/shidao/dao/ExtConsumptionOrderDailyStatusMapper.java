package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.ExtConsumptionOrderDailyStatus;
public interface ExtConsumptionOrderDailyStatusMapper extends IBatisMapper<ExtConsumptionOrderDailyStatus> {
    int deleteByPrimaryKey(Integer id);

    int insert(ExtConsumptionOrderDailyStatus record);

    ExtConsumptionOrderDailyStatus selectByPrimaryKey(Integer id);
   
    int updateByPrimaryKey(ExtConsumptionOrderDailyStatus record);
    
    
    /**
               *     根据3个基本判断返回状态
     * @param status
     * @return
     * @author lwh data:2020年1月14日
     */
    ExtConsumptionOrderDailyStatus selectDailyStatus(ExtConsumptionOrderDailyStatus record);
}