package com.haizhi.np.dispatch.mapper;

import java.sql.Timestamp;
import java.util.List;

import com.haizhi.np.dispatch.bean.FollowList;
import com.haizhi.np.dispatch.bean.NpVersionManager;
import org.apache.ibatis.annotations.Param;

public interface NpVersionManagerDao {

	public List<FollowList> select();
	
	public List<NpVersionManager> selectAllNpVersionManager();
	
	public List<NpVersionManager> selectNpVersionManager(@Param("tableType")String tableType);
	
	public List<String> selectFollowItemListByNpVersion(@Param("tableName")String tableName,
			@Param("lastTime")String lastTime);
	
	/**
	 *	所有时间段z
	 
	public List<String> selectFollowItemListByNpVersionAllTime(@Param("tableName")String tableName);
	*/
	public void updateNpVersionManager(List<NpVersionManager> list);
	
	public Timestamp getMaxUpdateTime(@Param("tableName")String tableName);
	
	public Timestamp getLastTime(@Param("tableName")String tableName);
	
	public void saveOrUpdateNpVersionManager(NpVersionManager npVersionManager);
}
