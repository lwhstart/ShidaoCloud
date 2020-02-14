package com.shidao.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsCustomerMembership;
import com.shidao.vo.MembershipCardUsedHistory;
import com.shidao.vo.RechargeRecordVO;
import com.shidao.vo.ServiceCardCustomerVO;

public interface SdbsCustomerMembershipMapper extends IBatisMapper<SdbsCustomerMembership>{
   
	/**
	 * 根据用户id获取用户购买服务信息
	 * @param customerId
	 * @return CustomerSerivce
	 */
	public SdbsCustomerMembership getByCustomerId(@Param(value="customerId")Integer customerId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年3月8日
	 * 修改功能:根据顾客salesOrderId或salesOrderIds查询所拥有的会员卡和疗程卡，并且消费前和消费后的记录
	 * @param salesOrderId
	 * @param salesOrderIds
	 * @return
	 */
	List<ServiceCardCustomerVO> listServiceCardInfo(@Param("salesOrderId") Integer salesOrderId,@Param("salesOrderIds")String salesOrderIds);
	
	/**
	 * 充值会员卡,返回salesOrderId
	 * @param rechargeRecordVO
	 */
	Integer rechargeMembershipCard(RechargeRecordVO rechargeRecordVO);
	
	/**
	 * 获取时间段内的会员办理人数
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	Map<String, Object> getCountNewTcmCustomerByDate(@Param("startDate") Date startDate,@Param("endDate") Date endDate);
	
	List<Map<String, Object>> getGroupByMembership(@Param("clubId") Integer clubId);
	
	Map<String, Integer> getInsertCall(Map<String, Object> map);
	
	Integer isMembership(@Param("customerId") Integer customerId);
	
	void deleteById(Integer id);
	
	/**
	 * 退卡
	 * @author yzl
	 * @param membershipId 卡号
	 * @param operatorId 操作员
	 */
	void refundCard(@Param("membershipId") Integer membershipId,@Param("operatorId") Integer operatorId,@Param("refundCardInfo") String refundCardInfo,@Param("refundTotal") BigDecimal refundTotal);
	
	SdbsCustomerMembership selectByUuid(String uuid);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年9月25日
	 * 功能:list卡和卡项详情
	 * @param membership
	 * @return
	 */
	List<SdbsCustomerMembership> listDetailByCondition(SdbsCustomerMembership membership);
	
	Boolean getIsCustomerMembership(Integer customerId);
	
	List<MembershipCardUsedHistory> listUsedHistory(Integer membershipCardId);
}