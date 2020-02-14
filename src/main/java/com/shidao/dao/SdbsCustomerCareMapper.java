package com.shidao.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsCustomerCare;

public interface SdbsCustomerCareMapper extends IBatisMapper<SdbsCustomerCare> {

	/**
	 * 插入客户关怀，2、5、9天的关怀，如果已经存在关怀则不插入。
	 *	@param appointmentId
	 * @author yzl , Created at 2019年10月11日
	 *
	 */
	public void insertCareByAppointmentId(Integer appointmentId);
	
	public void deleteByAppointmentId(@Param(value="id") Integer id);
	
	void deleteByAppointmentIdAndType(@Param("appointmentId")Integer appointmentId,@Param("type")String type);
	
	List<Map<String, Object>> getCountCareByDate(@Param("startDate") Date startDate,@Param("stopDate") Date stopDate);

	List<Map<String, Object>> getCareStatisticsByDate(@Param("clubId") Integer clubId,@Param("startDate") Date startDate, @Param("endDate") Date endDate);
	
	/**
	 * 处方提醒列表
	 * @param customerCare
	 * @return
	 */
	List<SdbsCustomerCare> listMedicalRemind(SdbsCustomerCare customerCare);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年10月15日
	 * 功能:修改助理
	 * @param careId
	 * @param appointmentId
	 */
	void updateAssistant(@Param("careId")Integer careId,@Param("appointmentId")Integer appointmentId);
}
