package com.shidao.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.WmsDeliveryVoucher;
import com.shidao.vo.DeliveryVoucherManauaVO;
import com.shidao.vo.MedicineInformationVo;
import com.shidao.vo.PharmacyDeliveryDetailVO;

public interface WmsDeliveryVoucherMapper extends IBatisMapper<WmsDeliveryVoucher> {
 
	void deleteByPrimaryKey(Integer id);
	
	List<MedicineInformationVo> selectMedicineInformation(Integer id);
	
	List<WmsDeliveryVoucher> getVoucherForPrescritionList(WmsDeliveryVoucher voucher);
	
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月2日
	 * 功能:自定义处方配药列表
	 * @param customerName
	 * @param status
	 * @param clubId
	 * @return
	 */
	List<DeliveryVoucherManauaVO> udPharmacyOrderList(DeliveryVoucherManauaVO condition);
	
	PharmacyDeliveryDetailVO getPrescriptionDetails(Integer id);
	
	PharmacyDeliveryDetailVO getPrescriptionDetailsByUuid(String uuid);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年9月6日
	 * 功能:处方打印详情
	 * @param uuid
	 * @return
	 */
	PharmacyDeliveryDetailVO getPrescriptionPrintDetails(String uuid);
	
	/**
	 * 取消配药
	 * @param deliveryVoucherId
	 */
//	void cancellDispensing(Integer deliveryVoucherId);
	void dispendingVoucherStarting(Integer deliveryVoucherId);
	
	/**
	 * 对出库单执行出库操作
	 * 对应的是，药房点配药
	 * 或者是管理员批准手动出库单
	 * @param id 出库单编号
	 * @param approverId 出库员/审核员编号
	 */
	void dispendingVoucher(@Param("id")Integer id,@Param("approverId")Integer approverId);
	
	/**
	 * 对出库单执行approve操作
	 * 对应的是，药房点配药
	 * 或者是管理员批准手动出库单
	 * @param id 出库单编号
	 * @param approverId 出库员/审核员编号
	 */
	void approve(@Param("id")Integer id,@Param("approverId")Integer approverId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月3日
	 * 功能:自定义处方的信息
	 * @param id
	 * @return
	 */
	Map<String, Object> getXDFInfoById(Integer id);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年10月15日
	 * 功能:根据id获取相对应门店的id
	 * @param id
	 * @return
	 */
	Integer getClubIdById(Integer id);
}