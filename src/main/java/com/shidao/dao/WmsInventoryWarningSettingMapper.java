package com.shidao.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.enums.ProductCategory;
import com.shidao.model.TcmMedicine;
import com.shidao.model.WmsInventoryWarningSetting;

public interface WmsInventoryWarningSettingMapper extends IBatisMapper<WmsInventoryWarningSetting>{
    void deleteByPrimaryKey(Integer id);
    
    
    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2019年1月18日<br>
     * 功能:查询需要采购的药品，处方使用量>库存量
     * @param category
     * @param warehouseId
     * @param dateStart
     * @param dateEnd
     * @return
     */
    List<Map<String, Object>> listPurchaseMedicine(@Param("category")ProductCategory category,@Param("warehouseId")Integer warehouseId,
    		@Param("dateStart")Date dateStart,@Param("dateEnd")Date dateEnd);
    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2018年12月21日<br>
     * 功能:库存警报设置列表
     * @param condition
     * @return
     */
    List<TcmMedicine> listInventoryWarning(TcmMedicine condition);
    
    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2018年12月21日<br>
     * @param warehouseId
     * @param category
     */
    void deleteByWarehouseIdAndCategory(@Param("warehouseId")Integer warehouseId,@Param("category")ProductCategory category);
    
    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2018年12月21日<br>
     * 功能:从inventory里找到对应的medicineId加入，报警值为wms_setting里的默认值
     * @param warehouseId
     * @param category
     */
    void insertDefaultWarning(@Param("warehouseId")Integer warehouseId,@Param("category")ProductCategory category);
    
    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2018年12月25日<br>
     * 功能:插入采购单详情
     * @param purchaseOrderId
     * @param warehouseId
     * @param category
     */
    void insertPurchaseDetail(@Param("purchaseOrderId")Integer purchaseOrderId,@Param("warehouseId")Integer warehouseId,@Param("category")ProductCategory category);
}