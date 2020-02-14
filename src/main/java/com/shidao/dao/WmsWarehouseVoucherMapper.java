package com.shidao.dao;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.WmsWarehouseVoucher;

public interface WmsWarehouseVoucherMapper extends IBatisMapper<WmsWarehouseVoucher>{
	
	void deleteByPrimaryKey(Integer id);
	
	void approve( @Param("id")Integer id, @Param("approverId")Integer approverId);
}