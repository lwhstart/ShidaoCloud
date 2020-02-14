package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.WmsBorrowMedicineOrderDetails;

public interface WmsBorrowMedicineOrderDetailsMapper extends IBatisMapper<WmsBorrowMedicineOrderDetails>{
	
	public void deleteByPrimaryKey(Integer id);
}