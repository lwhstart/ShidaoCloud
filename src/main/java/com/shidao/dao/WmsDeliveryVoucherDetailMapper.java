package com.shidao.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.WmsDeliveryVoucherDetail;

public interface WmsDeliveryVoucherDetailMapper extends IBatisMapper<WmsDeliveryVoucherDetail>{
  
	WmsDeliveryVoucherDetail getAllAmount(WmsDeliveryVoucherDetail wmsDeliveryVoucherDetail);
	
	void delectByDeliveryVoucherId(@Param(value="deliveryVoucherId")Integer deliveryVoucherId);
	
	BigDecimal getSumAmountDeliveryVoucher(@Param("productId") Integer productId,@Param("startDate") Date startDate,@Param("endDate") Date endDate);
	
	BigDecimal getSumTotalDeliveryVoucher(@Param("productId") Integer productId,@Param("startDate") Date startDate,@Param("endDate") Date endDate);
	
	void deleteByPrimaryKey(@Param(value="id")Integer id);
	
	List<WmsDeliveryVoucherDetail> getDeliveryVoucherDetailList(@Param("productId") Integer productId,@Param("startDate") Date startDate,@Param("endDate") Date endDate);
	
	/**
	 * 加权平均值
	 * @param productId
	 * @return
	 */
	Float getPriceByproductId(@Param(value="productId") Integer productId);
	
	/**
	 * 药房更改药品供应商 
	 * @param productId
	 * @param deliveryDetailId
	 * @param batchNumber
	 */
	void updateVendor(@Param("id") Integer id, @Param("productId") Integer productId);
	
	/**
	 * 根据deliveryVoucherId查询该出库单里出库量大于库存量的productId
	 * @param deliveryVoucherId
	 * @return
	 */
	List<Integer> listProductIdByDeliveryId(Integer deliveryVoucherId);
	
	BigDecimal getSumTotalByDeliveryVoucherId(Integer deliveryVoucherId);
}