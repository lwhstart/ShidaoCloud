package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsVendor;

public interface SdbsVendorMapper extends IBatisMapper<SdbsVendor> {
	List<SdbsVendor> listAll();

	Integer getIsEnableVendor(@Param("name") String name, @Param("warehouseId") Integer warehouseId);

	/**
	 * @author 创建人:liupengyuan,时间:2018年7月19日 
	 * 功能:如果供应商里没有药品并且其他仓库也没有关联词供应商，则删除供应商仓库对应关系
	 * @param clubId
	 * @return
	 */
	Integer deleteRelationWarehouseVendor(@Param("vendorId") Integer vendorId, @Param("warehouseId") Integer warehouseId);
}