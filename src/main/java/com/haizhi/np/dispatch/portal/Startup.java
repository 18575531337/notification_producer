package com.haizhi.np.dispatch.portal;

import java.util.ServiceLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *	@author zhangjunfei 
 */
public class Startup {
	
	private static Logger log = LogManager.getLogger(Startup.class);
	
	public static volatile boolean isRunning = true;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		log.info("调度系统开始启动！");
		try{
			final ServiceLoader<IServiceLoader> services = ServiceLoader.load(IServiceLoader.class);
			
			Runtime.getRuntime().addShutdownHook(new Thread(){
				public void run(){
					doDestroy(services);
				}
			});
			
			doInit(services);
			
			log.info("调度系统开始启动 --成功-- 。");
		}catch(Exception e){
			log.error("调度系统开始启动 --失败--！!",e);
			e.printStackTrace();
			System.exit(1);
		}

		synchronized(Startup.class){
			while(isRunning){
				try {
					Startup.class.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.error("调度系统开始启动 --莫名其妙失败--！!",e);
					System.exit(1);
				}
			}
		}
	}
	
	private static void doInit(ServiceLoader<IServiceLoader> services){
		for(IServiceLoader s : services){
			log.info("调度系统 - "+s.getName() +" - 开始初始化 。。。");
			s.init();
			log.info("调度系统 - "+s.getName() +" - 初始化完成 。");
		}
	}
	
	private static void doDestroy(ServiceLoader<IServiceLoader> services){
		for(IServiceLoader s : services){
			log.info("调度系统 - "+s.getName() +" - 开始销毁 。。。");
			s.destroy();
			log.info("调度系统 - "+s.getName() +" - 成功销毁 。");
		}
	}

}
