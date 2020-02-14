package com.shidao.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.WmsFinancialStatement;

public interface WmsFinancialStatementMapper extends IBatisMapper<WmsFinancialStatement>{
	
	void insertFinanceInventory(@Param("startDate")Date startDate,@Param("endDate")Date endDate,@Param("category") String category);
	
	WmsFinancialStatement getTotal(WmsFinancialStatement financialStatement);
}