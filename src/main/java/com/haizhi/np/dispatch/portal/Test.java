package com.haizhi.np.dispatch.portal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.np.dispatch.util.Utils;

public class Test {

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
//		Date d0 = Date.valueOf("2017-03-29 10:19:08");
//		
//		Date d1 = Date.valueOf("2017-03-29 10:19:07");
//		
//		boolean flag = d0.after(d1);
//		
//		System.out.println(flag);
		/*
		SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
		Date d0 = f.parse("2017-03-29 10:19:07");
		Date d1 = f.parse("2017-03-29 10:19:08");

		
		System.out.println(d0.after(d1));
		
		System.out.println(d0);
		
		List<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		
		Set<String> set = new HashSet<String>();
		
		set.addAll(list);
		System.out.println(set);
		
		for(String a : set){
			System.out.println(a);
		}
		
		System.out.println(buildCrawlerJson(set));
		
		set.clear();
		
		System.out.println(set);
		*/
		
		/*
		Timestamp ts = Timestamp.valueOf("2017-04-01 17:44:39");
		
		ts.setDate(ts.getDate() - 2);
		System.out.println(ts.getDate());
		
		long s = ts.getTime();
		System.out.println(ts);
		*/
		
//		SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//
//		Date d = new Date();
//
//		System.out.println(f.format(d));
//
//		Integer i = new Integer(111);
//		System.out.println(i.toString());
		
//		ArrayList list = new ArrayList();
//		
//		UserInNotification c = new UserInNotification();
//		c.setUserID("cc");
//		c.setNotificationTableName("dd");
//		
//		UserInNotification d1 = new UserInNotification();
//		d1.setUserID("cc");
//		d1.setNotificationTableName("dd");
//		
//		list.add(c);
		
		//System.out.println(list.contains(d1));

//		JSONObject result = new JSONObject();
//		List<String> list = new ArrayList<String>();
//
//		list.add("a");
//		list.add("b");
//		result.put("list",list);
//		System.out.println(list);

		try{
			System.out.println("1");
			int i = 1/0;
		}catch (Exception e){
			System.out.println("2");
			e.printStackTrace();
			System.out.println("3");
		}
		System.out.println("4");
	}
	
	   private static String buildCrawlerJson(Set<String> set){

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

}
