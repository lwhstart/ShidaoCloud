package com.shidao.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.shidao.base.inter.IBatisMapper;
import com.shidao.model.WmsInventorySnapshot;
import com.shidao.model.WmsInventorySnapshot.SnapshotCategory;

public interface WmsInventorySnapshotMapper extends IBatisMapper<WmsInventorySnapshot>{
	Integer deleteByPrimaryKey(Integer id);

    Integer insert(WmsInventorySnapshot record);

    Integer insertSelective(WmsInventorySnapshot record);

    WmsInventorySnapshot selectByPrimaryKey(Integer id);

    Integer updateByPrimaryKeySelective(WmsInventorySnapshot record);

    Integer updateByPrimaryKey(WmsInventorySnapshot record);
    
    Float getStartEndTotal(@Param("vendorName") String vendorName,@Param("fieldCondition") String fieldCondition,
    		@Param("startCode") String startCode,@Param("endCode") String endCode,@Param("date") Date date);
    
    void insertInventorySnapshot(@Param(value="approver") Integer approver, @Param(value="category") SnapshotCategory category);
}