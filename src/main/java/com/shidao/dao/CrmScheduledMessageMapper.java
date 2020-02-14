package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.CrmScheduledMessage;

public interface CrmScheduledMessageMapper extends IBatisMapper<CrmScheduledMessage>{
	
    void deleteByPrimaryKey(Integer id);
}