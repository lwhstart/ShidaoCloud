package com.shidao.dao;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.WmsPurchaseOrderDetail;

public interface WmsPurchaseOrderDetailMapper extends IBatisMapper<WmsPurchaseOrderDetail>{
	BigDecimal getSumTotal(@Param(value="purchaseOrderId") Integer purchaseOrderId);

	void deleteByPrimaryKey(Integer id);
	
	void deleteByPurchaseOrderId(Integer purchaseOrderId);
}