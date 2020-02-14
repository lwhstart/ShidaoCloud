package com.shidao.dao;



import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.CrmCustomerNotice;

public interface CrmCustomerNoticeMapper extends IBatisMapper<CrmCustomerNotice> {

	void deleteByPrimaryKey(@Param("id") Integer id);

	List<CrmCustomerNotice> getCustomerUnreadNotice(String customerCid);
}