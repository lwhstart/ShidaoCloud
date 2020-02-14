package com.shidao.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.enums.PayMethod;
import com.shidao.model.SdbsCustomerMembership;
import com.shidao.model.SdbsDelivery;
import com.shidao.model.SdbsSalesOrder;
import com.shidao.vo.Confirmation4Salesorders;
import com.shidao.vo.SalesOrderVO;
import com.shidao.vo.SdbsDeliveryVO;
import com.shidao.vo.SettlementVO;

public interface SdbsSalesOrderMapper extends IBatisMapper<SdbsSalesOrder> {

	public Float selectByquantity(@Param(value = "customerId") Integer customerId,
			@Param(value = "productId") Integer productId);

	public void confirmMedicine(@Param(value = "salesOrderId") Integer salesOrderId,
			@Param(value = "cancelMedicine") Integer cancelMedicine);

	public Integer selectNoPay(@Param(value = "customerId") Integer customerId);

	public void deleteByAppointmentId(@Param(value = "appointmentId") Integer appointmentId);

	void deleteByPrimaryKey(Integer id);

	void settlement(SettlementVO vo);

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月13日 功能:多个订单一起结算
	 * @param vo
	 * @return
	 */
	@Deprecated
	List<Integer> settlementBySalesOrderIds(SettlementVO vo);

	void doSettlement(@Param("salesOrderIds") String salesOrderIds, @Param("payMethod") PayMethod payMethod,
			@Param("payAmount") BigDecimal payAmount,  @Param("membershiUuid") String membershiUuid);

	/**
	 * @author 创建人:liupengyuan,时间:2018年10月18日 功能:免单和赠送
	 * @param salesOrderIds
	 * @param payMethod
	 */
	@Deprecated
	void settlements4Free(@Param("salesOrderIds") String salesOrderIds, @Param("payMethod") String payMethod);

	/**
	 * 根据结算订单出库表，每个药方一个出库表
	 * 
	 * @param salesOrderId
	 * @param employeeId
	 * @param warehouseId
	 */
	void insertDeliveryByPrescription(@Param("salesOrderId") Integer salesOrderId,
			@Param("employeeId") Integer employeeId, @Param("warehouseId") Integer warehouseId);

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月14日 功能:insertDeliveryBySalesOrderIds
	 * @param salesOrderIds
	 * @param employeeId
	 */
	int insertDeliveryBySalesOrderIds(@Param("salesOrderIds") String salesOrderIds,
			@Param("employeeId") Integer employeeId);

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月30日 功能:查询是否有降糖颗粒，并插入出库表
	 * @param salesOrderIds
	 * @param employeeId
	 * @param warehouseId
	 */
	void insertDeliveryOfXDF(@Param("salesOrderIds") String salesOrderIds, @Param("employeeId") Integer employeeId,
			@Param("warehouseId") Integer warehouseId);

	/**
	 * 退费
	 * 
	 * @param salesOrderId
	 * @param employeeId
	 */
	void refund(@Param("salesOrderId") Integer salesOrderId, @Param("employeeId") Integer employeeId);

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月30日 功能:降糖颗粒删除出库单
	 * @param salesOrderId
	 */
	void refundOfJiangtang(Integer salesOrderId);

	/**
	 * 获取配药状态
	 * 
	 * @param salesOrderId
	 * @return
	 */
	String getDispenseStatus(Integer salesOrderId);

	List<SdbsSalesOrder> getYunClinicPayment(SdbsSalesOrder sdbsSalesOrder);

	SdbsSalesOrder selectByAppointmentId(Integer appointmentId);

	void insertDelivery(SdbsDelivery delivery);

	void updateDelivery(SdbsDelivery delivery);

	List<SdbsDeliveryVO> listDelivery(SdbsDeliveryVO vo);

	SdbsDeliveryVO selectDeliveryById(Integer id);

	SdbsDeliveryVO selectDeliveryBySalesOrderId(Integer salesOrderId);

	void deleteDeliveryBySalesOrderId(Integer salesOrderId);

	void deleteDeliveryById(Integer id);

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月2日 功能:根据顾客ID，clubID，日期获取多个该用户的结算单详情
	 * @param categories
	 * @return
	 */
	List<SdbsSalesOrder> listByCustomerId(SdbsSalesOrder salesOrder);

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月5日 功能:查询多个结算单里是结算单的种类信息
	 * @param salesOrder
	 * @return
	 */
	List<SdbsSalesOrder> selectByIdsAndCategory(SdbsSalesOrder salesOrder);

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月7日 功能:顾客今日的所有结算单列表集合
	 * @param condition
	 * @return
	 */
	List<SalesOrderVO> listSalesOrder(SalesOrderVO condition);

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月8日 功能:查询药费和诊费
	 * @param condition
	 * @return
	 */
	List<SdbsSalesOrder> listMedicineAndDoctorFee(SdbsSalesOrder condition);

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月9日 功能:获取多个订单的ids以“,”隔开
	 * @param condition
	 * @param clubId
	 * @param customerId
	 * @param appointmentId  == 0 is APPOINTMENT_ID IS NOT NULL
	 * @param createdDate
	 * @param salesOrderIds
	 * @param deliveryNeeded
	 * @param categories
	 * @param type           = 'starting' 存在结算中，不存在未结算
	 * @return
	 */
	String getSalesOrderIds(SdbsSalesOrder condition);

