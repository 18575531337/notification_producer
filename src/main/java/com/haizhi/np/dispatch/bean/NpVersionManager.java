package com.haizhi.np.dispatch.bean;

import java.sql.Timestamp;
import java.util.Date;

public class NpVersionManager {

	private String id;
	private String tableName;
	private String notificationTableName;
	private String desc;
	//private Date lastTime;
	private Timestamp lastTime;
	
	private String tableType;
	

	
	public String getNotificationTableName() {
		return notificationTableName;
	}
	public void setNotificationTableName(String notificationTableName) {
		this.notificationTableName = notificationTableName;
	}
	public String getTableType() {
		return tableType;
	}
	public void setTableType(String tableType) {
		this.tableType = tableType;
	}
	public Timestamp getLastTime() {
		return lastTime;
	}
	public void setLastTime(Timestamp lastTime) {
		this.lastTime = lastTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
//	public Date getLastTime() {
//		return lastTime;
//	}
//	public void setLastTime(Date lastTime) {
//		this.lastTime = lastTime;
//	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "table_name : "+this.tableName+"   date:"+this.lastTime;
	}
	
	
}
