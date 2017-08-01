package com.haizhi.np.dispatch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.haizhi.np.dispatch.bean.NotificationInfo;

public interface PushNotificationDao {

	public void insertAllNotification(List<NotificationInfo> list);
	
	public void insertNotification(NotificationInfo notificationInfo);
	
	public List<String> getAllCompanyByMysqlTable(@Param("mysqlTableName")String mysqlTableName);
	
	public List<String> getAllUserByCompany(@Param("company")String company,
			@Param("mysqlTableName")String mysqlTableName);

	public List<String> getAllCompanyByRisk(@Param("mysqlTableName")String mysqlTableName);

	public List<String> getAllCompanyByMarketing(@Param("mysqlTableName")String mysqlTableName);
}