	/**
	 * @author 创建人:liupengyuan,时间:2018年10月24日 功能:获取多个订单的id的list
	 * @param condition
	 * @return
	 */
	List<Integer> listSalesOrderId(SdbsSalesOrder condition);

	/**
	 * @author 创建人:liupengyuan,时间:2018年3月16日 功能:为快递而写的输出格式
	 * @param clubId
	 * @param customerId
	 * @param createdDate
	 * @param salesOrderIds
	 * @param deliveryNeeded
	 * @param categories
	 * @return
	 */
	List<SdbsSalesOrder> listSalesOrderAboutDelivery(SdbsSalesOrder condition);

	/**
	 * @author 创建人:liupengyuan,时间:2018年5月14日 功能:简单的只查询salesOrder表
	 * @param condition
	 * @return
	 */
	List<SdbsSalesOrder> listSimpleSalesOrder(SdbsSalesOrder condition);

	/**
	 * @author 创建人:liupengyuan,时间:2018年7月5日 功能:简单的只查询salesOrder表
	 * @param id
	 * @return
	 */
	SdbsSalesOrder selectSimpleById(Integer id);

	SdbsSalesOrder getPayAmountBySalesOrderId(Integer salesOrderId);

	/**
	 * @author 创建人:liupengyuan,时间:2018年8月16日 功能:获取，药名，药量，药价和总价
	 * @param medicalRecordId
	 * @return
	 */
	List<SdbsSalesOrder> getPrescriptionPrice(String uuid);

	/**
	 * @author 创建人:liupengyuan,时间:2018年9月28日 功能:
	 * @param id
	 * @return
	 */
	SdbsCustomerMembership getPayMembership(Integer id);

	Map<String, Object> getSimpleStaticsOfDate(SdbsSalesOrder condition);

	/**
	 * @author 创建人:liupengyuan,时间:2018年10月12日 功能:根据对应的药品，查找对应的仓库，再根据仓库查找对应的clubId
	 * @param salesOrderIds
	 * @param positions
	 * @return
	 */
	List<Integer> getManagerClubIdByWarehous(@Param("salesOrderIds") String salesOrderIds);

	/**
	 * @author 创建人:liupengyuan,时间:2018年11月19日 功能:得到所用的积分总数
	 * @param salesOrder
	 * @return
	 */
	BigDecimal getJfAmount(SdbsSalesOrder salesOrder);

	/**
	 * @author 创建人:liupengyuan,时间:2018年11月20日 功能:根据订单详情里的积分总数更新到订单表里
	 * @param salesOrderId
	 */
	void updateJfAmount4Item(Integer salesOrderId);

	/**
	 * @author 创建人:liupengyuan<br>
	 *         时间:2018年12月17日<br>
	 *         功能:更新order的discount和payAmount
	 * @param salesOrderId
	 */
	void updateDiscountAndPayAmount(@Param("salesOrderId") Integer salesOrderId,
			@Param("salesOrderIds") List<Integer> salesOrderIds);

	/**
	 * @author 创建人:liupengyuan,时间:2018年12月17日 功能:得到某种订单类型的总价
	 * @param salesOrderIds
	 * @param category
	 * @return
	 */
	BigDecimal getTotalBySalesOrderIdsAndCategory(@Param("salesOrderIds") String salesOrderIds,
			@Param("category") String category, @Param("listSalesOrderIds") List<Integer> listSalesOrderIds);

	/**
	 * 获取制定结算单列表对应的药品和诊费的确认信息
	 * 
	 * @param condition 包含一系列结算单的id
	 * @return
	 */
	List<Confirmation4Salesorders> listMedicine4Confirmation(SdbsSalesOrder condition);

	/**
	 * 获取制定结算单列表对应的商品确认信息
	 * 
	 * @param condition 包含一系列结算单的id
	 * @return
	 */
	List<Confirmation4Salesorders> listProduct4Confirmation(SdbsSalesOrder condition);

	/**
	 * 获取卡项相关的消费
	 * 
	 * @param condition 包含一系列结算单的id
	 * @return
	 */
	List<Confirmation4Salesorders> listCardRelated4Confirmation(SdbsSalesOrder condition);

	Map<String, Object> getSalesOrdersSummary(SdbsSalesOrder condition);
	
	/**
	 *  #正式支付延期付款的单子
	 * @param id
	 * @param payMethod
	 * @return, Created at 2019年8月24日
	 * @auto yzl
	 *
	 */
	int pay4Postpone(@Param("id") Integer id, @Param("payMethod") PayMethod payMethod);
	
	/**
	 * #按照支付方式列举结算单
	 * @param condition
	 * @return
	 * @author yzl , Created at 2019年8月27日
	 *
	 */
	List<Map<String, Object>> listByPayCategory(SdbsSalesOrder condition);

}