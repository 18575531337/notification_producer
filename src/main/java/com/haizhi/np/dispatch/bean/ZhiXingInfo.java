package com.haizhi.np.dispatch.bean;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

//test for mongodb
@Document(collection="zhixing_info")
public class ZhiXingInfo {

	@Field("province")
	private String province;
	
	@Field("court")
	private String court;
	
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCourt() {
		return court;
	}
	public void setCourt(String court) {
		this.court = court;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "court:"+this.court+"   province:"+this.province;
	}
	
	
	
}
