package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.WmsRelationClubWarehouse;

public interface WmsRelationClubWarehouseMapper extends IBatisMapper<WmsRelationClubWarehouse>{
	void deleteByPrimaryKey(Integer id);
}