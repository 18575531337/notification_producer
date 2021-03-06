package com.haizhi.np.dispatch.cache.impl;

import com.haizhi.np.dispatch.bean.NpVersionManager;
import com.haizhi.np.dispatch.bean.UserInNotification;
import com.haizhi.np.dispatch.cache.Cache;
import com.haizhi.np.dispatch.constants.TableType;
import com.haizhi.np.dispatch.mapper.NpVersionManagerDao;
import com.haizhi.np.dispatch.mapper.PushNotificationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by haizhi on 2017/7/25.
 */
@Component("cacheRuleMarketing")
@SuppressWarnings("unchecked")
public class CacheRuleMarketing implements Cache{

    @Autowired
    private PushNotificationDao pushNotificationDao;

    @Autowired
    private NpVersionManagerDao npVersionManagerDao;

    private  Map<String,List<UserInNotification>> cacheMarketing = new ConcurrentHashMap<String,List<UserInNotification>>();

    public Map<String,List<UserInNotification>> getCache(){
        return cacheMarketing;
    }

    public void init(){
        if(this.cacheMarketing.size()<=0){
            synchronized (this.cacheMarketing){
                if(this.cacheMarketing.size()<=0){
                    List<NpVersionManager> listNpVersionManager = this.npVersionManagerDao.selectNpVersionManager(TableType.MYSQL);
                    List<String> allCompany = null;
                    List<UserInNotification> duplicatList = null;

                    for(NpVersionManager npVersionManager : listNpVersionManager){
                        allCompany = this.pushNotificationDao.getAllCompanyByMarketing(npVersionManager.getTableName());

                        for(String companyItem : allCompany){
                            List<String> userIDList = this.pushNotificationDao.getAllUserByCompany(companyItem,
                                    npVersionManager.getTableName());
                            for(String userID : userIDList){
                                UserInNotification userInNotification = new UserInNotification(userID,
                                        npVersionManager.getNotificationTableName());
                                if(cacheMarketing.containsKey(companyItem)){
                                    duplicatList = cacheMarketing.get(companyItem);
                                    if(!duplicatList.contains(userInNotification)){
                                        duplicatList.add(userInNotification);
                                    }
                                }else{
                                    List<UserInNotification> l = new ArrayList<UserInNotification>();
                                    l.add(userInNotification);
                                    cacheMarketing.put(companyItem, l);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void update(){
        cacheMarketing.clear();
        init();
    }

    @Override
    public boolean containsKey(String key) {
        return this.cacheMarketing.containsKey(key);
    }

    @Override
    public int size() {
        return this.cacheMarketing.size();
    }



    public PushNotificationDao getPushNotificationDao() {
        return pushNotificationDao;
    }

    public void setPushNotificationDao(PushNotificationDao pushNotificationDao) {
        this.pushNotificationDao = pushNotificationDao;
    }

    public NpVersionManagerDao getNpVersionManagerDao() {
        return npVersionManagerDao;
    }

    public void setNpVersionManagerDao(NpVersionManagerDao npVersionManagerDao) {
        this.npVersionManagerDao = npVersionManagerDao;
    }
}
