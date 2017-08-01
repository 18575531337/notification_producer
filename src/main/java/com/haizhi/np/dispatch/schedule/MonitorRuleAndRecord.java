package com.haizhi.np.dispatch.schedule;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.haizhi.np.dispatch.bean.Company;
import com.haizhi.np.dispatch.cache.Cache;
import com.haizhi.np.dispatch.constants.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.haizhi.np.dispatch.bean.NpVersionManager;
import com.haizhi.np.dispatch.bean.RuleMapper;
import com.haizhi.np.dispatch.bean.UserInNotification;
import com.haizhi.np.dispatch.service.MonitorRuleAndRecordService;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 *
 *	@author zhangjunfei
 * 监控规则映射，并且写入 notification_地区后缀
 */
@Component
@SuppressWarnings("unchecked")
public class MonitorRuleAndRecord {
	
	private static Logger logger = LogManager.getLogger(MonitorRuleAndRecord.class);
	
	@Autowired
	private MonitorRuleAndRecordService monitorRuleAndRecordService;

	@Resource(name="cacheRuleRisk")
	private Cache cacheRuleRisk;
    @Resource(name="cacheRuleMarketing")
	private Cache cacheRuleMarketing;
	
	/**
	 *{
	 *	company:[{userID:0,notificationTable:"table0"}]
	 *}
	 *
	 */
	public static Map<String,List<UserInNotification>> cacheRisk =
			new ConcurrentHashMap<String,List<UserInNotification>>();
	
	private ExecutorService threadPool = Executors.newFixedThreadPool(70, new ThreadFactory(){

		public Thread newThread(Runnable r) {
			// TODO Auto-generated method stub
			Thread t = new Thread(r);
			t.setDaemon(true);
			return t;
		}
		
	});

	//@PostConstruct
    @Scheduled(cron = "${scheduled.MonitorRuleAndRecord}")
    void doService(){
        logger.info("定时任务开始[MonitorRuleAndRecord] 开始生产消息......");
        
        //获取所有规则 并 根据 规则 获取增量data
        getDataIncByNpVersion();
        
    }
	
	/**
	 *	@author zhangjunfei
	 *	以后可能重点修改的方法!
	 */
	private void getDataIncByNpVersion(){
		List<RuleMapper> ruleMapperList = this.monitorRuleAndRecordService.getAllRuleMapper();
		CountDownLatch countDown = new CountDownLatch(ruleMapperList.size());
		for(final RuleMapper ruleMapper : ruleMapperList){

			threadPool.execute(new Runnable(){
				public void run(){
					logger.info("开始处理规则 ："+ruleMapper.getMongodbTableName());
					//根据规则映射 获取增量信息
					try {
						DBCursor ite = monitorRuleAndRecordService.getIncInfoByRuleMapper(ruleMapper);
						SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Timestamp ts = Timestamp.valueOf(f.format(new Date()));
						//根据规则处理增量信息
						dealIncInfoByRuleMapper(ite, ruleMapper);

						updateNpVersionManager(ruleMapper, ts);
					}catch(Exception e){
						logger.error("",e);
					}

					logger.info(ruleMapper.getMongodbTableName() + "规则处理完毕 !   如果是特殊规则 则还在处理中。");
					countDown.countDown();
				}
			});
		}
		try {
			countDown.await();
			logger.info("-----------------------------------success 普通规则处理完毕----------------------------------------");
		}catch(Exception e){
			logger.error("计数器报错，规则当中或许有未知异常！",e);
		}
	}
	
	private void updateNpVersionManager(RuleMapper ruleMapper,Timestamp ts){
		NpVersionManager npVersionManager = new NpVersionManager();
		npVersionManager.setTableName(ruleMapper.getMongodbTableName());
		npVersionManager.setTableType(TableType.MONGODB);
		
		npVersionManager.setLastTime(ts);
		logger.info("np_version_manager  --  mongodbName :"+ruleMapper.getMongodbTableName() +
				"      time : "+ts.toString());
		this.monitorRuleAndRecordService.saveOrUpdateNpVersionManager(npVersionManager);
	}
	
