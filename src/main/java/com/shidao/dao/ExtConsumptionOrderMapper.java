package com.shidao.dao;


import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.shidao.base.inter.IBatisMapper;
import com.shidao.enums.ExtApprovalStatus;
import com.shidao.enums.ExtBusinessCategory;
import com.shidao.model.ExtConsumptionOrder;

public interface ExtConsumptionOrderMapper extends IBatisMapper<ExtConsumptionOrder> {
    int deleteByPrimaryKey(Integer id);
    
    ExtConsumptionOrder selectByPrimaryKey(Integer id);

	void deleteByUuid(String uuid);
	
	List<ExtConsumptionOrder> listConsumptionOrderSummaryByDate(@Param("dDate")LocalDate dDate ,@Param("dateTo")LocalDate dateTo,@Param("category")ExtBusinessCategory category,@Param("cycleIndex")int cycleIndex,@Param("clubId")Integer clubId);

	ExtApprovalStatus getOrderStatus(@Param("orderUuid") String orderUuid,@Param("itemUuid") String itemUuid,@Param("finalizationUuid") String finalizationUuid);

	/**
	 * 
	 * @param uuid
	 * @param employeeId
	 * @param parameterId :声明可传递orderId 
	 * @author lwh data:2020年1月9日
	 */
	void approveConsumptionOrder(@Param("uuid")String uuid,@Param("employeeId")Integer employeeId,@Param("parameterId")Integer parameterId);
	
	void cancelApproval(@Param("uuid")String uuid,@Param("employeeId")Integer employeeId,@Param("parameterId")Integer parameterId);
	 
	List<ExtConsumptionOrder> listWithFinalization(ExtConsumptionOrder order);
	
	/**
	 * 执行数据插入
	 */
	void generateDailySummary(@Param("dDate")LocalDate dDate,@Param("clubId")Integer clubId,@Param("categoryEnum")ExtBusinessCategory categoryEnum);
	
	/**
	 * 取消提交
	 */
	void cancelSubmitConsumptionOrder(@Param("dDate")LocalDate dDate,@Param("clubId")Integer clubId,@Param("categoryEnum")ExtBusinessCategory categoryEnum);
	/**
	 * 批量审核
	 * @param dDate
	 * @param clubId       命名因游标执行时与数据库表字段名称相同导致无法查出
	 * @param categoryEnum
	 * @author lwh data:2020年1月9日
	 */
	void approveConsumptionOrderByDate(@Param("dDate") LocalDate dDate,@Param("clubId")Integer clubId,@Param("categoryEnum")ExtBusinessCategory categoryEnum,@Param("employeeId")Integer employeeId);
	
	/**
	 * 批量取消审核
	 * @param dDate
	 * @param clubId
	 * @param categoryEnum
	 * @param employeeId
	 * @author lwh data:2020年1月16日
	 */
	void cancelApproveConsumptionOrderByDate(@Param("dDate") LocalDate dDate,@Param("clubId")Integer clubId,@Param("categoryEnum")ExtBusinessCategory categoryEnum,@Param("employeeId")Integer employeeId);
	
	
	void updateTotal(@Param("itemUuid")String itemUuid,@Param("id")Integer id);
	
	/**
	 * 提交订单时修改状态
	 * @param date
	 * @param clubId
	 * @param category
	 * @author lwh data:2020年1月14日
	 */
	void submit(@Param("dDate")LocalDate dDate,@Param("clubId")Integer clubId,@Param("categoryEnum")ExtBusinessCategory categoryEnum);
	
	/**
	 * 判断总账是否相等
	 * @param date
	 * @param clubId
	 * @param category
	 * @author lwh data:2020年1月16日
	 */
	void accountsContrast(@Param("dDate")LocalDate dDate,@Param("clubId")Integer clubId,@Param("categoryEnum")ExtBusinessCategory categoryEnum);
	
}