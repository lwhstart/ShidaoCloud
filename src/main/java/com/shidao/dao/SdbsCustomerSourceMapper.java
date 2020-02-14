package com.shidao.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdbsCustomerSource;

public interface SdbsCustomerSourceMapper extends IBatisMapper<SdbsCustomerSource>{
	
    void deleteByPrimaryKey(Integer id);
    
    /**
     * @author 创建人:liupengyuan,时间:2018年4月10日
     * @param customerId
     * @return
     */
    SdbsCustomerSource selectByCustomerId(@Param("customerId")Integer customerId,@Param("clubId") Integer clubId);
    
    /**
     * @author 创建人:liupengyuan,时间:2018年4月10日
     * @param customerId
     * @return
     */
    SdbsCustomerSource selectByCustomerUuid(@Param("customerUuid") String customerUuid,@Param("clubId") Integer clubId);
    
    /**
     * @author 创建人:liupengyuan,时间:2018年4月11日
     * 功能:是否是初诊 1初诊，0复诊
     * @param customerUuid
     * @param date
     * @return
     */
    Integer getIsFirstVisit(@Param("customerUuid")String customerUuid,@Param("date")Date date);
    
    /**
     * 获取该门诊支持的客户来源
     * @param clubId 门诊编号
     * @return 如果本门诊没有设置，则使用默认设置（customerId=null，firstVisitClubId=0),
     */
    List<String> listSources(Integer clubId);
}