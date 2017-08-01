package com.haizhi.np.dispatch.portal;

import com.haizhi.np.dispatch.mongo.dao.impl.ZhiXingInfoDaoImpl;
import com.haizhi.np.dispatch.servicemysql.FollowListService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *	@author zhangjunfei 
 */
public class SpringContext implements IServiceLoader{

	public static ApplicationContext context = null;
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		System.out.println("init");
		context = new AnnotationConfigApplicationContext(SpringConfig.class);
		
		//mySQLtest();
		//mongoDBTest();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		System.out.println("destroy");
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

	public void mySQLtest(){
		FollowListService service = context.getBean(FollowListService.class);
		service.test();
	}
	
	public void mongoDBTest(){
		ZhiXingInfoDaoImpl zxImpl = context.getBean(ZhiXingInfoDaoImpl.class);
		zxImpl.selectOne();
	}
	
}
