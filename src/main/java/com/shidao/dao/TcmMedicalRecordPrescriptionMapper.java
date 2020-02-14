package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.TcmMedicalRecord;
import com.shidao.model.TcmMedicalRecordPrescription;

public interface TcmMedicalRecordPrescriptionMapper extends IBatisMapper<TcmMedicalRecordPrescription>{
	
	void deleteByPrimaryKey(Integer id);
	void deleteGaofang(Integer medicalRecordId);
	
	/**
	 * 获取药品的总天数，可以根据category进行选取，若category为null则取全部的药品的天数
	 * @param appointmentId
	 * @param category
	 * @return
	 */
	Double getMedicineDaysByAppointmentId(@Param("appointmentId") Integer appointmentId,@Param("category")String category);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年8月1日
	 * 功能:获得处方的价值 如果处方剂量未填的话，按1算。
	 * @param id
	 * @return
	 */
	Float getAmount(Integer id);
	
	int insertDiagnosisItem(@Param("id") Integer id, @Param("items")List<String> items);
	
	/**
	 * 获得处方所属的病历信息
	 *	@param uuid
	 *	@return
	 * @author yzl , Created at 2019年11月2日
	 *
	 */
	TcmMedicalRecord selectMedicalRecord(String uuid);

	/**
	 * 复制处方到新的病历， 
	 * 如果目标已经有膏方了，不能复制膏方
	 * 非药品类处方，因为一个病例只有一个，所以只能把源项目拼接到新病例的对应处方中。
	 *	@param srcPUuid 源处方uuid
	 *	@param destMRUuid 目标病历uuid
	 *	@return 更新的条数
	 * @author yzl , Created at 2019年11月21日
	 *
	 */
	int copyPrescription2Medical(@Param("srcPUuid")String srcPUuid, @Param("destMRUuid")String destMRUuid);
}