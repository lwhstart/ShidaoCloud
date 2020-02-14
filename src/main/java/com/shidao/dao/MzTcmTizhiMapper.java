package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.MzTcmTizhi;

public interface MzTcmTizhiMapper extends IBatisMapper<MzTcmTizhi> {
	public MzTcmTizhi selectByName(String name);
}