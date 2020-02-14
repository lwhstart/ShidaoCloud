package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.WmsPurchaseRequestOrder;

public interface WmsPurchaseRequestOrderMapper extends IBatisMapper<WmsPurchaseRequestOrder>{
	
	void deleteByPrimaryKey(Integer id);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月25日<br>
	 * 功能:删除订单和订单详情
	 * @param id
	 */
	void deleteOrderAndDetail(Integer id);
	
	WmsPurchaseRequestOrder selectByUuid(String uuid);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月30日<br>
	 * 功能:根据订单id更新总价
	 * @param id
	 */
	void updateTotalById(Integer id);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月30日<br>
	 * 功能:根据详情id更新总价
	 * @param id
	 */
	void updateTotalByDetailId(Integer detailId);
}