	/**
	 *	根据规则处理增量信息
	 */
	private void dealIncInfoByRuleMapper(DBCursor ite,RuleMapper ruleMapper){
		//根据触发条件判断
		switch(ruleMapper.getTriggerCondition()){
			case TriggerCondition.FIELDNOTNULL :
				dealPushMsgByFidleNotNull(ite,ruleMapper);
				break;
			case TriggerCondition.ENTERPRISESPECIAL :
				int count = this.monitorRuleAndRecordService.getCount(ruleMapper);
				int n = 50;
				int inc = count/n;

				float r = (float)count/(float)n;
				if(r>inc){
					n = n+1;
				}

				if(inc == 0){
					n = 1;
				}
				logger.debug("查询到结果集数量 ："+count  +"     处理线程数量："+n +"      每批次处理数量 ："+inc);

				int start = 0;
				for(int i=0;i<n;i++){
					if(start >= count){
						logger.error("起始位置大于总数，所以退出。start :"+start+"    count:"+count);
						break;
					}
					DBCursor cursor = this.monitorRuleAndRecordService.getResultsByPage(ruleMapper,start,inc);
					threadPool.execute(new Runnable() {
						@Override
						public void run() {
							logger.debug("特殊规则在子线程中处理 ："+ruleMapper.getMongodbTableName());
							dealPushMsgByEnterpriseSpecial(cursor,ruleMapper);
						}
					});
					start = start + inc;
				}
				break;
			default:
				logger.error("dealIncInfoByRuleMapper 发生重大错误");
				break;
		}

	}

	private void dealPushMsgByEnterpriseSpecial(DBCursor ite,RuleMapper ruleMapper){
		while(ite.hasNext()){
			logger.debug("-----特殊规则----- ： "+ruleMapper.getMongodbTableName());
			DBObject item = ite.next();

			switch(item.get("field").toString()){
				case "business_status" :
					getAndPush("business_status",item,ruleMapper);
					break;
				case "shareholder_information" :
					getAndPush("shareholder_information",item,ruleMapper);
					break;
				case "branch" :
					getAndPush("branch",item,ruleMapper);
					break;
				case "invested_companies" :
					getAndPush("invested_companies",item,ruleMapper);
					break;
				default :
					logger.error("dealPushMsgByEnterpriseSpecial 发生重大错误");
					break;
			}

		}
	}

	private void getAndPush(String type,DBObject item,RuleMapper ruleMapper){
		Company company = this.monitorRuleAndRecordService.getCompanyByEnterpriseSpecial(item,
				ruleMapper.getFieldEndDate());
		if(company.getDate() != null && isOverDue(company.getDate(),DateType.MONTH)){
			return;
		}
		int ruleType = getRuleTypeBySpecial(type);
        List<UserInNotification> list = null;
		if(ruleType == RuleType.RISK){
		    list = (List<UserInNotification>)cacheRuleRisk.getCache().get(company.getName());
        }else if(ruleType == RuleType.MARKETING){
            list = (List<UserInNotification>)cacheRuleMarketing.getCache().get(company.getName());
        }
        if(isNotificationed(company.getName(),ruleType)){
			this.monitorRuleAndRecordService.pushMsgToNotificationBySpecial(type,
					company.getName(),list,item,ruleMapper);
		}
	}

	private int getRuleTypeBySpecial(String type){
        switch (type){
            case MongoDBTableName.BUSINESS_STATUS :
                return RuleType.RISK;
            case MongoDBTableName.SHAREHOLDER_INFORMATION :
                return RuleType.MARKETING;
            case MongoDBTableName.INVESTED_COMPANIES :
                return RuleType.MARKETING;
            case MongoDBTableName.BRANCH :
                return RuleType.MARKETING;
        }
        return RuleType.ERROR;
    }
	
	private void pushMsg(String company,DBObject itemInfo,RuleMapper ruleMapper,String targetField){
        List<UserInNotification> notificationList = null;
        if(ruleMapper.getRuleType() == RuleType.RISK){
            notificationList = (List<UserInNotification>)this.cacheRuleRisk.getCache().get(company);
        }else if(ruleMapper.getRuleType() == RuleType.MARKETING){
            notificationList = (List<UserInNotification>)this.cacheRuleMarketing.getCache().get(company);
        }
		this.monitorRuleAndRecordService.pushMsgToNotification(company,notificationList,
				itemInfo,ruleMapper,targetField);
	}

