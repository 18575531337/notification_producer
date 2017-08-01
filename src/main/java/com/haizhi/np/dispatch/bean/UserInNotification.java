package com.haizhi.np.dispatch.bean;

public class UserInNotification {

	private String userID;
	private String notificationTableName;
	
	public UserInNotification(String userID,String notificationTableName){
		this.userID = userID;
		this.notificationTableName = notificationTableName;
	}
	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getNotificationTableName() {
		return notificationTableName;
	}
	public void setNotificationTableName(String notificationTableName) {
		this.notificationTableName = notificationTableName;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.userID +" ; "+this.notificationTableName;
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return this.toString().equals(obj.toString());
	}
	
	
}
