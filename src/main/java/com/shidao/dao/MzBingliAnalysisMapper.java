package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.MzBingliAnalysis;
import com.shidao.model.MzBingliAnalysis.BingliDiseaseType;


public interface MzBingliAnalysisMapper  extends IBatisMapper<MzBingliAnalysis>{
	/**
	 * 获取病理的分析数据
	 * @param historyId
	 * @param type
	 * @return
	 */
	List<MzBingliAnalysis> generateAnalysisOfType(@Param("value=historyId") Integer historyId,
			@Param("value=type") BingliDiseaseType type	); 
    
}