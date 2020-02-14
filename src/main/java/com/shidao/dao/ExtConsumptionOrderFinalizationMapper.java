package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.ExtConsumptionOrderFinalization;

public interface ExtConsumptionOrderFinalizationMapper extends IBatisMapper<ExtConsumptionOrderFinalization> {
	
    int deleteByPrimaryKey(Integer id);


}