package com.shidao.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.ExtConsumptionOrderItem;

public interface ExtConsumptionOrderItemMapper extends IBatisMapper<ExtConsumptionOrderItem> {
    int deleteByPrimaryKey(Integer id);

	void deleteByOrderId(Integer orderId);

	void deleteByUuid(String uuid);

	void insertBatchItems(@Param("items") List<ExtConsumptionOrderItem> items);
	
	Map<String, Object> selectItem4Shougong(String itemUuid);
	
	Integer deleteBySelective(ExtConsumptionOrderItem  record);
}