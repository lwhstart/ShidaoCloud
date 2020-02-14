package com.shidao.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.WmsBorrowMedicineOrder;

public interface WmsBorrowMedicineOrderMapper extends IBatisMapper<WmsBorrowMedicineOrder>{
	public void insertBorrowMedicine(Map<String, Object> map);
	
	public WmsBorrowMedicineOrder borrowMedicineOrderByUuid(String uuid);

	public void deleteByUuid(String uuid);
	
	public void updateBorrowMedicine(@Param("uuid") String uuid, @Param("lendWarehouseId") Integer lendWarehouseId,@Param("lendVendorId") Integer lendVendorId);
	
	public void approveMedicineBorrowOrder(Map<String, Object> map);
}