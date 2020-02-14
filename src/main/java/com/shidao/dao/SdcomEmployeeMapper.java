package com.shidao.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.enums.Position;
import com.shidao.model.SdcomEmployee;

public interface SdcomEmployeeMapper extends IBatisMapper<SdcomEmployee> {   
     String findPassWordByLoginName(@Param(value="loginName")String loginName);
          
     List<SdcomEmployee> getDefaultContactors(@Param(value="id") Integer id);
     
     List<String> getPermissions(Integer id);

	 void updatePasswordByMobile(@Param(value="password") String password, @Param(value="mobile") String mobile);
	 
	 List<SdcomEmployee> getDoctorByPharmacy(@Param(value="clubId") Integer clubId);
	 
	 Integer updatePasswordOfLoginName(@Param(value="password") String password, @Param(value="loginName") String loginName);
	 
	 List<Map<String, Object>> getCountByClubGroupPosition(@Param(value="clubId") Integer clubId);
	 
	 /**
	  * 可供顾客服务员工列表
	 * @param customerId
	 * @param position
	 * @return
	 */
	List<SdcomEmployee> listServiceGroup(@Param("customerId")Integer customerId,@Param("serviceType")String serviceType,@Param("position")String position,@Param("clubId")Integer clubId);
	
	Integer getAssistantIdOfDoctorClub(@Param("doctorId")Integer doctorId,@Param("clubId")Integer clubId);
	
	void updateAssistantIdOfDoctorClub(@Param("assistantId")Integer assistantId,@Param("doctorId")Integer doctorId,@Param("clubId")Integer clubId);

	SdcomEmployee selectByUuid(@Param("uuid")String uuid, @Param("clubId")Integer clubId);
	
	/**
	 * 根据登录名查找员工
	 * @author yzl 2018年6月21日
	 * @param loginName
	 * @return
	 */
	SdcomEmployee selectByLoginName(String loginName);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月8日
	 * 功能:得到管理员的登录名和密码
	 * @param position
	 * @param clubId
	 * @return
	 */
	SdcomEmployee getAdminLoginInfo(SdcomEmployee employee);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月9日
	 * 功能:不包含id的医生 
	 * @param employee
	 * @return
	 */
	List<SdcomEmployee> listNoIds(SdcomEmployee employee);
	
	/**
	 * 删除员工，如果没有被使用的话。
	 * @author yzl 2018年11月16日
	 * @param id
	 * @return
	 */
	int deleteIfUnused(Integer id);
	
	List<SdcomEmployee> listByPositionOfClub(@Param("clubId")Integer clubId, @Param("positions")Position... positions);
	
	/**
	 * 返回在职人员id和name
	 * @return
	 * @author lwh data:2020年1月7日
	 */
	List<SdcomEmployee> listByInServiceWorkingState();
	
	List<SdcomEmployee> listByInServiceStaff();
}