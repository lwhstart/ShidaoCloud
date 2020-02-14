package com.shidao.dao;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.WmsSetting;

public interface WmsSettingMapper extends IBatisMapper<WmsSetting>{
	void deleteByPrimaryKey(@Param(value="id")Integer id);
}