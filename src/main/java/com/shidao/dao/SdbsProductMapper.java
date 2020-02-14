package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsProduct;
import com.shidao.enums.ProductCategory;

public interface SdbsProductMapper extends IBatisMapper<SdbsProduct>{
    
	public List<String> selectProductType(SdbsProduct product);
		
	/**
	 * 打印采购单详情
	 * @param product
	 * @return
	 */
	List<SdbsProduct> getFramework(SdbsProduct product);
	
	SdbsProduct sumProduct(Integer id);
	
	List<SdbsProduct> getAllDateInventory(SdbsProduct product);
	
	/**
	 * 根据产品种类查询
	 * @param category
	 * @return
	 */
	List<SdbsProduct> selectByCategory(@Param("category") String category);
	
	void deleteByPrimaryKey(@Param("id")Integer id);
	
	/**
	 * 根据医生ID查询诊费
	 * @param id
	 * @return
	 */
	SdbsProduct getDoctorFee(@Param("doctorId")Integer doctorId,@Param("clubId")Integer clubId);
	
	/**
	 * 根据药品编号和仓库ID查询供应商
	 * @param code 药品编号
	 * @param warehouseId 仓库ID
	 * @return
	 */
	List<SdbsProduct> listVendorByCodeAndWarehouse(@Param("code")String code,@Param("warehouseId")Integer warehouseId);
	
	List<SdbsProduct> quickSearch(@Param("nameInitial")String nameInitial,  
			@Param("warehouseId")int warehouseId, 
			@Param("category")ProductCategory category);

	/**
	 * 删除诊费
	 * @author yzl 2018年6月22日
	 * @param doctorId 
	 * @param clubId
	 * @param category , doctorFee huozhe remoteDiagnosticDoctorFee
	 */
	int removeDoctorFee(@Param("doctorId") Integer doctorId, 
			@Param("clubId") Integer clubId, 
			@Param("category") ProductCategory category);
	
	
	void batchSaveMedicine(List<SdbsProduct> products);
	
	void batchSaveMedicine1(List<SdbsProduct> products);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年7月19日
	 * @param uuid
	 * @return
	 */
	SdbsProduct selectByUuid(String uuid);
}