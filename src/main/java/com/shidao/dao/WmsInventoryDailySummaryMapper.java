package com.shidao.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.enums.ProductCategory;
import com.shidao.model.WmsInventoryDailySummary;
import com.shidao.vo.JinxiaocunSummaryVo;
import com.shidao.vo.WarehouseUnionDeliveryVo;

public interface WmsInventoryDailySummaryMapper extends IBatisMapper<WmsInventoryDailySummary> {

	/**
	 * 新增库存每日统计，使用了避免重复插入的设置：where  not exists
	 */
	void insertDailySummary();
	
	List<JinxiaocunSummaryVo> getJinxiaocunSummary(@Param("dateStart") Date dateStart,@Param("dateEnd") Date dateEnd,@Param("warehouseId") Integer warehouseId,@Param("category")ProductCategory category);
	
	List<Map<String, Object>> getJinxiaocunOverview(@Param("dateStart") Date dateStart,@Param("dateEnd") Date dateEnd,@Param("warehouseId") Integer warehouseId,@Param("vendorId") Integer vendorId,@Param("category")ProductCategory category);
	
	List<WarehouseUnionDeliveryVo> getJinxiaocunDetail(@Param("dateStart") Date dateStart,@Param("dateEnd") Date dateEnd,@Param("warehouseId") Integer warehouseId,@Param("productId") Integer productId);
}
