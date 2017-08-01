package com.haizhi.np.dispatch.cache;

import com.haizhi.np.dispatch.bean.UserInNotification;

import java.util.List;
import java.util.Map;

/**
 * Created by haizhi on 2017/7/25.
 */
public interface Cache {

    public Map getCache();

    public void init();

    public void update();

    public boolean containsKey(String key);

    public int size();

}
