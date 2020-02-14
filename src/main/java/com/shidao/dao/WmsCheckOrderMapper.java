package com.shidao.dao;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.WmsCheckOrder;

public interface WmsCheckOrderMapper extends IBatisMapper<WmsCheckOrder>{
	void deleteByPrimaryKey(@Param(value="id")Integer id);
	
	void autoInsertByChecked(@Param("employeeId")Integer employeeId,@Param("warehouseId")Integer warehouseId);
	
	/**
	 * 盘点审核通过，并返回出库ID和入库ID
	 * 
	 */
	void approve(@Param("id")Integer id, @Param("approverId")Integer approverId);
}