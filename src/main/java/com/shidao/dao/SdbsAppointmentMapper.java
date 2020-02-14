package com.shidao.dao;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsAppointment;
import com.shidao.model.SdbsPeriod;
import com.shidao.vo.SdbsAppointmentVO;

public interface SdbsAppointmentMapper extends  IBatisMapper<SdbsAppointment>{
	
	public SdbsAppointmentVO getCustomerLastConsumptionByAppointmentId(@Param(value="id") Integer id);
	
	public SdbsAppointmentVO getCustomerInfoByAppointmentId(@Param(value="id") Integer id);
	
	/**
	 * 获取会员莅临人数,次数，消费金额
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	Map<String, Object> getAppointmentStatistics(@Param("date") Date date,@Param("membershipType") String membershipType,@Param("clubId") Integer clubId);

	/**
	 * @author 创建人:liupengyuan,时间:2018年4月13日
	 * 功能:获取首诊日期
	 * @param customerUuid
	 * @return
	 */
	Date getFirstVisitDateByCustomerUuid(@Param("customerUuid")String customerUuid);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月13日
	 * 功能:获取首诊日期
	 * @param customerId
	 * @return
	 */
	LocalDate getFirstVisitDateByCustomerId(@Param("customerId")Integer customerId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月11日
	 * 功能:远程会诊预约列表
	 * @param condition
	 * @return
	 */
	List<SdbsAppointment> listRemoteDiagnostic(SdbsAppointment condition);
	
	/**
	 * 检查某个医生， 在指定的日期和时间段，是否被支付了。
	 * @param doctor
	 * @param date
	 * @param period
	 * @return
	 */
	Integer getAppointmentPayed(@Param("doctorId")Integer doctorId, 
			@Param("date")Date date, 
			@Param("period")String period);
	
	/**
	 * 删除预约
	 * @author yzl 2018-5-16
	 * @param id
	 */
	void deleteById(Integer id);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月7日<br>
	 * 功能:获取该门店该医生本日各个时间段的顾客信息
	 * @param doctorId
	 * @param date
	 * @param clubId
	 * @return
	 */
	List<SdbsPeriod> getDoctorDaily(@Param("doctorId")Integer doctorId, 
			@Param("date")LocalDate date, 
			@Param("clubId")Integer clubId);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2018年12月7日<br>
	 * 功能:该月各个天所预约的人数
	 * @param doctorId
	 * @param date
	 * @param clubId
	 * @return
	 */
	List<Map<String, Object>>  getDoctorWorks(@Param("doctorId")Integer doctorId);
	
	Boolean cancelable(Integer appointmentId);
}                          