package com.shidao.dao;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.WmsPurchaseOrder;

public interface WmsPurchaseOrderMapper extends IBatisMapper<WmsPurchaseOrder>{
	void deleteByPrimaryKey(@Param(value="id")Integer id);
	
	
	/**
	 * 自动采购-插入采购详情表
	 * @param warehouseId
	 * @param purchaseOrderId
	 */
	void autoInsertByPurchase(@Param("warehouseId")Integer warehouseId,@Param("purchaseOrderId")Integer purchaseOrderId);
}