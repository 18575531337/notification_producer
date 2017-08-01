package com.haizhi.np.dispatch.mongo;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 *	@author zhangjunfei 
 */
public abstract class AbstractBaseMongoTemplate implements ApplicationContextAware{
	
    protected MongoTemplate mongoTemplate;  
    
    private ApplicationContext applicationContext;

    public void setMongoTemplate(MongoTemplate mongoTemplate) {  
        this.mongoTemplate = mongoTemplate;  
    }  

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		 MongoTemplate mongoTemplate = applicationContext.getBean("mongoTemplate", MongoTemplate.class);  
		 this.applicationContext = applicationContext;
	     setMongoTemplate(mongoTemplate); 
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	
}
