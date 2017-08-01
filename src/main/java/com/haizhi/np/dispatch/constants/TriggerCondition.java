package com.haizhi.np.dispatch.constants;

public interface TriggerCondition {

	//字段非空
	public static final int FIELDNOTNULL = 0;
	
	//状态变更
	public static final int STATUSCHANGE = 1;

	//增量大于存量
	public static final int INCMORETHANSTOCK = 2;

	//经营状态变更
	public static final int BUSINESSSTATUSCHANGE = 3;
	
	//企业 特殊情况
	public static final int ENTERPRISESPECIAL = -1;


}
