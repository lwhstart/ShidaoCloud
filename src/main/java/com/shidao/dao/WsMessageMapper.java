package com.shidao.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.enums.MessageType;
import com.shidao.model.WsMessage;
import com.shidao.vo.ContactorMessageVO;

public interface WsMessageMapper extends IBatisMapper<WsMessage> {

	List<Map<String, Object>> getUnreadMessageCount(@Param(value="receiverId") Integer receiverId,@Param(value="clubId")Integer clubId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月25日
	 * 功能:获取顾客的未读消息
	 * @param receiverId
	 * @return
	 */
	List<Map<String, Object>> getCustomerUnreadMessageCount(Integer receiverId);
	
	void updateRead(@Param(value="sender") String sender,@Param(value="receiver") String receiver);
	
	/**
	 * 查看客户信息
	 * @param customerId
	 * @return
	 */
	List<WsMessage> listCustomerInfo(@Param("customerId")Integer customerId);
	
	/**
	 * 点击客户未查看的客户系统信息，调用接口wsMessage.readCustomerNotice(Integer customerId, Integer noticeId), 
		插入一条记录到wsMessage
	 * @param customerId
	 * @param noticeId
	 */
	void readCustomerNotice(@Param("customerId") Integer customerId, @Param("noticeId") Integer noticeId);
	
	/**
	 * 获取某客户的未读消息列表
	 * @param customerCId 客户的及时通讯编号，C+客户编号
	 * @return
	 */
	Integer getCustomerUnreadCount(String customerCId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月19日
	 * 功能:联系人的未读消息数量和最后一条信息
	 * @param userId
	 * @return
	 */
	List<ContactorMessageVO> getContactorWithLastMessage(String userId);
	
	/**
	 * @author 创建人:liupengyuan,时间:2018年4月19日
	 * 功能:申请信息
	 * @param userId
	 * @return
	 */
	List<ContactorMessageVO> getRequestMessage(ContactorMessageVO condition);

	List<WsMessage> weixinSmallProgramLoad(@Param("maxId")Integer maxId, @Param("sender") String sender, @Param("receiver") String receiver, @Param("pageSize") Integer pageSize);
	
	/**
	 * 或得某用户，指定类型消息的未读数量
	 * @author yzl 2018年9月29日
	 * @param websocketId 用户聊天编号
	 * @param type 聊天类型
	 * @return
	 */
	Integer getUnreadMessageCountOfType(@Param("websocketId")String websocketId, @Param("type")MessageType type);
}
