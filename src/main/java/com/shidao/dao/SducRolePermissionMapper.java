package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SducRolePermission;

public interface SducRolePermissionMapper extends IBatisMapper<SducRolePermission>{
	
    List<String> getRolePermissionByloginName(@Param(value="loginName")String loginName);
}
