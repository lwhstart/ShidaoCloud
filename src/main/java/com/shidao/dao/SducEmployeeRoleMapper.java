package com.shidao.dao;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SducEmployeeRole;

public interface SducEmployeeRoleMapper extends IBatisMapper<SducEmployeeRole>{
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年5月15日
	 * 功能:根据员工的id和角色名称，删除对应的数据
	 * @param userId
	 * @param roleName
	 */
	void deleteByUserIdAndRoleName(@Param("userId")Integer userId,@Param("roleName")String roleName);
 }