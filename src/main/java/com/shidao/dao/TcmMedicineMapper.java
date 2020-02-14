package com.shidao.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.enums.ClubType;
import com.shidao.enums.ProductCategory;
import com.shidao.model.SdbsMedicinePrice;
import com.shidao.model.TcmMedicine;

public interface TcmMedicineMapper extends IBatisMapper<TcmMedicine> {
    	
	List<Map<String, Object>> quickSearch (@Param(value="nameInitial")String nameInitial, 
			@Param(value="category")ProductCategory category, @Param(value="warehouseId")Integer warehouseId,
			@Param(value="quickSearchStrategy")String quickSearchStrategy);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年2月18日<br>
	 * 功能:
	 * @param nameInitial
	 * @param category
	 * @param warehouseId
	 * @param quickSearchStrategy
	 * @return
	 */
	List<Map<String, Object>> quickSearchMeicineName (@Param(value="nameInitial")String nameInitial,
			@Param(value="category")ProductCategory category,@Param(value="warehouseId")Integer warehouseId);
	
	void deleteByPrimaryKey(Integer id);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月30日
	 * 功能:如果草药的价格修改，则相关的颗粒剂的价格也修改
	 * @param medicineId
	 */
	void updateProductPriceAbountMedicine(Integer medicineId);
	
	/**
	 * 获取名字对应的中药名编号
	 * @author yzl 2018年7月11日
	 * @param name 中药名
	 * @return
	 */
	TcmMedicine getMedicineOfName(String name); 
	
	
	List<TcmMedicine> getMedicineNames(@Param("nameInitial") String nameInitial,@Param("warehouseId")Integer warehouseId,@Param("clubType")ClubType clubType,@Param("category")String category);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年7月20日
	 * @param uuid
	 * @return
	 */
	TcmMedicine selectByUuid(String uuid);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年8月6日
	 * @return
	 */
	List<SdbsMedicinePrice> getPerGherbalPrice(SdbsMedicinePrice medicinePrice);
	
    /**
     * @author 创建人:liupengyuan,时间:2018年8月6日
     * 功能:
     * @param id
     * @param price
     */
    void updatePerGherbalPrice(@Param("id")Integer id,@Param("price") BigDecimal price,@Param("lastModifiedId")Integer lastModifiedId);
    
    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2019年2月14日<br>
     * 功能:插入medicinechangehistory
     * @param priceId
     * @param newPrice
     * @param modifierId
     */
    void insertPerGherbalPriceHistory(@Param("priceId")Integer priceId,@Param("newPrice")BigDecimal newPrice,@Param("modifierId")Integer modifierId);
}