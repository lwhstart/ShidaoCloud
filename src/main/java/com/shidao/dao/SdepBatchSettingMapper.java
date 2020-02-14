package com.shidao.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.SdepBatchSetting;

public interface SdepBatchSettingMapper extends IBatisMapper<SdepBatchSetting> {
	List<SdepBatchSetting> getSetting(@Param(value = "model") String model, @Param(value = "category") String category,
			@Param(value = "source") String source);

	/**
	 * @author 创建人:liupengyuan<br>
	 *         时间:2018年12月5日<br>
	 *         功能:字段中文名
	 * @param model
	 * @param category
	 * @param source
	 * @return
	 */
	List<SdepBatchSetting> getColumnName(@Param(value = "model") String model,
			@Param(value = "category") String category, @Param(value = "source") String source);
}