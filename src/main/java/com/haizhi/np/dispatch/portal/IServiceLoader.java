package com.haizhi.np.dispatch.portal;

/**
 *	@author zhangjunfei 
 */
public interface IServiceLoader {

	public void init();
	
	public void destroy();
	
	public String getName();
}
