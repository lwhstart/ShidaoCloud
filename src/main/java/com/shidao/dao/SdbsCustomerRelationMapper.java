package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsCustomerRelation;

public interface SdbsCustomerRelationMapper extends IBatisMapper<SdbsCustomerRelation>{
	
	void insertSelect(SdbsCustomerRelation relation);
}