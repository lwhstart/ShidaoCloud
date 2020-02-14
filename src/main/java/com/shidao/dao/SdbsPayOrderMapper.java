package com.shidao.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.enums.PayMethod;
import com.shidao.model.SdbsPayOrder;

public interface SdbsPayOrderMapper extends IBatisMapper<SdbsPayOrder>{
	
	SdbsPayOrder selectBySalesOrderId(Integer salesOrderId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月7日
	 * 功能:多个订单的总额
	 * @param payOrderIds
	 * @return
	 */
	Float getAmountByIds(@Param("payOrderIds")List<Integer> payOrderIds,@Param("salesOrderIds") String salesOrderIds);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月10日
	 * 功能:获取数量
	 * @param salesOrderIds
	 * @return
	 */
	Integer getCountBySalesOrderIds(@Param("salesOrderIds") String salesOrderIds);
	
	void networkPay(@Param("salesOrderId") Integer salesOrderId,@Param("payMethod") PayMethod payMethod,@Param("payAmount") BigDecimal payAmount);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年9月20日
	 * 功能:
	 * @param customerId
	 * @return
	 */
	LocalDate getMaxPayDateByCustomerId(Integer customerId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年9月20日
	 * 功能:
	 * @param customerId
	 * @param date
	 * @return
	 */
	Map<String, LocalDate> getSidePayDateByCustomerId(@Param("customerId")Integer customerId,@Param("date")LocalDate date);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年9月20日
	 * 功能:获取小程序消费数据
	 * @param customerId
	 * @param date
	 * @return
	 */
	SdbsPayOrder getPayOrderHistory(@Param("customerId")Integer customerId,@Param("date")LocalDate date);
}