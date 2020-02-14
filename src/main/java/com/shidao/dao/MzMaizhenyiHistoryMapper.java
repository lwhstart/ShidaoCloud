package com.shidao.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.MzDisease;
import com.shidao.model.MzMaizhenyiHistory;
import com.shidao.vo.MaizhenyiReportVO;

public interface MzMaizhenyiHistoryMapper extends IBatisMapper<MzMaizhenyiHistory> {
	public MaizhenyiReportVO getMaizhenyiResultReport(@Param(value = "uuid") String uuid,
			@Param(value = "category") String category);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年3月18日<br>
	 * 功能:为浮中沉
	 * @param uuid
	 * @param category
	 * @return
	 */
	MaizhenyiReportVO getMaizhenyiResultReport4FZC(@Param(value = "uuid") String uuid,
			@Param(value = "category") String category);

	public List<MzDisease> calculateDiseasesForMaizhenyiHistory(@Param(value = "id") Integer id,
			@Param(value = "category") String category);

	public MzMaizhenyiHistory completeMzMaizhenyiHistory(Integer id);

	public MzMaizhenyiHistory selectByUuid(String uuid);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年3月18日<br>
	 * 功能:
	 * @param uuid
	 * @return
	 */
	public MzMaizhenyiHistory selectSimpleByUuid(String uuid);

	public List<Map<String, Object>> getFubingFactors(Integer historyId);
	
	public String getMedicalRelatedMZYUUID(Integer medicalRecordId);
	
	public Map<String, String> get5In1Solution(String yunmai);
	
	/**
	 * @author 创建人:liupengyuan<br>
	 * 时间:2019年3月15日<br>
	 * 功能:查FZC信息
	 * @param uuid
	 * @return
	 */
	MzMaizhenyiHistory selectByUuid4FZC(String uuid);
}