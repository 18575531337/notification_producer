package com.haizhi.np.dispatch.mongo.dao.impl;

import org.springframework.stereotype.Component;

import com.haizhi.np.dispatch.mongo.AbstractBaseMongoTemplate;
import com.haizhi.np.dispatch.mongo.dao.ZhiXingInfoDao;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

//test
@Component
public class ZhiXingInfoDaoImpl extends AbstractBaseMongoTemplate implements ZhiXingInfoDao{

	/*
	@Override
	public void selectOne() {
		// TODO Auto-generated method stub
		Query query = new Query();
//		CriteriaDefinition c= new CriteriaDefinition();
//		
//		query.addCriteria(criteriaDefinition)
		query.limit(1);
		List<ZhiXingInfo> list = mongoTemplate.find(query, ZhiXingInfo.class);

		System.out.println(list);
	}
*/
	/**/
	public void selectOne() {
		// TODO Auto-generated method stub
/*
		CommandResult result = mongoTemplate.executeCommand("{distinct:'court_ktgg',key:'_utime',sort:-1,limit:1}");
		
		Set<Entry<String, Object>> set = result.entrySet();
		
		System.err.println(set);*/
		
		DBCollection db = mongoTemplate.getCollection("court_ktgg");

		
		
		BasicDBObject query = new BasicDBObject();
		//query.put("_utime", new BasicDBObject("$gt", "2017-04-11 16:20:47"));
		//query.put("name","_utime");

		DBCursor ite = db.find(query).sort(new BasicDBObject("_utime", -1)).limit(1);
		System.err.println(ite.next().get("litigant_list"));
		
//		DBCursor ite = db.find(query);
//		System.out.println(ite.count());
		
		
		
//		DBCursor cursor = db.find();
//		db.find(query)
//		
//		while(cursor.hasNext()){
//			DBObject obj = cursor.next();
//			System.out.println(obj);
//			//obj.keySet()
//		}
		
		//List list = db.distinct("_utime");
		
		//System.err.println(list);
	}
	
}
