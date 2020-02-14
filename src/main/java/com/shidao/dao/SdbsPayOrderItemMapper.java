package com.shidao.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsPayOrderItem;
import com.shidao.vo.ServiceCardCustomerVO;

public interface SdbsPayOrderItemMapper extends IBatisMapper<SdbsPayOrderItem>{
	
	public List<SdbsPayOrderItem> getSummaryInfo(SdbsPayOrderItem payOrderItem);
	
	public List<SdbsPayOrderItem> getDoctorProduct(SdbsPayOrderItem payOrderItem);
	
	/**
	 * 根据医生ID查询医生会员统计
	 * @param doctorId
	 * @return
	 */
	List<ServiceCardCustomerVO> listserviceCardStatisticsByDoctorId(@Param("doctorId")Integer doctorId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月8日
	 * 功能:支付过后的金额和支付方式
	 * @param salesOrderId
	 * @param salesOrderIds
	 * @param payMethod
	 * @param notPayMethod
	 * @return
	 */
	List<SdbsPayOrderItem> listPayAfter(@Param("salesOrderId") Integer salesOrderId,@Param("salesOrderIds")String salesOrderIds,@Param("payMethod")String payMethod,@Param("notPayMethod")String notPayMethod);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月7日
	 * 功能:查询多个订单的支付方式和总金额
	 * @param payOrderIds
	 * @param salesOrderIds
	 * @return
	 */
	List<SdbsPayOrderItem> listPayMethodAndAmount(@Param("payOrderIds") List<Integer> payOrderIds,@Param("salesOrderIds")String salesOrderIds);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月5日
	 * 功能:查询客户某个日期多个订单的支付金额 不包括套餐卡
	 * @param customerId
	 * @param date
	 * @return
	 */
	List<SdbsPayOrderItem> listPayAfterByCustomerIdAndDate(@Param("customerId")Integer customerId,@Param("date")Date date);
}