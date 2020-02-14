package com.shidao.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.WmsRelationClubWarehouseVendor;
import com.shidao.model.WmsWarehouse;

public interface WmsWarehouseMapper extends IBatisMapper<WmsWarehouse>{
	
	/**
	 * 删除仓库对应的供应商
	 * @param vendorId
	 * @param warehouseId
	 */
	void deleteWarehouseVendor(@Param("vendorId")Integer vendorId,
			@Param("warehouseId")Integer warehouseId,
			@Param("relationId")Integer relationId);
	
	/**
	 * 插入仓库对应的门供应商
	 * @param relationClubWarehouseVendor
	 */
	void insertWarehouseVendor(WmsRelationClubWarehouseVendor relationClubWarehouseVendor);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月24日
	 * 功能:仓库对应的门店
	 * @param warehouseId
	 * @return
	 */
	List<Map<String, String>> listClubsByWarehouseId(Integer warehouseId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月24日
	 * 功能:仓库对应的供应商
	 * @param warehouseId
	 * @return
	 */
	List<Map<String, String>> listVendorsByWarehouseId(Integer warehouseId);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年3月29日<br>
	 * 功能:查询仓库关联的和不关联的供应商
	 * @param warehouseId
	 * @return
	 */
	List<Map<String, String>> listRelatedOrNoRelatedVendorsByWarehouseId(Integer warehouseId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年7月19日
	 * 功能:仓库供应商对应的药品种类数量和供应商仓库里的价格统计信息
	 * @param warehouseId
	 * @return
	 */
	List<Map<String, Object>> getVendorStatistics(Integer warehouseId);
	
	List<WmsWarehouse> selectWarehouseAndVendor();
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年3月1日<br>
	 * 功能:club门店的warehouseId
	 * @param clubId
	 * @return
	 */
	Integer getOwnedClubWarehouseId(Integer clubId);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年3月28日<br>
	 * 功能:根据clubId获取连锁药房
	 * @param clubId
	 * @return
	 */
	List<WmsWarehouse> listWarehouseByClubId(Integer clubId);
}