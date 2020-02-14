package com.shidao.dao;


import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsServiceCard;

public interface SdbsServiceCardMapper extends IBatisMapper<SdbsServiceCard>{
	
	
	void deleteByPrimaryKey(@Param("id")Integer id);
	
	/**
	 * 更新套餐卡的价格
	 * @author yzl 2018年8月21日
	 * @param id
	 */
	void updateTCKPrice(@Param("tckId")Integer tckId, @Param("itemId")Integer itemId);
	
	/**
	 * 更新服务卡对应的商品信息
	 * @author yzl 2018年8月23日
	 * @param id
	 */
	void updateRelatedProduct(Integer id);
	
	/**
	 * 查看名字是否有冲突
	 * @author yzl 2018年10月30日
	 * @param type
	 * @param name
	 * @param clubId
	 * @return
	 */
	Integer hasDuplicateName(SdbsServiceCard serviceCard);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年11月7日
	 * 功能:
	 * @param productId
	 * @return
	 */
	SdbsServiceCard selectByProductId(Integer productId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年11月7日
	 * 功能:根据会员卡id和clubId获取促销卡信息
	 * @param id
	 * @param clubId
	 * @return
	 */
	SdbsServiceCard getCXKByTCMId(@Param("id")Integer id,@Param("clubId")Integer clubId);
	
}