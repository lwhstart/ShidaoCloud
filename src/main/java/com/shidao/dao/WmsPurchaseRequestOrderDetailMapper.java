package com.shidao.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.enums.ProductCategory;
import com.shidao.model.WmsPurchaseRequestOrderDetail;

public interface WmsPurchaseRequestOrderDetailMapper extends IBatisMapper<WmsPurchaseRequestOrderDetail>{
    void deleteByPrimaryKey(Integer id);
    
    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2019年1月18日<br>
     * 功能:插入申请单详情
     * @param orderId
     * @param category
     * @param clubId
     * @param warehosueId
     * @param dateStart
     * @param dateEnd
     */
    void insertPurchaseOrderRequestDetail(@Param("orderId")Integer orderId,@Param("category")ProductCategory category,@Param("warehouseId")Integer warehouseId,
    		@Param("clubId")Integer clubId,@Param("dateStart")Date dateStart,@Param("dateEnd") Date dateEnd);
    
    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2019年1月21日<br>
     * 功能:根据采购申请单的actualVendorId插入采购单
     * @param warehouseId
     * @param employeeId
     * @param requestOrderId
     */
    void insertPurchaseOrder(@Param("warehouseId")Integer warehouseId,@Param("employeeId")Integer employeeId,@Param("requestOrderId")Integer requestOrderId);

    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2019年1月21日<br>
     * 功能:根据采购申请单id插入采购单详情
     * @param requestOrderId
     */
    void insertPurchaseOrderDetail(Integer requestOrderId);
    
    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2019年1月21日<br>
     * 功能:根据id获取对应的所有供应商的信息
     * @param requestOrderDetailId
     * @return
     */
    List<WmsPurchaseRequestOrderDetail> getMedicineVendorInfoById(Integer requestOrderDetailId);
    
    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2019年1月21日<br>
     * 功能:更换药供应商
     * @param id
     * @param vendorId
     */
    void updateMedicineVendorId(@Param("id")Integer id,@Param("vendorId")Integer vendorId);
    
    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2019年1月21日<br>
     * 功能:新增新的药
     * @param condition
     */
    void insertNewMenicine(WmsPurchaseRequestOrderDetail condition);
    
    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2019年1月30日<br>
     * 功能:更新total
     * @param id
     */
    void updateTotal(Integer id);
    
    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2019年2月14日<br>
     * 功能:获取供应商的总价和差价
     * @param uuid
     * @return
     */
    List<Map<String, Object>> getVendorTotalAndDifference(String uuid);
}