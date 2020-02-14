package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.WmsCheckOrderDetail;

public interface WmsCheckOrderDetailMapper extends IBatisMapper<WmsCheckOrderDetail>{
	void deleteByPrimaryKey(WmsCheckOrderDetail checkOrderDetail);
}