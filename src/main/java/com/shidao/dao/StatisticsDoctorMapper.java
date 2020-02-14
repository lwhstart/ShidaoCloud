package com.shidao.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IVOMapper;

public interface StatisticsDoctorMapper extends IVOMapper {
	
	List<Map<String, Object>> getProfit(@Param("clubId") Integer clubId, 
			@Param("doctorId")Integer doctorId, 
			@Param("dateFrom")Date dateFrom,
			@Param("dateTo")Date dateTo);
	
	List<Map<String, Object>> getSimpleProfitSummary(@Param("dateFrom") Date dateFrom,
			@Param("dateTo") Date dateTo,
			@Param("clubId") Integer clubId);

}
