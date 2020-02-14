package com.shidao.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsPeriod;

public interface SdbsPeriodMapper extends IBatisMapper<SdbsPeriod>{
   
	List<String> findPeriodByserviceTypeAndDate(@Param(value="serviceType") String serviceType,@Param(value="date") String date);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年7月26日
	 * @param clubId
	 * @return
	 */
	SdbsPeriod getClubWorkingTime(Integer clubId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年7月26日
	 * @param id
	 * @return
	 */
	Integer deleteByPrimaryKey(Integer id);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年7月26日
	 * @param clubId
	 * @return
	 */
	Integer deleteByClubId(Integer clubId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年7月27日
	 * 功能:批处理插入
	 * @param list
	 */
	void batchInsert(List<SdbsPeriod> list);
	
	List<SdbsPeriod> getAppointmentNumByDate(@Param("date") Date date,@Param("clubId") Integer clubId,@Param("doctorId") Integer doctorId);
}