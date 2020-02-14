package com.shidao.dao;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SducRole;

public interface SducRoleMapper extends IBatisMapper<SducRole>{

	String findRoleTypeByloginName(@Param(value="loginName")String loginName);
}