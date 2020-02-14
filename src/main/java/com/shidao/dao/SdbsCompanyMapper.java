package com.shidao.dao;


import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsCompany;

public interface SdbsCompanyMapper extends IBatisMapper<SdbsCompany> {

	public SdbsCompany getCompanyByCustomerId(@Param(value = "customerId") Integer customerId);
}