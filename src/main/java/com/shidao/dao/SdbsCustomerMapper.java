package com.shidao.dao;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsCustomer;
import com.shidao.model.SdbsCustomer.ServiceGroup;
import com.shidao.model.SdcomEmployee;
import com.shidao.vo.ConsumptionMemberVO;
import com.shidao.vo.CustomerActivityVO;
import com.shidao.vo.CustomerMemberStatisticsVO;
import com.shidao.vo.XMKCustomerVO;

public interface SdbsCustomerMapper extends IBatisMapper<SdbsCustomer> {

	Integer getByMobileAndPassword(@Param(value = "mobile") String mobile, @Param(value = "password") String password);

	void updatePasswordByMobile(@Param(value = "password") String password, @Param(value = "mobile") String mobile);

	SdbsCustomer selectDetail(@Param("uuid") String uuid, @Param("id") Integer id, @Param("clubId") Integer clubId);

	List<CustomerMemberStatisticsVO> getCustomerMemberStatistics(@Param(value = "dateFrom") Date dateFrom,
			@Param(value = "dateTo") Date dateTo, @Param(value = "clubId") Integer clubId,
			@Param(value = "summary") Boolean summary);

	ServiceGroup getServiceGroup(Integer id);

	/**
	 * @author 创建人:liupengyuan,时间:2018年9月26日 功能:
	 * @param customerId
	 * @param position
	 * @return
	 */
	List<SdcomEmployee> getCustomerServiceGroup(@Param("customerId") Integer customerId,
			@Param("position") String position);

	CustomerMemberStatisticsVO getCustomerNonMemberStatistics(@Param(value = "dateFrom") Date dateFrom,
			@Param(value = "dateTo") Date dateTo, @Param(value = "clubId") Integer clubId);

	List<Map<String, Object>> getNewCustomer(@Param(value = "dateFrom") Date dateFrom,
			@Param(value = "dateTo") Date dateTo, @Param(value = "clubId") Integer clubId);

	/**
	 * 
	 * @param clubId
	 * @param dateFrom
	 * @param dateTo
	 * @param byDays   是分天计算，还是统计所有
	 * @return
	 */
	List<ConsumptionMemberVO> listConsumption(@Param("clubId") Integer clubId, @Param("dateFrom") Date dateFrom,
			@Param("dateTo") Date dateTo, @Param("byDays") Boolean byDays);

	/**
	 * 客流详单
	 * 
	 * @param dateFrom
	 * @param dateTo
	 * @param customerName
	 * @return
	 */
	List<CustomerActivityVO> listCustomerActivity(CustomerActivityVO activityVO);

	/**
	 * 根据顾客ID查询是否拥有项目卡
	 * 
	 * @param id
	 * @return
	 */
	XMKCustomerVO listXMKById(@Param("id") Integer id, @Param("clubId") Integer clubId);

	/**
	 * 获得某客户的家属
	 * 
	 * @param mainId
	 * @return
	 */
	List<SdbsCustomer> listCustomerRelationByMainId(@Param("mainId") Integer mainId);

	/**
	 * 合并用户
	 * 
	 * @param customerId
	 * @param discardCustomerId
	 * @param operator
	 */
//	void mergeCustomer(@Param("customerId") Integer customerId, @Param("discardCustomerId") Integer discardCustomerId,
//			@Param("operator") Integer operator);
	void mergeCustomer(@Param("newUuid") String newUuid, @Param("oldUuid") String oldUuid,
			@Param("operator") Integer operator);

	List<SdbsCustomer> listCustomerOfServiceCard(@Param("serviceCardId") Integer serviceCardId,
			@Param("clubId") Integer clubId);

	void deleteByPrimaryKey(Integer id);

	/**
	 * @author 创建人:liupengyuan,时间:2018年4月18日 功能:关于员工的我的客户信息
	 * @param employeeId
	 * @param name
	 * @return
	 */
	List<SdbsCustomer> listMyCustomerByEmployeeId(SdbsCustomer condition);

	/**
	 * 获取几日后要过生日的客户列表，以及对应的店长信息
	 * 
	 * @param daysBeforeBirthday 几日后
	 * @return
	 */
	List<Map<String, String>> getCustomersOfBirthday4Manager(Integer daysBeforeBirthday);

	/**
	 * 获取当天生日的客户
	 * 
	 * @return
	 */
	List<Map<String, String>> getCustomerOfBirthday();

	/**
	 * 查看最后访问时间，在一定时间范围内的顾客
	 * 
	 * @param clubId    门店编号
	 * @param dateStart 开始日期
	 * @param dateEnD   结束日期
	 * @param doctorId  责任医生编号
	 * @return
	 */
	List<HashMap<String, Object>> listLastVisitInfo(@Param("clubId") Integer clubId,
			@Param("dateStart") LocalDate dateStart, @Param("dateEnd") LocalDate dateEnd,
			@Param("doctorId") Integer doctorId);

	/**
	 * 查看客户在这一天，是不是初诊。
	 *	@param customerUuid
	 *	@param date
	 *	@return
	 * @author yzl , Created at 2019年10月24日
	 *
	 */
	boolean isFirstVisit(@Param("uuid")String uuid,@Param("date")LocalDate date);
}