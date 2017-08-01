package com.haizhi.np.dispatch.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.middleware.follow.enums.NotificationType;
import com.haizhi.np.dispatch.bean.*;
import com.haizhi.np.dispatch.constants.EnterpriseStatus;
import com.haizhi.np.dispatch.constants.Format;
import com.haizhi.np.dispatch.mapper.NpVersionManagerDao;
import com.haizhi.np.dispatch.mapper.PushNotificationDao;
import com.haizhi.np.dispatch.mapper.RuleMapperDao;
import com.haizhi.np.dispatch.mongo.dao.impl.CrewlerInfoDaoImpl;
import com.haizhi.np.dispatch.util.Utils;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MonitorRuleAndRecordService {
	
	private static Logger logger = LogManager.getLogger(MonitorRuleAndRecordService.class);

	@Autowired
	private CrewlerInfoDaoImpl crewlerInfoDaoImpl;//mongodb
	
	@Autowired
	private RuleMapperDao ruleMapperDao;//mysql
	
	@Autowired
	private NpVersionManagerDao npVersionManagerDao;//mysql
	
	@Autowired
	private PushNotificationDao pushNotificationDao;//mysql

	@Deprecated
    public String getCompanyByEnterpriseSpecial(String field,DBObject item){
        String refKey = item.get("ref_record_id").toString();
        DBCursor ite = this.crewlerInfoDaoImpl.getDBObjectByCondition("enterprise_data_gov",
                "_record_id",refKey);
        while(ite.hasNext()){
            DBObject obj = ite.next();
            return obj.get("company").toString();
        }
        return null;
    }

	public Company getCompanyByEnterpriseSpecial(DBObject item,String fieldEndDate){
		String refKey = item.get("ref_record_id").toString();
		DBCursor ite = this.crewlerInfoDaoImpl.getDBObjectByCondition("enterprise_data_gov",
				"_record_id",refKey);
		while(ite.hasNext()){
			DBObject obj = ite.next();
			Company company = new Company();
			company.setName(obj.get("company").toString());
			SimpleDateFormat format = new SimpleDateFormat(Format.PATTERN_DATE);
			Object endDate = null;
			try {
				endDate = obj.get(fieldEndDate);
				if(!StringUtils.isEmpty(endDate)){
					Date date = format.parse(endDate.toString());
					company.setDate(date);
				}
			}catch(Exception e){
				logger.error("getCompanyByEnterpriseSpecial() 解析时间出错！endDate:"+endDate
						+"   _record_id : "+obj.get("_record_id")+"    mongoName : enterprise_data_gov",e);
				e.printStackTrace();
			}

			return company;
		}
		return null;
	}
	
	public void saveOrUpdateNpVersionManager(NpVersionManager npVersionManager){
		this.npVersionManagerDao.saveOrUpdateNpVersionManager(npVersionManager);
	}

	//推送特殊消息
    public void pushMsgToNotificationBySpecial(String field,String company,List<UserInNotification> notificationList,
            DBObject mongoItem,RuleMapper ruleMapper){
        //List<NotificationInfo> list = new ArrayList<NotificationInfo>();
		List<String> incList = null;
		List<String> companyList = new ArrayList<String>();

		for(UserInNotification notificationItem : notificationList){
            NotificationInfo item = new NotificationInfo();
            item.setNotificationTableName(notificationItem.getNotificationTableName());
            item.setUserID(notificationItem.getUserID());
            item.setCollected(0);
            item.setRead(0);
			if("business_status".equals(field)){
                String beforeChange = Utils.unicodeToString(mongoItem.get("before_change").toString());
                String afterChange = Utils.unicodeToString(mongoItem.get("after_change").toString());

                item.setTitle("经营状态由"+ beforeChange + "变为" + afterChange);
                item.setType(NotificationType.get("business_status_change").getCode().toString());

                JSONObject detail = new JSONObject();

                detail.put("before",beforeChange);
                detail.put("after",afterChange);

                if( beforeChange.equals(afterChange)||
						/*
                        EnterpriseStatus.STATUSCHANGEALL.contains(beforeChange) ||
                        !EnterpriseStatus.STATUSCHANGEALL.contains(afterChange)
                        */
						Utils.matchNegativeStatus(beforeChange) ||
						!Utils.matchNegativeStatus(afterChange)){
                    logger.info("企业经营状态前后一致，或者不再包含当中。"+beforeChange+"   ,   "+afterChange);
                	continue;
				}

                item.setDetail(detail.toString());
            }else if("shareholder_information".equals(field)){
				item.setTitle("企业股东中新出现上市公司");
                item.setType(NotificationType.get("new_listed_shareholder").getCode().toString());

                JSONObject result = new JSONObject();
				incList = getDetaiInclList(mongoItem,"shareholder_name");

				if(incList.size()<=0){
					continue;
				}
				for(String shareHolder : incList){
					if(isQuotedCompany(shareHolder)){
						companyList.add(shareHolder);
					}

				}
				if(companyList.size()<=0){
					continue;
				}
				//result.put("list",incList);
				result.put("list",companyList);
                //result.put("shareholderNameList",getDetaiInclList(mongoItem,"shareholder_name"));

                item.setDetail(result.toString());
            }else if("branch".equals(field)){
                item.setTitle("企业新增分支机构");
                item.setType(NotificationType.get("new_affiliate").getCode().toString());

                JSONObject result = new JSONObject();
				incList = getDetaiInclList(mongoItem,"compay_name");

				if(incList.size()<=0){
					continue;
				}
				result.put("list",incList);
                //result.put("branchList",getDetaiInclList(mongoItem,"compay_name"));

                item.setDetail(result.toString());
            }else if("invested_companies".equals(field)){
                item.setTitle("企业新增对外投资");
                item.setType(NotificationType.get("new_invested_company").getCode().toString());
                JSONObject result = new JSONObject();
				incList = getDetaiInclList(mongoItem,"name");

				if(incList.size()<=0){
					continue;
				}
				result.put("list",incList);
                //result.put("investedCompaniesList",getDetaiInclList(mongoItem,"name"));

                item.setDetail(result.toString());
            }else{
                if(!StringUtils.isEmpty(ruleMapper.getPushTitle())){
                    item.setTitle(ruleMapper.getPushTitle());
                }else{
                    item.setTitle(mongoItem.get(ruleMapper.getPushTitleField()).toString());
                }
                item.setType(NotificationType.get(ruleMapper.getMongodbTableName()).getCode().toString());
            }

            item.setCompany(company);

            SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            item.setPushTime(f.format(new Date()));

            //list.add(item);

			this.pushNotificationDao.insertNotification(item);
        }

        //this.pushNotificationDao.insertAllNotification(list);
		/*
        for(NotificationInfo item : list){
            this.pushNotificationDao.insertNotification(item);
        }
        */
    }

    private boolean isContainCommon(String field0 , String field1){
    	boolean flag = false;
    	if(field0.contains(field1) || field1.contains(field0)){
    		flag = true;
		}
    	return flag;
	}

	private boolean isQuotedCompany(String shareholder){
    	DBObject obj = this.crewlerInfoDaoImpl.getResultByCondition("ssgs_baseinfo",null,
				"company_full_name",shareholder);
    	if(obj != null){
    		return true;
		}
		return false;
	}

    private List<String> getDetaiInclList(DBObject mongoItem,String field){
        JSONArray beforeArray = JSON.parseArray(mongoItem.get("before_change").toString());
        JSONArray afterArray = JSON.parseArray(mongoItem.get("after_change").toString());


        List<String> vlist = new ArrayList<String>();

        JSONObject targetAfter = null;
        JSONObject targetBefore = null;
        boolean isContain = false;
        for(int i=0;i<afterArray.size();i++){
            targetAfter = (JSONObject)afterArray.get(i);
            String afterName = Utils.unicodeToString(targetAfter.get(field).toString());
            if(StringUtils.isEmpty(afterName)){
            	continue;
			}
            isContain = false;
            for(int j=0;j<beforeArray.size();j++){
                targetBefore = (JSONObject)beforeArray.get(j);
                String beforeName = Utils.unicodeToString(targetBefore.get(field).toString());
				if(StringUtils.isEmpty(beforeName)){
					continue;
				}
                if(afterName.equals(beforeName)){
                    isContain = true;
                }
            }
            if(!isContain){
                vlist.add(afterName);
            }
        }
        return vlist;
    }

	//推送消息到相关的 表中
	public void pushMsgToNotification(String company,List<UserInNotification> notificationList,
			DBObject mongoItem,RuleMapper ruleMapper,String targetField){
		List<NotificationInfo> list = new ArrayList<NotificationInfo>();
		String type = null;
		for(UserInNotification notificationItem : notificationList){
			NotificationInfo item = new NotificationInfo();
			item.setNotificationTableName(notificationItem.getNotificationTableName());
			item.setUserID(notificationItem.getUserID());
			item.setCollected(0);
			item.setRead(0);
			if(!StringUtils.isEmpty(ruleMapper.getPushTitle())){
			    item.setTitle(ruleMapper.getPushTitle());
			}else{
			    /*
			    if("judge_process".equals(ruleMapper.getMongodbTableName())){
                    item.setTitle(mongoItem.get("case_id").toString() + mongoItem.get("status").toString());
                }else{
                    item.setTitle(mongoItem.get(ruleMapper.getPushTitleField()).toString());
                }
                */
                String title = "";
			    for(String titleField : ruleMapper.getPushTitleField().split(",")){
					if(!StringUtils.isEmpty(mongoItem.get(titleField))){
						title = title + mongoItem.get(titleField).toString();
					}
                }
                item.setTitle(title);
            }
			item.setCompany(company);

			if("enterprise_owing_tax".equals(ruleMapper.getMongodbTableName())){
				type = NotificationType.get("owing_tax").getCode().toString();
			}else if("bid_detail".equals(ruleMapper.getMongodbTableName())){
				if("win_bid_company".equals(targetField)){
					type = NotificationType.get("win_info").getCode().toString();
				}else{
					type = NotificationType.get("bid_info").getCode().toString();
				}
			}else{
				type = NotificationType.get(ruleMapper.getMongodbTableName()).getCode().toString();
			}
			item.setType(type);
			
			SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			item.setPushTime(f.format(new Date()));

			//if(PushContentType.ALL.equalsIgnoreCase(ruleMapper.getPushContentType())){
                item.setDetail(mongoItem.toString());
            //}

			
			list.add(item);
			this.pushNotificationDao.insertNotification(item);
		}

		//this.pushNotificationDao.insertAllNotification(list);
		/*
		for(NotificationInfo item : list){
			this.pushNotificationDao.insertNotification(item);
		}
		*/
	}
	
	//获取所有企业名
	public List<String> getAllCompanyByMysqlTable(String mysqlTableName){
		return this.pushNotificationDao.getAllCompanyByMysqlTable(mysqlTableName);
	}
	
	public List<String> getAllUserByCompany(String company,String mysqlTableName){
		return this.pushNotificationDao.getAllUserByCompany(company,mysqlTableName);
	}
	
	//获取所有规则映射
	public List<RuleMapper> getAllRuleMapper(){
		return this.ruleMapperDao.getAllRuleMapper();
	}

	//根据规则映射 获取 mongodb 中 的增量信息
	public DBCursor getIncInfoByRuleMapper(RuleMapper ruleMapper){
		//获得上次更新时间
		/*
		Timestamp lastTime = this.npVersionManagerDao.getMaxUpdateTime(ruleMapper.getNpVersionTableName());
		if(lastTime == null || StringUtils.isEmpty(lastTime.toString())){
			lastTime = Timestamp.valueOf(getMaxTimeByMongo(ruleMapper.getMongodbTableName()));
			lastTime.setDate(lastTime.getDate()-2);
		}
		*/
		Timestamp lastTime = this.getLastTime(ruleMapper);

		logger.debug("寻找增量 ：" + ruleMapper.getMongodbTableName() +  " 上次时间 ："+lastTime);
		//找到增量
		return getIncInfoByMongo(ruleMapper.getMongodbTableName(),lastTime);
	}

	public DBCursor getResultsByPage(RuleMapper ruleMapper,int offset,int limit){
		Timestamp lastTime = this.getLastTime(ruleMapper);
		return this.crewlerInfoDaoImpl.getResultByPage(ruleMapper.getMongodbTableName(),lastTime.toString(),offset,limit);
	}

	public int getCount(RuleMapper ruleMapper){
		Timestamp lastTime = this.getLastTime(ruleMapper);
		return this.crewlerInfoDaoImpl.getCount(ruleMapper.getMongodbTableName(),lastTime.toString());
	}

	//根据规则映射 获取 mongodb 中 的增量信息
	public DBCursor getIncInfoByRuleMapper(RuleMapper ruleMapper,String company){
		Timestamp lastTime = this.getLastTime(ruleMapper);

		logger.debug("寻找增量 ：" + ruleMapper.getMongodbTableName() +  " 上次时间 ："+lastTime);
		//找到增量
		return getIncInfoByMongo(ruleMapper,lastTime,company);
	}

	public Timestamp getLastTime(RuleMapper ruleMapper){
		Timestamp lastTime = this.npVersionManagerDao.getLastTime(ruleMapper.getMongodbTableName());
		if(lastTime == null || StringUtils.isEmpty(lastTime.toString())){
			lastTime = Timestamp.valueOf(getMaxTimeByMongo(ruleMapper.getMongodbTableName()));
			lastTime.setDate(lastTime.getDate()-2);
		}
		return lastTime;
	}

	//根据mongo 找到最大时间
	public String getMaxTimeByMongo(String mongoTableName){
		//logger.debug("获取最大时间 - mongodbname ： "+mongoTableName);
		try {
			return this.crewlerInfoDaoImpl.getMaxTime(mongoTableName);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	//从mongo 中获取增量
	public DBCursor getIncInfoByMongo(String mongoTableName,Timestamp lastTime){
		return this.crewlerInfoDaoImpl.getIncListByTime(mongoTableName,lastTime.toString());
	}

	//从mongo 中获取增量
	public DBCursor getIncInfoByMongo(RuleMapper ruleMapper,Timestamp lastTime,String item){
		return this.crewlerInfoDaoImpl.getIncListByTime(ruleMapper,lastTime.toString(),item);
	}
	
	public List<NpVersionManager> getNpVersionManagerByType(String tableType){
		return this.npVersionManagerDao.selectNpVersionManager(tableType);
	}


	public CrewlerInfoDaoImpl getCrewlerInfoDaoImpl() {
		return crewlerInfoDaoImpl;
	}

	public void setCrewlerInfoDaoImpl(CrewlerInfoDaoImpl crewlerInfoDaoImpl) {
		this.crewlerInfoDaoImpl = crewlerInfoDaoImpl;
	}

	public RuleMapperDao getRuleMapperDao() {
		return ruleMapperDao;
	}

	public void setRuleMapperDao(RuleMapperDao ruleMapperDao) {
		this.ruleMapperDao = ruleMapperDao;
	}

	public NpVersionManagerDao getNpVersionManagerDao() {
		return npVersionManagerDao;
	}

	public void setNpVersionManagerDao(NpVersionManagerDao npVersionManagerDao) {
		this.npVersionManagerDao = npVersionManagerDao;
	}

	public PushNotificationDao getPushNotificationDao() {
		return pushNotificationDao;
	}

	public void setPushNotificationDao(PushNotificationDao pushNotificationDao) {
		this.pushNotificationDao = pushNotificationDao;
	}
	
	
}
