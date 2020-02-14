package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.enums.MRPrescriptionCategory;
import com.shidao.enums.ProductCategory;
import com.shidao.model.SdbsProduct;
import com.shidao.model.WmsInventory;

public interface WmsInventoryMapper extends IBatisMapper<WmsInventory>{
    
	/**
	 * 可用库存量 = 实际库存 - 未审核的库存
	 * @param medicineId
	 * @return
	 */
	Integer getOnHandInventory(@Param(value="medicineId") Integer medicineId,@Param(value="warehouseId") Integer warehouseId,@Param(value="productId")Integer productId,@Param("category")MRPrescriptionCategory category);
	
	void deleteByPrimaryKey(WmsInventory inventory);
	/**
	 * 查询之前的不过期的产品ID和库存剩余量
	 * @param inventory
	 * @return
	 */
	List<WmsInventory> getRemainderAmount(WmsInventory inventory);
	
	void updateByBatchNumberAndProductId(WmsInventory inventory);
	
	/**
	 * 配药更改供应商所需要查询的数据
	 * @param code
	 * @param warehouseId
	 * @return
	 */
	List<SdbsProduct> listVendorByMedicineOfWarehouse(@Param("medicineId") Integer medicineId,@Param("warehouseId") Integer warehouseId,@Param("category") ProductCategory category);

	/**
	 * 入库单插入库存表
	 * @param orderId
	 */
	void insertByWarehouseVoucher(Integer orderId);
	
	/**
	 * 查仓库里的批号
	 * @param category
	 * @return
	 */
	List<WmsInventory> listProductInventory(WmsInventory condition);
	
	/**
	 * 获取商品及对应的库存
	 * @param condition
	 * @return
	 */
	List<WmsInventory> listProductByInventory(WmsInventory condition);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年8月15日
	 * 功能:获取库存列表信息
	 * @param condition
	 * @return
	 */
	List<WmsInventory> listSimpleByCondition(WmsInventory condition);
}