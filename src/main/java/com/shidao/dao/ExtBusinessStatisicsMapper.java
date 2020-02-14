package com.shidao.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import com.shidao.base.inter.IBatisMapper;
import com.shidao.enums.ExtBusinessCategory;
import com.shidao.model.ExtBusinessStatisics;


public interface ExtBusinessStatisicsMapper extends IBatisMapper<ExtBusinessStatisics> {

	/*
	 * 查询医保信息，条件参数 日期，门诊编号，卡号类型
	 */
	List<ExtBusinessStatisics> listFullDinggao(@Param("startDate")LocalDate startDate,@Param("endDate")LocalDate endDate,@Param("clubId")Integer clubId,@Param("category")ExtBusinessCategory category);

	/**
	 * 汇总信息
	 * @param startDate
	 * @param endDate
	 * @param clubId
	 * @param category
	 * @returns
	 * @author lwh data:2020年1月18日
	 */
	HashMap<String, Object> getSummarizing(@Param("startDate")LocalDate startDate,@Param("endDate")LocalDate endDate,@Param("clubId")Integer clubId,@Param("category")ExtBusinessCategory category);
}
