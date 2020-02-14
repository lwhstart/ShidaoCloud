package com.shidao.dao;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdcomClubMembership;
import com.shidao.model.SdcomClubMembership.OperationCategory;

public interface SdcomClubMembershipMapper extends IBatisMapper<SdcomClubMembership> {

	public Integer takeClubOperation(@Param("clubId") Integer clubId, @Param("operation") OperationCategory operation);
}