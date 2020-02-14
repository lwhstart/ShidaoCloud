package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.ExtConsumptionOrderDailyStatusHistory;

public interface ExtConsumptionOrderDailyStatusHistoryMapper extends IBatisMapper<ExtConsumptionOrderDailyStatusHistory> {
    int deleteByPrimaryKey(Integer id);

    int insert(ExtConsumptionOrderDailyStatusHistory record);

    ExtConsumptionOrderDailyStatusHistory selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(ExtConsumptionOrderDailyStatusHistory record);
}