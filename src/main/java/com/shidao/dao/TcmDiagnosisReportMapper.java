package com.shidao.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.enums.DiagnosisCategory;
import com.shidao.model.TcmDiagnosisReport;
import com.shidao.vo.DiagnosisReportComparisonVO;

public interface TcmDiagnosisReportMapper extends IBatisMapper<TcmDiagnosisReport> {
	/**
	 * 根据编号，删除诊断报告。
	 *	@param id
	 *	@return
	 * @author yzl , Created at 2019年10月25日
	 *
	 */
    int deleteByPrimaryKey(Integer id);
    
    /**
     * 获取病历对应的诊断报告
     *	@param medicalRecordUuid
     *	@return
     * @author yzl , Created at 2019年10月25日
     *
     */
    TcmDiagnosisReport selectByMedicalRecord(@Param("medicalRecordUuid") String medicalRecordUuid,
    		@Param("prescriptionUuid") String prescriptionUuid);
    
    /**
     * 复制诊断报告
     *	@param uuid 当前诊断报告uuid
     *	@param creatorId 操作者编号
     * @author yzl , Created at 2019年10月26日
     *
     */
    void copyDiagnosisReport(@Param("uuid")String uuid, @Param("creatorId")Integer creatorId);
    
    /**
     * 列出本次和前次诊断报告的差别
     *	@param uuid
     *	@return
     * @author yzl , Created at 2019年10月26日
     *
     */
    DiagnosisReportComparisonVO listComparison(String uuid);
    
    Integer getUserAddedDiagnosisFieldIdByName(@Param("name")String name, @Param("category")DiagnosisCategory category) ;
//    int getUserAddedDiagnosisFieldIdByName(String name, DiagnosisCategory category) ;
    
    void insertDiagnosisField(Map<String, Object> field) ;

}