package com.haizhi.np.dispatch.bean;

import java.sql.Timestamp;

public class FollowItem {

	private String userID;
	private String companyName;
	
	private int riskNotify;
	private int marketingNotify;
	
	private Timestamp updateTime;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public int getRiskNotify() {
		return riskNotify;
	}

	public void setRiskNotify(int riskNotify) {
		this.riskNotify = riskNotify;
	}

	public int getMarketingNotify() {
		return marketingNotify;
	}

	public void setMarketingNotify(int marketingNotify) {
		this.marketingNotify = marketingNotify;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	
	
}
