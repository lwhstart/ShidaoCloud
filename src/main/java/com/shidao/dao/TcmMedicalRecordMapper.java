package com.shidao.dao;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.enums.MRPrescriptionCategory;
import com.shidao.enums.PDFPrescriptionCategory;
import com.shidao.model.SdbsSalesOrderItem;
import com.shidao.model.TcmMedicalRecord;

public interface TcmMedicalRecordMapper extends  IBatisMapper<TcmMedicalRecord>{
	
//	public TcmMedicalRecord generateExpertPrescription(@Param(value="yunmai") String yunmai, 
//			   @Param(value="diseaseIds") String diseaseIds, @Param(value="medicalRecordId") Integer medicalRecordId,@Param("category") PDFPrescriptionCategory category,
//			   @Param(value="method") String method);
	
	public List<SdbsSalesOrderItem> getPrescription(@Param(value="salesOrderId") Integer salesOrderId);
	
	Map<String, Object> sendRecipe(@Param(value="medicalRecordId")Integer medicalRecordId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年10月24日
	 * 功能:除了药，发送处方
	 * @param medicalRecordId
	 */
	void sendRecipe4Product(@Param(value="medicalRecordId")Integer medicalRecordId);
	
	List<TcmMedicalRecord> getCareQuestionBymedicalRecordId(@Param("customerId")Integer customerId);
	
	/**
	 * 查看客户莅临的第一次和最后一次日期，如果currentUuid不是null，则表示这个
	 *	@param customerId
	 *	@param currentMedicalRecordUuid, 对比的当前病历。如果设置，最大日期则是这个病历之前的最大日期
	 *	@return value是LocalDate类型， 但是实际上，是java.sql.Date类型
	 * @author yzl , Created at 2019年9月25日
	 *
	 */
	Map<String, LocalDate> getFirstLastDate(@Param(value="customerId")Integer customerId, 
			@Param(value="currentMedicalRecordUuid")String currentMedicalRecordUuid,
			@Param(value="clubId")Integer clubId);
	
	TcmMedicalRecord selectPrescriptionById(Integer id);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年9月6日
	 * @param uuid
	 * @return
	 */
	TcmMedicalRecord selectPrescription4PrintByUuid(String uuid);
	
	TcmMedicalRecord getStatus(@Param(value="id") Integer id,@Param(value="prescriptionId") Integer prescriptionId,
	@Param(value="itemId") Integer itemId);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年1月4日<br>
	 * 功能:是否能修改处方，null能修改处方，有值不能修改处方
	 * @param id
	 * @param prescriptionId
	 * @param itemId
	 * @return
	 */
	Integer getIsChangePrescription(@Param(value="id") Integer id,@Param(value="prescriptionId") Integer prescriptionId,
			@Param(value="itemId") Integer itemId);
	
	void copyMedicalRecord(@Param(value="oldUuid") String oldUuid, @Param(value="newUuid") String newUuid);
	
	void updateByPrimaryKeyDirectly(TcmMedicalRecord medicalRecord);
	
	TcmMedicalRecord selectSimpleByAppointmentId(Integer appointmentId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年7月4日
	 * @param id
	 * @return
	 */
	TcmMedicalRecord selectSimpleById(Integer id);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年9月6日
	 * @param uuid
	 * @return
	 */
	TcmMedicalRecord selectSimpleByUuid(String uuid);
	
	List<TcmMedicalRecord> getCustomerCareList(@Param("carerId") Integer carerId,@Param("customerName") String customerName,@Param("startDate") Date startDate,@Param("endDate") Date endDate);
	
	TcmMedicalRecord getCustomerCareMedicalRecord(@Param("medicalRecordId") Integer medicalRecordId);
	
	/**
	 * 删除预约对应的病历，
	 * 1. 用于取消预约
	 * 
	 * @param appointmentId 预约编号
	 */
	void deleteByAppointmentId(Integer appointmentId);
	
	/*public TcmMedicalRecord generateExpertPrescriptionJingfang(@Param(value="yunmai") String yunmai, @Param(value="diseaseIds") String diseaseIds, @Param(value="medicalRecordId") Integer medicalRecordId,
																@Param(value="method") String method);*/
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月2日
	 * 功能:获取看诊的状态
	 * @param salesOrderIds
	 * @return
	 */
	List<TcmMedicalRecord> listStatus(@Param("listSalesOrderId") List<Integer> listSalesOrderId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月23日
	 * @param uuid
	 * @return
	 */
	TcmMedicalRecord selectByUuid(String uuid);
	
	/**
	 * 使用自动处方开药
	 * @author 袁志林 2018-3-26
	 * @param medicalRecordId 病历编号
	 * @param diseaseIds 疾病列表， 使用“,”分割疾病id，可空
	 * @param yunmai 运脉，可空
	 * @param pdfCategory 预定义处方类型
	 * @param mrCategory 病历处方类型
	 * @return
	 */
	public TcmMedicalRecord generatePrescriptionWithAI(@Param(value="mrId") Integer mrId,
			@Param(value="diseaseIds") String diseaseIds, 
			@Param(value="yunmai") String yunmai, 
			@Param(value="pdfCategory") PDFPrescriptionCategory pdfCategory,
			@Param(value="mrCategory") MRPrescriptionCategory mrCategory
			);
	
	public TcmMedicalRecord generatePrescriptionWithAIFull(@Param(value="mrUuid") String mrUuid,
			@Param(value="diagnosisItemUuids") String diagnosisItemUuids, 
			@Param(value="yunmai") String yunmai, 
			@Param(value="pdfCategory") PDFPrescriptionCategory pdfCategory,
			@Param(value="mrCategory") MRPrescriptionCategory mrCategory
			);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月20日
	 * 功能:得到最近的病例的uuid
	 * @param customerId
	 * @return
	 */
	String getLastUuidOfCustomer(@Param("id")Integer id, @Param("uuid")String uuid);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年7月4日
	 * 功能:得到最大的服药天数
	 * @param medicalRecordId
	 * @return 	
	 */
	Integer getMaxDosageDays(Integer medicalRecordId);
	
	/**
	 * 获取某个病历，在其病历列表中的位置信息
	 * @author yzl 2018年9月17日
	 * @param uuid uuid对应的分页信息
	 * @param excludedUuid 不计算在内的病历，用在对比病例中
	 * @return	nextUuid, 下一条uuid
				previousUuid, 上一条uuid
				total, 总共多少条
				index 当前uuid对应的条数
	 */
	Map<String, Object> getPagerInfo(@Param("uuid") String uuid, @Param("excludedUuid") String excludedUuid );
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年9月13日
	 * 功能:药方打印列表
	 * @param medicalRecord
	 * @return
	 */
	List<TcmMedicalRecord> listPrescription(TcmMedicalRecord medicalRecord);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年10月25日
	 * 功能:
	 * @param locked
	 * @param salesOrderIds
	 */
	void updateLockedBySalesOrderIds(@Param("locked") Integer locked, @Param("listSalesOrderId") List<Integer> listSalesOrderId);
	
	/**
	 * 删除病历对应的结算单。其中结算单是没有结算，以及没有锁定的。
	 * @author yzl 2018年12月5日
	 * @param id 病历编号
	 */
	void removeRelatedSalesOrders(Integer id);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月18日<br>
	 * @param appointmentId
	 */
	void updateByAppointmentId(TcmMedicalRecord medicalRecord);

	/**
	 * 复制旧处方给新处方
	 * @param oldUuid
	 * @param newUuid
	 * @param prescriptionId
	 * @param prescribes
	 */
	void copySelectMedicalRecord(@Param("oldUuid") String oldUuid, @Param("newUuid") String newUuid,@Param("prescriptionId") String prescriptionId,@Param("prescribes") String prescribes);
	
	/**
	 * 开始看诊
	 *	@param medicalRecordId
	 * @author yzl , Created at 2019年9月25日
	 *
	 */
    void insert4DiagnoseBeginning(Integer medicalRecordId);
    /**
     *  看诊结束
     *	@param medicalRecordId
     * @author yzl , Created at 2019年9月25日
     *
     */
    void update4DiagnoseFinished(Integer medicalRecordId);
    
    /**
     * 把病历和诊断报告关联起来，如果没有报告在，则插入报告。
     *	@param paraMap
     * @author yzl , Created at 2019年10月22日
     *
     */
    void insertDiagnosisReport4MedicalRecord(Map<String, Object> paraMap);
    
    Map<String, Object> selectLastDiagnosisReportInfor(String uuid);
}