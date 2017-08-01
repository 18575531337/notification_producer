package com.haizhi.np.dispatch.constants;

public interface PushType {

	//litigant_list包含目标企业名  企业涉诉
	public static final int ENTERPRISELITIGATION = 0;
	//EnterpriseLitigation(0),
	
	//企业失信被执行
	//EnterpriseDishonesty(1),
	public static final int ENTERPRISEDISHONESTY = 1;
	
	//企业欠税被披露
	//EnterpriseOwingTaxes(2),
	public static final int ENTERPRISEOWINGTAXES = 2;
	
	//企业被行政处罚
	//EnterpriseAdministrativeSanction(3),
	public static final int ENTERPRISEADMINISTRATIVESANCTION = 3;
	
	//企业的经营状态变
	//EnterpriseManagementStatus(4),
	public static final int ENTERPRISEMANAGEMENTSTATUS = 4;
	
	//企业股东中新出现上市公司
	//EnterpriseShareholdersAppearInListedCompanies(5),
	public static final int ENTERPRISESHAREHOLDERSAPPEARINLISTEDCOMPANIES = 5;
	
	//企业被公布纳税等级为A
	//EnterpriseRatepayingLevelA(6),
	public static final int ENTERPRISERATEPAYINGLEVELA = 6;
	
	//企业中标
	//EnterpriseBid(7),
	public static final int ENTERPRISEBID = 7;
	
	//企业新增分支机构
	//EnterpriseNewBranches(8),
	public static final int ENTERPRISENEWBRANCHES = 8;
	
	//企业新增对外投资
	//EnterpriseNewForeignInvestment(9);
	public static final int ENTERPRISENEWFOREIGNINVESTMENT = 9;
	
}
