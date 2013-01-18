package com.renren.dp.xlog.cache;

import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.exception.ReflectionException;
import com.renren.dp.xlog.util.ReflectionUtil;

public class CacheManagerFactory {

  private static CacheManager cm = null;

  public static CacheManager getInstance() throws ReflectionException {
    if (cm == null) {
      synchronized (CacheManagerFactory.class) {
        if (cm == null) {
          cm =(CacheManager)ReflectionUtil.newInstance(Configuration.getString("cache.manager.impl"));
        }
      }
    }
    return cm;
  }
}
