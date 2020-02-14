package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsCustomerMembershipItem;

public interface SdbsCustomerMembershipItemMapper extends IBatisMapper<SdbsCustomerMembershipItem>{
	void deleteById(Integer id);
	
	void insertSelectServiceCardItem(@Param("serviceCardId")Integer serviceCardId);
	
	/**
	 * customerMembershipId不能为空
	 * serviceId可以不为空，不为空时当成where条件
	 * @param customerMembershipItem
	 */
	void updateBycustomerMemberShipId(SdbsCustomerMembershipItem customerMembershipItem);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年10月26日
	 * 功能:
	 * @param list
	 */
	void insertBatch(List<SdbsCustomerMembershipItem> list);
	
	void deleteByMembershipId(Integer customerMembershipId);
}