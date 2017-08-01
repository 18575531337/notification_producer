package com.haizhi.np.dispatch.mongo.dao.impl;

import java.util.List;

import com.haizhi.np.dispatch.bean.RuleMapper;
import com.haizhi.np.dispatch.mongo.dao.CrewlerInfoDao;
import com.haizhi.np.dispatch.service.MonitorRuleAndRecordService;
import com.mongodb.DBObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.haizhi.np.dispatch.mongo.AbstractBaseMongoTemplate;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import org.springframework.util.StringUtils;

@Component
public class CrewlerInfoDaoImpl extends AbstractBaseMongoTemplate implements CrewlerInfoDao {
	
	private static Logger logger = LogManager.getLogger(CrewlerInfoDaoImpl.class);

	public List<Object> getAllByTableName(String tableName) {
		// TODO Auto-generated method stub
		Query query = new Query();
		return null;
	}

	public String getMaxTime(String mongoTableName) throws Throwable{
		DBCollection db = mongoTemplate.getCollection(mongoTableName);
		BasicDBObject query = new BasicDBObject();
		DBCursor ite = db.find(query).sort(new BasicDBObject("_utime", -1)).limit(1);
		logger.debug("getMaxTime--3"+"    -"+mongoTableName +ite.count());
		//logger.debug("getMaxTime--3"+"    -"ite.);
		DBObject item = ite.next();
		logger.debug("getMaxTime--"+item.get("_utime")+"        "+mongoTableName+"       "+item.toString());
		return item.get("_utime").toString();
//		while(ite.hasNext()){
//			logger.debug("getMaxTimeite.next");
//			return ite.next().get("_utime").toString();
//		}
//		
//		logger.error("mongodb : "+mongoTableName+"     获取最大时间异常 ！ 查询结果集数量 为 0 ！");
//		throw new Exception("mongodb : "+mongoTableName+"     获取最大时间异常 ！ 查询结果集数量 为 0 ！");
		
	}
	
	public DBCursor getIncListByTime(String mongoTableName,String lastTime){
		DBCollection db = mongoTemplate.getCollection(mongoTableName);

		BasicDBObject query = new BasicDBObject();
		query.put("_utime", new BasicDBObject("$gt",lastTime));
		DBCursor ite = db.find(query);
		return ite;
	}

	public DBCursor getResultByPage(String mongoTableName,String lastTime,int offset,int limit){
		DBCollection db = mongoTemplate.getCollection(mongoTableName);

		BasicDBObject query = new BasicDBObject();
		query.put("_utime", new BasicDBObject("$gt",lastTime));
		DBCursor ite = db.find(query).skip(offset).limit(limit);
		return ite;
	}

	public int getCount(String mongoTableName,String lastTime){
		DBCollection db = mongoTemplate.getCollection(mongoTableName);

		BasicDBObject query = new BasicDBObject();
		query.put("_utime", new BasicDBObject("$gt",lastTime));
		return db.find(query).count();
	}

	public DBCursor getIncListByTime(RuleMapper ruleMapper, String lastTime,String item){
		DBCollection db = mongoTemplate.getCollection(ruleMapper.getMongodbTableName());

		BasicDBObject query = new BasicDBObject();
		query.put("_utime", new BasicDBObject("$gt",lastTime));
		query.put(ruleMapper.getOperationField(), "/^"+item+"/");
		DBCursor ite = db.find(query);
		return ite;
	}

	public DBCursor getDBObjectByCondition(String mongoTableName,String key,String value){
		DBCollection db = mongoTemplate.getCollection(mongoTableName);

		BasicDBObject query = new BasicDBObject();
		query.put(key,value);
		DBCursor ite = db.find(query);
		return  ite;
	}

	public DBObject getResultByCondition(String tableName,String id,String field,String fieldContent){
		DBCollection db = mongoTemplate.getCollection(tableName);
		BasicDBObject query = new BasicDBObject();
		if(!StringUtils.isEmpty(id)){
			query.put("_record_id",id);
		}
		query.put(field, fieldContent);
		DBCursor ite = db.find(query).limit(1);
		while(ite.hasNext()){
			DBObject obj = ite.next();
			return obj;
		}
		return null;
	}
}
