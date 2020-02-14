package com.shidao.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.WmsWarehouseVoucherDetail;

public interface WmsWarehouseVoucherDetailMapper extends IBatisMapper<WmsWarehouseVoucherDetail>{
	
	void deleteByPrimaryKey(Integer id);
	
	void deleteByOrderId(Integer orderId);
	
	BigDecimal getSumTotal(Integer orderId);
	
	WmsWarehouseVoucherDetail getSumWarehouseVoucher(WmsWarehouseVoucherDetail wmsWarehouseVoucherDetail);
	
	BigDecimal getSumAmountWarehouseVoucher(@Param("productId") Integer productId,@Param("startDate") Date startDate,@Param("endDate") Date endDate);
	
	BigDecimal getSumTotalWarehouseVoucher(@Param("productId") Integer productId,@Param("startDate") Date startDate,@Param("endDate") Date endDate);
	//根据productId、startDate、endDate查询所有出库入库详情
	List<WmsWarehouseVoucherDetail> getWarehouseDeliveryDetailList(@Param("productId") Integer productId,@Param("startDate") Date startDate,@Param("endDate") Date endDate);
	
	/**
	 * 从采购单插入入库单
	 * @param orderId
	 * @param purchaseOrderId
	 */
	void insertByPurchase(@Param("orderId")Integer orderId,@Param("purchaseOrderId")Integer purchaseOrderId);
}