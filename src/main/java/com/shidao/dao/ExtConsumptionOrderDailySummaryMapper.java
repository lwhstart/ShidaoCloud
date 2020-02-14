package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.ExtConsumptionOrderDailySummary;

public interface ExtConsumptionOrderDailySummaryMapper extends IBatisMapper<ExtConsumptionOrderDailySummary> {
    int deleteByPrimaryKey(Integer id);
}