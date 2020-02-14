package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.enums.DiagnosisCategory;
import com.shidao.model.TcmDiagnosisReportItem;

public interface TcmDiagnosisReportItemMapper extends IBatisMapper<TcmDiagnosisReportItem> {
    int deleteByUuid(String uuid);
    
    List<TcmDiagnosisReportItem> listWithDetailsOfPrescriptionUuid(String uuid);
    
    List<TcmDiagnosisReportItem> listSimpleOfPrescriptionUuid(String uuid);
    
    int getPrescriptionUseCount(String uuid);
    
    String selectProjectIdsOfUUids(@Param("uuids")List<String> uuids, @Param("category")DiagnosisCategory category);
    
    /**
     * 插入处方和诊断条目的关系
     *	@param prescriptionUuid
     *	@param itemUuid
     * @author yzl , Created at 2019年11月27日
     *
     */
    int insertRelationWithPrescription(@Param("prescriptionUuid")String prescriptionUuid, @Param("itemUuid")String itemUuid);
    
    /**
	 * 移除处方和诊断的关系
	 *	@param prescriptionUuid
	 *	@param itemUuid
	 *	@return
	 * @author yzl , Created at 2019年11月5日
	 *
	 */
	int deleteRelationWithPrescription(@Param("prescriptionUuid")String prescriptionUuid, @Param("itemUuid")String itemUuid);

	/**
	 * 获取诊断条目，包含详情的设置信息。
	 *	@param uuid
	 *	@return
	 * @author yzl , Created at 2019年12月7日
	 *
	 */
	TcmDiagnosisReportItem selectWithSettingByUuid(String uuid);
	
	TcmDiagnosisReportItem selectDetailsByUuid(String uuid);
}