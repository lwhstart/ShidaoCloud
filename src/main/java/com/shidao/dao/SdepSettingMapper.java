package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.enums.ClubType;
import com.shidao.enums.SdepSettingCategory;
import com.shidao.model.SdepSetting;
import com.shidao.util.ValidList;

public interface SdepSettingMapper extends IBatisMapper<SdepSetting> {
	void deleteByPrimaryKey(Integer id);

	/**
	 * 获取门店的设置
	 * 
	 * @author yzl 2018年7月24日
	 * @param clubId
	 * @return
	 */
	List<SdepSetting> getClubSetting(@Param("clubId") Integer clubId, @Param("clubType") ClubType clubType);

	/**
	 * 以category，type总和为基本查询依据
	 * @author yzl 2018年7月31日
	 * @param category 分类 
	 * @param type  子类型
	 * @param clubId 门店编号
	 * @return
	 */
	List<SdepSetting> listBasedOnCategoryType(@Param("category") SdepSettingCategory category,
			@Param("type") String type, @Param("clubId") Integer clubId);
	
	List<String> listValidListByCategory(ValidList.ValidListCategory category);
}