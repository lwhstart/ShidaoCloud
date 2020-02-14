package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdepContactorRequest;

public interface SdepContactorRequestMapper extends IBatisMapper<SdepContactorRequest>{
	
    void deleteByPrimaryKey(Integer id);
    
    /**
     * @author 创建人:liupengyuan,时间:2018年4月23日
     * 功能:得到申请信息
     * @param condition
     * @return
     */
    List<SdepContactorRequest> getRequestMessage(SdepContactorRequest condition);
    
    /**
     * @author 创建人:liupengyuan,时间:2018年4月24日
     * 功能:顾客的未读消息数量
     * @param userId
     * @return
     */
    Integer getUnReadCount(String userId);
    
    /**
     * 查看userId发给contactorId的申请状态
     * 
     * @author yzl 2018年11月20日
     * @param userId
     * @param contactorId
     * @return
     */
    SdepContactorRequest selectLastStatus(@Param("userId") String userId, @Param("contactorId")String contactorId);
}