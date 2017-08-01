package com.haizhi.np.dispatch.bean;

import java.util.List;

//规则映射
public class RuleMapper {

	private String mongodbTableName;
	private String npVersionTableName;
	private String notificationTableName;//这个不需要了
	
	private String operationField;

	private String pushTitle;
	private String pushTitleField;



	private String pushContentType;
	private String pushContentField;
	
	private int ruleType;
	private int rulePushType;
	private int triggerCondition;

	private String fieldEndDate;

	public String getPushContentType() {
		return pushContentType;
	}

	public void setPushContentType(String pushContentType) {
		this.pushContentType = pushContentType;
	}

	public String getPushTitle() {
		return pushTitle;
	}

	public void setPushTitle(String pushTitle) {
		this.pushTitle = pushTitle;
	}

	public String getMongodbTableName() {
		return mongodbTableName;
	}
	public void setMongodbTableName(String mongodbTableName) {
		this.mongodbTableName = mongodbTableName;
	}
	public String getNpVersionTableName() {
		return npVersionTableName;
	}
	public void setNpVersionTableName(String npVersionTableName) {
		this.npVersionTableName = npVersionTableName;
	}
	public String getNotificationTableName() {
		return notificationTableName;
	}
	public void setNotificationTableName(String notificationTableName) {
		this.notificationTableName = notificationTableName;
	}
	public String getOperationField() {
		return operationField;
	}
	public void setOperationField(String operationField) {
		this.operationField = operationField;
	}
	public int getTriggerCondition() {
		return triggerCondition;
	}
	public void setTriggerCondition(int triggerCondition) {
		this.triggerCondition = triggerCondition;
	}

	public String getPushTitleField() {
		return pushTitleField;
	}
	public void setPushTitleField(String pushTitleField) {
		this.pushTitleField = pushTitleField;
	}
	public String getPushContentField() {
		return pushContentField;
	}
	public void setPushContentField(String pushContentField) {
		this.pushContentField = pushContentField;
	}
	public int getRuleType() {
		return ruleType;
	}
	public void setRuleType(int ruleType) {
		this.ruleType = ruleType;
	}
	public int getRulePushType() {
		return rulePushType;
	}
	public void setRulePushType(int rulePushType) {
		this.rulePushType = rulePushType;
	}

	public String getFieldEndDate() {
		return fieldEndDate;
	}

	public void setFieldEndDate(String fieldEndDate) {
		this.fieldEndDate = fieldEndDate;
	}
}
