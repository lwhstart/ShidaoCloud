package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsAppointmentD2dOrder;

public interface SdbsAppointmentD2dOrderMapper extends IBatisMapper<SdbsAppointmentD2dOrder> {
   
	public List<SdbsAppointmentD2dOrder> getCompanyD2DList();
	
	public SdbsAppointmentD2dOrder selectD2DInfo(@Param(value="id") Integer id);
}