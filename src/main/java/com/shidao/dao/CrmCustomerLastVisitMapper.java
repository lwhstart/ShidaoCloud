package com.shidao.dao;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.CrmCustomerLastVisit;

public interface CrmCustomerLastVisitMapper extends IBatisMapper<CrmCustomerLastVisit>{
    void deleteByPrimaryKey(Integer id);
    
    /**
     * @author 创建人:liupengyuan,时间:2018年6月28日
     * @param medicalRecordId
     */
    void insertByMedicalRecordId(Integer medicalRecordId);
}