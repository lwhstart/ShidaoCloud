package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsServiceCardItem;

public interface SdbsServiceCardItemMapper extends IBatisMapper<SdbsServiceCardItem>{
	
	void deleteByPrimaryKey(@Param("id")Integer id);
	
	void deleteByServiceCardId(@Param("serviceCardId")Integer serviceCardId);
	
	void updateEnabledByServiceCardId(@Param("serviceCardId")Integer serviceCardId,@Param("enabled")Integer enabled);
	
	void insertBatch(List<SdbsServiceCardItem> list);
}