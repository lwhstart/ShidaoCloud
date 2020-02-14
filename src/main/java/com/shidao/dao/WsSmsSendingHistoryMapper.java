package com.shidao.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.WsSmsSendingHistory;

public interface WsSmsSendingHistoryMapper extends IBatisMapper<WsSmsSendingHistory> {

	/**
	 * @author 创建人:liupengyuan,时间:2018年8月16日
	 * 功能:所有门店的短信数量统计
	 * @return
	 */
	List<Map<String, Object>> getAllClubSmsMessageStatistics(@Param("date")Date date);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年8月16日
	 * 功能:本门店的短信数量统计
	 * @param createdDate
	 * @param clubId
	 * @return
	 */
	List<Map<String, Object>> getClubSmsMessageStatistics(@Param("createdDate")Date createdDate,@Param("clubId")Integer clubId);

	/**
	 * @author 创建人:liupengyuan,时间:2018年8月16日
	 * 功能:获取某门店发送过短信的年数
	 * @return
	 */
	List<String> getYears(Integer clubId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年8月17日
	 * 功能:获取最晚后一个月和最早前一个月日期
	 * @return
	 */
	Map<String, Date> getMinAndMaxDate();
}
