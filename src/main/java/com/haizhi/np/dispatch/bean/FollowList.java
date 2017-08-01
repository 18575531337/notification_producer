package com.haizhi.np.dispatch.bean;

import java.sql.Date;
import java.sql.Timestamp;

//test
public class FollowList {

	private String id;
	private String name;
	private Timestamp time;
	

	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.valueOf(this.getTime().getTime());
	}
	
	
	
}
