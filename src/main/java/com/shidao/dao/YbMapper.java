package com.shidao.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.shidao.model.DataAnalysis;
import com.shidao.model.InsertBatchModel;
import com.shidao.model.YbCustomer;
import com.shidao.vo.FuZhenCondition;
import com.shidao.vo.YbMedicalRecordVO;

/**
 * @author 创建人:liupengyuan,时间:2018年12月12日
 * 功能:我创建的
 */
@Repository
public interface YbMapper{
    
    Map<String, Object> getSalesOrderDate();
    
    Map<String, Object> getSalesOrderDetailDate();
    
    Map<String, Object> getHistoryDateAndCount();
    
    Map<String, Object> getZhiliaoDateAndCount();
    
    Map<String, Object> getDiagnosisDateAndCount();
    
    Map<String, Object> getCustomerDate();
    
    List<Map<String, Object>> getCustomerFeeTypeCount();
    
    List<YbCustomer> getYbCustomerList();
    
    void insertGeneralBatch(InsertBatchModel batchModel);
    
    void updateSalesOrderCustomerId(Integer clubId);
    
    void updateZhiliaoId(Integer clubId);
    
    void updateZhiliaoCustomerId();
    
    void updateDiagnosisCustomerId();
    
    void updateHistoryCustomerId();
    
    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2018年12月14日<br>
     * 功能:diseases字段，需要把中文的，、空格，三个符号替换成英文的,(逗号)
     */
    void updateDiagnosisDiseasesChangeComma();
    
    void insertYbDisease(List<String> list);
    
    /**
     * 获得按性别筛选的十大疾病
     * @author yzl 2018年12月18日
     * @param dataAnalysis
     * @return
     */
    List<Map<String, Object>> listTop10DiseaseOfGender(DataAnalysis dataAnalysis);
    
    List<Map<String, Object>> listTop10DiseaseOfAge(DataAnalysis dataAnalysis);
    
    /**
     * @author 创建人:liupengyuan<br>
     * 时间:2018年12月27日<br>
     * 功能:医保处方详情
     * @param condition
     * @return
     */
    YbMedicalRecordVO getYbMedicalRecordList(FuZhenCondition condition);
}