	@Deprecated
	private boolean isNotificationed(String company){
		if(cacheRisk.size() <= 0){
			synchronized (cacheRisk) {
				if(cacheRisk.size() <= 0) {
					initCacheRisk();
				}
			}
		}
		if(cacheRisk.containsKey(company)){
			return true;
		}
		return false;
	}

	private boolean isNotificationed(String company,int ruleType){
	    if(ruleType == RuleType.RISK){
            if(cacheRuleRisk.size() <= 0){
                cacheRuleRisk.init();
            }
            if(cacheRuleRisk.containsKey(company)){
                return true;
            }
            return false;
        }else if(ruleType == RuleType.MARKETING){
            if(cacheRuleMarketing.size() <= 0){
                cacheRuleMarketing.init();
            }
            if(cacheRuleMarketing.containsKey(company)){
                return true;
            }
            return false;
        }
        return false;
    }
	
	private void dealPushMsgByFidleNotNull(DBCursor ite,RuleMapper ruleMapper){
		Collection<String> colList = null;
		Map<String,String> map = null;
		String targetCompany = null;
		SimpleDateFormat format = new SimpleDateFormat(Format.PATTERN_DATE);
		SimpleDateFormat formatYear = new SimpleDateFormat(Format.PATTERN_DATE_YEAR);
		while(ite.hasNext()){
			logger.debug("规则字段不为空 运行当中 。。。。。。。："+ruleMapper.getMongodbTableName());
			DBObject item = ite.next();
			//判断是否过期
			if(!StringUtils.isEmpty(ruleMapper.getFieldEndDate()) ){
				Date d = null;
				try {
					Object endDate = item.get(ruleMapper.getFieldEndDate());
					if(!StringUtils.isEmpty(endDate)){
						if("tax_payer_level_A".equals(ruleMapper.getMongodbTableName())){
							d = formatYear.parse(endDate.toString());
							if (isOverDue(d,DateType.YEAR)) {
								continue;
							}
						}else{
							d = format.parse(endDate.toString());
							if (isOverDue(d,DateType.MONTH)) {
								continue;
							}
						}
					}
				}catch(Exception e){
					logger.error("判断是否过期时报错！ _record_id : "+item.get("_record_id")
							+"     mongoName : "+ruleMapper.getMongodbTableName(),e);
				}
			}
			String[] operationFields = ruleMapper.getOperationField().split(",");

			for(String operationField : operationFields){
				Object fieldInfo = item.get(operationField);
				if(fieldInfo == null || StringUtils.isEmpty(fieldInfo.toString())){
					continue;
				}
				String t = fieldInfo.toString();
				if(t.startsWith("[")){
					List<String> list = null;
					try{
					    list = JSON.parseArray(t,String.class);
                    }catch(Exception e){
						logger.error("获取到脏数据，解析数据报错！不影响程序，直接跳过。"+t
								+"      _record_id:"+item.get("_record_id")+
								"    mongodbName:"+ruleMapper.getMongodbTableName(),e);
						continue;
                    }
					for(String itemCompany : list){
						if(itemCompany.startsWith("{")){
							try {
								map = JSON.parseObject(itemCompany, Map.class);
							}catch(Exception e){
								logger.error("获取到脏数据，解析数据报错！不影响程序，直接跳过。"+itemCompany
										+"      _record_id:"+item.get("_record_id")+
										"    mongodbName:"+ruleMapper.getMongodbTableName(),e);
								continue;
							}
							colList = map.values();
							for(String itemOne : colList){
								if(isNotificationed(itemOne,ruleMapper.getRuleType())){
									pushMsg(itemOne,item,ruleMapper,operationField);
								}
							}
						}else if(itemCompany.contains(":")){
							try {
								targetCompany = itemCompany.split(":")[1];
							}catch(Exception e){
								logger.error("获取到脏数据，解析数据报错！不影响程序，直接跳过。"+itemCompany
										+"      _record_id:"+item.get("_record_id")+
										"    mongodbName:"+ruleMapper.getMongodbTableName(),e);
								continue;
							}
							if(isNotificationed(targetCompany,ruleMapper.getRuleType())){
								pushMsg(targetCompany,item,ruleMapper,operationField);
							}
						}else{
							if(isNotificationed(itemCompany,ruleMapper.getRuleType())){
								pushMsg(itemCompany,item,ruleMapper,operationField);
							}
						}
					}
				}else{
					if(isNotificationed(t,ruleMapper.getRuleType())){
						pushMsg(t,item,ruleMapper,operationField);
					}
				}
			}

		}
	}

