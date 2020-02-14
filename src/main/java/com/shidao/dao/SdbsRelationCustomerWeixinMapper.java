package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsRelationCustomerWeixin;

public interface SdbsRelationCustomerWeixinMapper extends IBatisMapper<SdbsRelationCustomerWeixin> {

	SdbsRelationCustomerWeixin selectByOpenid(String openId);
	
	void updateByOpenId(SdbsRelationCustomerWeixin customerWeixin);
	
	void deleteByOpenId(String openId);
}
