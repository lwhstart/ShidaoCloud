package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.CrmScheduledCareManagement;

public interface CrmScheduledCareManagementMapper extends IBatisMapper<CrmScheduledCareManagement>{
	
    void deleteByPrimaryKey(Integer id);
    
    /**
     * 获取下一个关怀管理
     * @author yzl 2018年7月3日
     * @param currentManagementId
     * @return
     */
    CrmScheduledCareManagement getNextManagement(Integer currentManagementId);
    
    int deleteByLastSalesOrderItemId(int lastSalesOrderItemId);

}