package com.haizhi.np.dispatch.servicemysql;

import java.util.List;

import com.haizhi.np.dispatch.bean.FollowList;
import com.haizhi.np.dispatch.mapper.NpVersionManagerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//test
@Service
public class FollowListService {

	@Autowired
	private NpVersionManagerDao followListDao;
	
	public NpVersionManagerDao getFollowListDao() {
		return followListDao;
	}

	public void setFollowListDao(NpVersionManagerDao followListDao) {
		this.followListDao = followListDao;
	}

	public void test(){
		List<FollowList> list = this.followListDao.select();
		System.out.println(list);
	}
}
