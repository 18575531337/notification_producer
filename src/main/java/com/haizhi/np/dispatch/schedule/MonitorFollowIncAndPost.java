package com.haizhi.np.dispatch.schedule;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.np.dispatch.bean.NpVersionManager;
import com.haizhi.np.dispatch.constants.TableType;
import com.haizhi.np.dispatch.http.HttpSender;
import com.haizhi.np.dispatch.mapper.NpVersionManagerDao;
import com.haizhi.np.dispatch.mongo.AbstractBaseMongoTemplate;

/**
 *	@author zhangjunfei
 *	监控关注增量，对企业去重然后投放爬虫
 */
@Component
public class MonitorFollowIncAndPost extends AbstractBaseMongoTemplate implements EnvironmentAware{

	private static Logger logger = LogManager.getLogger(MonitorFollowIncAndPost.class);
	
	private Environment env;
	
	@Autowired
	private NpVersionManagerDao npVersionManagerDao;
	
	//爬虫uri
	//@Value("${crawlerUpdatecompanydataUri}")
	private static String crawlerUpdatecompanydataUri;

	@Value("${scheduled.MonitorFollowIncAndPost.isEnable}")
	private boolean isEnable;
	
	private ExecutorService threadPool = Executors.newFixedThreadPool(20, new ThreadFactory(){

		public Thread newThread(Runnable r) {
			// TODO Auto-generated method stub
			Thread t = new Thread(r);
			t.setDaemon(true);
			return t;
		}
		
	});
	
	//@PostConstruct
    @Scheduled(cron = "${scheduled.MonitorFollowIncAndPost}")//每隔5s生产消息
    void doService(){
    	if(!this.isEnable){
    		logger.info("投放爬虫程序关闭 ！");
    		return;
		}
        logger.info("定时任务开始[MonitorFollowIncAndPost]获取增量并投放爬虫......");
        long begin = System.currentTimeMillis();
        
        //List<NpVersionManager> npVersionManagerList = this.npVersionManagerDao.selectAllNpVersionManager();
        List<NpVersionManager> npVersionManagerList = this.npVersionManagerDao.selectNpVersionManager(
        		TableType.MYSQL);
        Set<String> companyIncList = new HashSet<String>();
        for(NpVersionManager item : npVersionManagerList){
        	
        	String lastTime = null;
        	if(item.getLastTime() == null || StringUtils.isEmpty(item.getLastTime().toString())){
        		logger.debug("NpVersionManager 为空 取所有时间段 公司");
        		Timestamp ts = this.npVersionManagerDao.getMaxUpdateTime(item.getTableName());
        		ts.setDate(ts.getDate()-2);
        		lastTime = ts.toString();
        	}else{
        		lastTime = item.getLastTime().toString();
        	}
        	companyIncList.addAll(this.npVersionManagerDao.selectFollowItemListByNpVersion(
        			item.getTableName(),lastTime));
        }
        
        //投放爬虫
        postCrawler(companyIncList);
        
        //更新管理表
        updateNpVersionManager(npVersionManagerList);
    }
    
    private void updateNpVersionManager(List<NpVersionManager> npVersionManagerList){
    	//List<NpVersionManager> npVersionManagerList = this.npVersionManagerDao.selectAllNpVersionManager();
    	for(NpVersionManager item : npVersionManagerList){
    		Timestamp time = this.npVersionManagerDao.getMaxUpdateTime(item.getTableName());
    		item.setLastTime(time);
    	}
    	
    	this.npVersionManagerDao.updateNpVersionManager(npVersionManagerList);
    	//return npVersionManagerList;
    }
    
    //投放爬虫
    private void postCrawler(Set<String> set){
    	final HttpSender httpSender = getApplicationContext().getBean(HttpSender.class);
    	
    	//一个简陋的分批处理
    	int i = 1;
    	int j = 1;
    	Set<String> itemList = new HashSet<String>();
    	for(String item : set){
    		itemList.add(item);
    		if(i == 100 || j == set.size()){
    			final String data = buildCrawlerJson(set);
    			this.threadPool.execute(new Runnable(){
    				
					public void run() {
						// TODO Auto-generated method stub
						logger.debug("投放爬虫 data ："+data);
						httpSender.send(crawlerUpdatecompanydataUri, data);
					}
    				
    			});
    			
    			itemList.clear();
    			i=1;
    		}
    		i++;
    		j++;
    	}
    	//httpSender.send(this.crawlerUpdatecompanydataUri, buildCrawlerJson(set));
    }
    
    private String buildCrawlerJson(Set<String> set){

    	JSONObject json = new JSONObject();
    	JSONArray list = new JSONArray();
    	for(String company : set){
    		JSONObject item = new JSONObject();
    		item.put("company", company);
    		item.put("level", 3);
    		
    		list.add(item);
    	}
    	
    	json.put("data", list);
    	return json.toJSONString();
    }

	public NpVersionManagerDao getFollowListDao() {
		return npVersionManagerDao;
	}

	public void setFollowListDao(NpVersionManagerDao npVersionManagerDao) {
		this.npVersionManagerDao = npVersionManagerDao;
	}

	public boolean isEnable() {
		return isEnable;
	}

	public void setEnable(boolean enable) {
		isEnable = enable;
	}

	public void setEnvironment(Environment environment) {
		// TODO Auto-generated method stub
		this.env = environment;
		crawlerUpdatecompanydataUri = this.env.getProperty("crawlerUpdatecompanydataUri");
	}

}
