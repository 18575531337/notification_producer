package com.haizhi.np.dispatch.constants;

import java.util.regex.Pattern;

public interface EnterpriseStatus {

	/**
	 *	吊销／注销／清算／停业／撤销”
	 */
	public static final String CANCLE = "吊销";
	
	public static final String CANCELLATION = "注销";
	
	public static final String LIQUIDATION = "清算";
	
	public static final String OUTOFBUSINESS = "停业";
	
	public static final String REVOKE = "撤销";
	
	public static final String STATUSCHANGEALL = "吊销／注销／清算／停业／撤销";

	public static final String ENTTERPRISESTATUSREGEX = "(.*吊销.*)|(.*注销.*)|(.*清算.*)|(.*停业.*)|(.*撤销.*)";

}
