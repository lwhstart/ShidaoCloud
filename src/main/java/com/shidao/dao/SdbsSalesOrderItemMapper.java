package com.shidao.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.enums.PayMethod;
import com.shidao.model.SdbsSalesOrderItem;
import com.shidao.vo.ServiceCardCustomerVO;

public interface SdbsSalesOrderItemMapper extends IBatisMapper<SdbsSalesOrderItem>{
	/*
	 * 插入专家处方
	 */
	public Integer insertExpertPrescription(@Param(value = "mainPulse") String mainPulse,
			@Param(value = "subPulse") String subPulse, @Param(value = "diseaseId") Integer diseaseId,
			@Param(value = "salesOrderId") Integer salesOrderId);
	public Float getMaxQuantity (@Param(value="productId") Integer productId);
		
	public List<Map<String, Object>> getSumTotalBysalesOrderId(@Param(value="salesOrderId") Integer salesOrderId);
	
	public Integer deleteBySalesOrderId(Integer salesOrderId);
	
	public List<SdbsSalesOrderItem> listBySaleOrderId(SdbsSalesOrderItem salesOrderItem);
	
	void delectBySalesOrderId(@Param("salesOrderId")Integer salesOrderId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月8日
	 * 功能:加能查询多个订单
	 * @param salesOrderId
	 * @param salesOrderIds
	 * @param category
	 * @return
	 */
	Map<String, Object> getSumTotal(@Param("salesOrderId")Integer salesOrderId,@Param("salesOrderIds")String salesOrderIds,@Param("category")String category);
	
	/**
	 * 不是药费的总消费
	 * @param salesOrderId
	 * @return
	 */
	BigDecimal getNotMedicineTotal(Integer salesOrderId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月6日
	 * 功能:修改功能:每一种药费向上进一位并算出总药费
	 * @param salesOrderId
	 * @param category 
	 * @return
	 */
	BigDecimal getMedicineTotal(@Param("salesOrderId")Integer salesOrderId,@Param("category")String category);
	
	List<ServiceCardCustomerVO>  getServiceCardTotalTime(@Param("salesOrderId")Integer salesOrderId);
	
	void deleteByPrimaryKey(@Param("id")Integer id);
	
	/**
	 * 更新药费的结算方式
	 * @param payMethod
	 * @param category
	 */
	void updateMedicinePaymethod(@Param("payMethod")PayMethod payMethod,@Param("salesOrderId")Integer salesOrderId,@Param("category")String category);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月10日
	 * 功能:更新订单里的会员卡折扣和总额
	 */
	void updateProductDiscountAndTotal(SdbsSalesOrderItem salesOrderItem);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年8月21日
	 * 功能:根据会员卡更新订单的折扣和总价
	 * @param customerMembershipId
	 * @param salesOrderIds
	 */
	void updateDiscountAndTotalByTCM(@Param("customerMembershipId")Integer customerMembershipId,@Param("listSalesOrderId")List<Integer> listSalesOrderId);
	/**
	 * 查询各种类型的药费,groupBy等于1有GROUP BY条件
	 * @param salesOrderId
	 * @return
	 */
	List<SdbsSalesOrderItem> listMedicineTotal(@Param("salesOrderId")Integer salesOrderId,@Param("groupBy")Integer groupBy);
	
	/**
	 * 查询没有作废的订单里的产品数量
	 * @param productId
	 * @return
	 */
	Integer getPaidProductAmount(Integer productId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月2日
	 * 功能:更新药费的折扣
	 * @param id
	 */
	void updateMedicne(SdbsSalesOrderItem salesOrderItem);
	
	List<Map<String, Object>> getCustomerConsumptionItem(@Param("customerId") Integer customerId,@Param("date") Date date,@Param("doctorId") Integer doctorId,@Param("employeeId") Integer employeeId); 
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月20日
	 * 功能:反正不管是啥，药费group by
	 * @param salesOrderIds
	 * @return
	 */
	List<SdbsSalesOrderItem> listGroupMedicine(String salesOrderIds);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年6月27日
	 * 功能:根据salesOrderIds得到所有的产品名字
	 * @param salesOrderIds
	 * @return
	 */
	String getGroupConcatProductName(String salesOrderIds);
}