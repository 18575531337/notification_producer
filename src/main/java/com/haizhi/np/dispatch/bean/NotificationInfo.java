package com.haizhi.np.dispatch.bean;

public class NotificationInfo {

	private String notificationTableName;
	private String userID;
	private String title;
	private String company;
	private int read;
	private int collected;
	private String detail;
	private String pushTime;
	private String type;
	

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNotificationTableName() {
		return notificationTableName;
	}
	public void setNotificationTableName(String notificationTableName) {
		this.notificationTableName = notificationTableName;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public int getRead() {
		return read;
	}
	public void setRead(int read) {
		this.read = read;
	}
	public int getCollected() {
		return collected;
	}
	public void setCollected(int collected) {
		this.collected = collected;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getPushTime() {
		return pushTime;
	}
	public void setPushTime(String pushTime) {
		this.pushTime = pushTime;
	}
}
