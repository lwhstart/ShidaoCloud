package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.enums.ProductCategory;
import com.shidao.model.SdcomClub;

public interface SdcomClubMapper extends IBatisMapper<SdcomClub> {
	SdcomClub selectByUuid(@Param(value = "uuid") String uuid);

	Integer updateWarehouse(@Param(value = "clubId") Integer clubId, @Param(value = "warehouseId") Integer warehouseId);
	
	Integer getRelatedWarehouseId(Integer clubId);
	
	/**
	 * 获取和本门诊共用一个仓库的门诊
	 * @author yzl 2018年7月16日
	 * @param clubId
	 * @return
	 */
	List<SdcomClub> getRelatedClubs(Integer clubId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月29日
	 * 功能:医生和门店的对应关系
	 * @param club
	 * @return
	 */
	List<SdcomClub> listClubRelationDoctor();
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年7月19日
	 * 功能:与仓库相关联的所有门店
	 * @param warehouseId
	 * @return
	 */
	List<SdcomClub> listRelationClubs(Integer warehouseId);
	
	/**
	 * 获得医生关联的门店
	 * @author yzl 2018年7月19日
	 * @return
	 */
	List<SdcomClub> getDoctorRelatedClubs(Integer doctorId);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年3月1日<br>
	 * 功能:获得员工关联的门店
	 * @param doctorId
	 * @return
	 */
	List<SdcomClub> getRelationEmployeeClubs(Integer doctorId);
	
	Integer getWarehouseIdByCategoryAndClubId(@Param("clubId")Integer clubId,@Param("category")ProductCategory category);
}