	/**
		判断是否 过期 ， 默认 一个year
	 */
	private boolean isOverDue(Date endDate,String byExample){
		try {
			Date now = new Date();
			Calendar nowCalendar = Calendar.getInstance();
            nowCalendar.setTime(now);

            Calendar endDateCalendar = Calendar.getInstance();
            endDateCalendar.setTime(endDate);

			if(DateType.MONTH.equals(byExample)){
				nowCalendar.set(Calendar.MONTH,nowCalendar.get(Calendar.MONTH)-1);
			}else if(DateType.YEAR.equals(byExample)){
				nowCalendar.set(Calendar.YEAR,nowCalendar.get(Calendar.YEAR)-1);

				return nowCalendar.get(Calendar.YEAR) <= endDateCalendar.get(Calendar.YEAR);
			}

			if(nowCalendar.compareTo(endDateCalendar) <= 0){
				return false;
			}
		}catch(Exception e){
			logger.error("运行isOverDue()时报错！ endDate : "+endDate.toString(),e);
			e.printStackTrace();
		}
		return true;
	}
	
	@Scheduled(cron = "${scheduled.MonitorRuleAndRecord.updateCacheRisk}")
	void updateCache(){
		logger.info("定时任务开始[MonitorRuleAndRecord] 更新缓存......");
		//cacheRisk.clear();
		//initCacheRisk();
        cacheRuleMarketing.update();
        cacheRuleRisk.update();
        logger.info("---------------------------风险规则、营销规则 缓存初始化完成 cache update finish---------------------------");
    }

	@Deprecated
	private void initCacheRisk(){
		logger.info("定时任务开始[MonitorRuleAndRecord] 填充缓存cacheRisk数据......");
		List<NpVersionManager> mysqlList = this.monitorRuleAndRecordService.
				getNpVersionManagerByType(TableType.MYSQL);
		
		List<UserInNotification> duplicatList = null;
		
		for(NpVersionManager mysqlItem : mysqlList){
			List<String> allCompany = this.monitorRuleAndRecordService.getAllCompanyByMysqlTable(
					mysqlItem.getTableName());
			
			for(String companyItem : allCompany){
				
				List<String> userIDList = this.monitorRuleAndRecordService.getAllUserByCompany(companyItem,
						mysqlItem.getTableName());
				
				for(String userID : userIDList){
					UserInNotification userInNotification = new UserInNotification(userID,
							mysqlItem.getNotificationTableName());
					if(cacheRisk.containsKey(companyItem)){
						duplicatList = cacheRisk.get(companyItem);
						
						if(!duplicatList.contains(userInNotification)){
							duplicatList.add(userInNotification);
						}
					}else{
						List<UserInNotification> l = new ArrayList<UserInNotification>();
						l.add(userInNotification);
						cacheRisk.put(companyItem, l);
					}
				}
				
			}
		}
		logger.info("---------------------------风险规则、营销规则 缓存初始化完成 cache finish---------------------------");
		logger.debug("cacheRisk : "+cacheRisk);
	}

	public MonitorRuleAndRecordService getMonitorRuleAndRecordService() {
		return monitorRuleAndRecordService;
	}

	public void setMonitorRuleAndRecordService(MonitorRuleAndRecordService monitorRuleAndRecordService) {
		this.monitorRuleAndRecordService = monitorRuleAndRecordService;
	}

    public Cache getCacheRuleRisk() {
        return cacheRuleRisk;
    }

    public void setCacheRuleRisk(Cache cacheRuleRisk) {
        this.cacheRuleRisk = cacheRuleRisk;
    }

    public Cache getCacheRuleMarketing() {
        return cacheRuleMarketing;
    }

    public void setCacheRuleMarketing(Cache cacheRuleMarketing) {
        this.cacheRuleMarketing = cacheRuleMarketing;
    }
}
