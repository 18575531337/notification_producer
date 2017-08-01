package com.haizhi.np.dispatch.schedule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
public class ScheduledProductor {
	
	private static Logger logger = LogManager.getLogger(ScheduledProductor.class);
	
	@Value("${ds.mysql.driverClass}")
	private String v;
	
	//@PostConstruct
    //@Scheduled(cron = "${scheduled.MonitorFollowIncAndPost}")//每隔5s生产消息
	@Scheduled(cron = "0/5 * * * * ?")//每隔5s生产消息
    void doProduct(){
		System.out.println(this.v);
    }
    
    @Scheduled(cron = "0/5 * * * * ?")//每隔5s生产消息
    void cc(){
    	System.out.println("update");
    }
}
