package com.haizhi.np.dispatch.mongo.dao;

import java.util.List;

import com.mongodb.DBCursor;

public interface CrewlerInfoDao {

	public List<Object> getAllByTableName(String tableName);
	
	public String getMaxTime(String mongoTableName) throws Throwable;
	
	public DBCursor getIncListByTime(String mongoTableName,String lastTime);
}
