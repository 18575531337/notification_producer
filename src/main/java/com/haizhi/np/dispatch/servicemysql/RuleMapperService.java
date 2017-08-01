package com.haizhi.np.dispatch.servicemysql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haizhi.np.dispatch.bean.RuleMapper;
import com.haizhi.np.dispatch.mapper.RuleMapperDao;

@Service
public class RuleMapperService {

	@Autowired
	private RuleMapperDao ruleMapperDao;

	public List<RuleMapper> getAllRuleMapper(){
		return this.ruleMapperDao.getAllRuleMapper();
	}
	
	public RuleMapperDao getRuleMapperDao() {
		return ruleMapperDao;
	}

	public void setRuleMapperDao(RuleMapperDao ruleMapperDao) {
		this.ruleMapperDao = ruleMapperDao;
	}
}
