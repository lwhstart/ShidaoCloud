package com.shidao.dao;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsDelivery;
import com.shidao.vo.SdbsDeliveryVO;

public interface SdbsDeliveryMapper extends IBatisMapper<SdbsDelivery>{
    void deleteByPrimaryKey(Integer id);
    
    /**
     * @author 创建人:liupengyuan,时间:2018年3月14日
     * @param id
     * @return
     */
    SdbsDeliveryVO selectDeliveryById(Integer id);
    
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月14日
	 * @param salesOrderId
	 * @return
	 */
	SdbsDeliveryVO selectDeliveryBySalesOrderId(Integer salesOrderId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月14日
	 * @param salesOrderId
	 */
	void deleteDeliveryBySalesOrderId(Integer salesOrderId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月14日
	 * 功能:得到关系表里快递表id
	 * @param salesOrderId
	 * @param salesOrderIds
	 * @return
	 */
	Integer getDeliveryIdRelation(@Param("salesOrderId")Integer salesOrderId,@Param("salesOrderIds")String salesOrderIds);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月14日
	 * 功能:插入关系表
	 * @param deliveryId
	 * @param salesOrderId
	 */
	void insertRelation(@Param("deliveryId")Integer deliveryId,@Param("salesOrderId")Integer salesOrderId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月14日
	 * 功能:删除关系表
	 * @param salesOrderId
	 */
	void deleteRelationBySalesOrderId(Integer salesOrderId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月19日
	 * 功能:删除关系表
	 * @param deliveryId
	 */
	void deleteRelationByDeliveryId(Integer deliveryId);
}