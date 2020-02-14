package com.shidao.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsAppointmentD2d;
import com.shidao.vo.appointmentD2dStatisticsVo;

public interface SdbsAppointmentD2dMapper extends IBatisMapper<SdbsAppointmentD2d> {

	/**
	 * 
	 * @param companyId
	 * @param date
	 * @param period
	 * @param isFuture(是否大于当前时间)
	 * @return
	 */
	public List<appointmentD2dStatisticsVo> getAppointmentStatistics(@Param(value = "companyId") Integer companyId,
			@Param(value = "date") Date date, @Param(value = "period") String period,
			@Param(value = "isFuture") Integer isFuture, @Param(value = "customerId") Integer customerId);

	/**
	 * 
	 * @param customerId
	 * @return
	 */
	public List<SdbsAppointmentD2d> getByCustomerId(@Param(value = "customerId") Integer customerId);

	public List<String> getAppointmentD2dList(@Param(value = "orderId") Integer orderId);
}