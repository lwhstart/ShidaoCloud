package com.shidao.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IVOMapper;
import com.shidao.enums.ProductCategory;
import com.shidao.vo.StatsWarehouDeliverySummary;

public interface StatisticsWarehouseMapper extends IVOMapper {
	List<StatsWarehouDeliverySummary> getMedicineSummaryByVendor(@Param("dateFrom") Date dateFrom, 
			@Param("dateTo") Date dateTo,
			@Param("warehouseId") Integer warehouseId,
			@Param("productCategory") ProductCategory productCategory);
	
	List<StatsWarehouDeliverySummary> getMedicineSummaryByWarehouse(@Param("dateFrom") Date dateFrom, 
			@Param("dateTo") Date dateTo,
			@Param("warehouseId") Integer warehouseId,
			@Param("productCategory") ProductCategory productCategory);
}
