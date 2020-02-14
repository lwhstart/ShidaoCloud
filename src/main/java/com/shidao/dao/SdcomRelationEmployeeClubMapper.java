package com.shidao.dao;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdcomRelationEmployeeClub;

public interface SdcomRelationEmployeeClubMapper extends IBatisMapper<SdcomRelationEmployeeClub>{
    void deleteByPrimaryKey(Integer id);
    
    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2019年3月1日<br>
     * 功能:获取药房主管/药剂师的warehouseId
     * @param employeeId
     * @return
     */
    Integer getEmployeeWarehouseId(Integer employeeId);
    
    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2019年3月1日<br>
     * 功能:删除employee所对应的所有关系表
     * @param employeeId
     */
    void deleteEmployee(@Param("employeeId")Integer employeeId,@Param("clubId")Integer clubId);
    
    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2019年3月4日<br>
     * 功能:更改为药房主管/药剂师职位，warehouseId也跟着添加
     * @param employeeId
     * @param clubId
     * @param isWarehouseIdNull true:设置warehouseId为null，否则设置为wms_warehouse里的clubId对用的warehouseId
     */
    void updateWarehouseId(@Param("employeeId")Integer employeeId,@Param("clubId")Integer clubId,@Param("isWarehouseIdNull")Boolean isWarehouseIdNull);
}