package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdepContactor;

public interface SdepContactorMapper extends IBatisMapper<SdepContactor>{
	
    void deleteByPrimaryKey(Integer id);
    
    /**
     * @author 创建人:liupengyuan,时间:2018年4月18日
     * @param userId
     * @param contactorId
     * @return
     */
    SdepContactor selectByUserIdAndContactorId(@Param("userId")String userId,@Param("contactorId")String contactorId);
    
    /**
     * @author 创建人:liupengyuan,时间:2018年4月18日
     * @param userId
     * @param contactorId
     */
    void deleteByUserIdAndContactorId(@Param("userId")String userId,@Param("contactorId")String contactorId);
    
    /**
     * @author 创建人:liupengyuan,时间:2018年4月18日
     * 功能:值修改category
     * @param contactor
     */
    void updateByUserIdAndContactorId(SdepContactor contactor);
    
    /**
     * @author 创建人:liupengyuan,时间:2018年4月25日
     * 功能:查询顾客的所有联系人
     * @param contactor
     * @return
     */
    List<SdepContactor> listCustomerContactor(SdepContactor contactor);
    
    /**
     * @author 创建人:liupengyuan,时间:2018年7月4日
     * 功能:强行插为联系人
     * @param userId
     * @param contactorId
     */
    void insertContactor(@Param("userId")String userId,@Param("contactorId")String contactorId);
    
    /**
     * @author zzp 2018-11-19
     * 获取自己对应的好友
     * @param userId
     * @return
     */
    List<SdepContactor> selectSendAppByUserId(@Param("userId") String userId,@Param("id") Integer id);
}