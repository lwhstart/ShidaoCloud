package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsRefundRequest;

public interface SdbsRefundRequestMapper extends IBatisMapper<SdbsRefundRequest>{
	
    void deleteByPrimaryKey(Integer id);
}