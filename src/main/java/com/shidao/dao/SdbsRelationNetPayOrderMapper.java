package com.shidao.dao;

import java.util.Map;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsRelationNetPayOrder;

public interface SdbsRelationNetPayOrderMapper extends IBatisMapper<SdbsRelationNetPayOrder> {

	Map<String, Object> selectByOutTradeNo(String outTradeNo);
}
