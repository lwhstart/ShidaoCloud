package com.shidao.dao;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.ExtCustomer;

public interface ExtCustomerMapper extends IBatisMapper<ExtCustomer> {
    int deleteByPrimaryKey(Integer id);

    ExtCustomer selectByCardNumberAndClubId(@Param("cardNumber") String cardNumber,@Param("clubId") Integer clubId);

	int updateByUUid(ExtCustomer extCustomer);

}