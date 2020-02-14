package com.shidao.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdepPayCategory;

/** 
* @author 作者 zzp: 
* @version 创建时间：2019年8月6日 下午1:41:13 
* 类说明 
*/
public interface SdepPayCategoryMapper extends IBatisMapper<SdepPayCategory> {
	
	/**
	 *  返回所有支持的支付种类。
	 * @return
	 * @author yzl , Created at 2019年8月27日
	 *
	 */
	List<String> listAllDistinct();
	
	/**
	 * @param clubId
	 * @author yjj 2019年10月9日
	 */
	List<String> selectPayCategoryByClubId(Integer clubId);
	
	/**
	 * 查询支付类型下面是否有支付方法   
	 * @return
	 * @author yjj 2019年9月25日
	 */
	boolean isContainsPayMethods(String  uuid);
	
	/**
	 * 删除支付类型
	 * @param uuid
	 * @author yjj 2019年9月25日
	 */
	void deleteByUuid(String uuid);
	
	/**
	 * 判断支付类型是否已存在
	 * @param name
	 * @param clubId
	 * @return
	 * @author yjj 2019年9月27日
	 */
	boolean isNameUsed(@Param("name")String name, @Param("clubId")int clubId);

}
