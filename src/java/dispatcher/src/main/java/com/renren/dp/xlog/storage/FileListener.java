package com.renren.dp.xlog.storage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.dp.xlog.cache.CacheFileMeta;
import com.renren.dp.xlog.cache.CacheManager;
import com.renren.dp.xlog.config.Configuration;
import com.renren.dp.xlog.exception.ReflectionException;
import com.renren.dp.xlog.io.SuffixFileFilter;
import com.renren.dp.xlog.util.Constants;
import com.renren.dp.xlog.util.ReflectionUtil;

public class FileListener extends Thread {

  private String categories = null;

  private static Logger LOG = LoggerFactory.getLogger(FileListener.class);
  
  public FileListener(String categories) {
    this.categories = categories;
  }

  public void run() {
    File cacheFileDir = new File(Configuration.getString("oplog.store.path")
        + "/" + CacheManager.CACHE_TYPE + "/" + categories);
    File[] files = null;
    SuffixFileFilter filter = new SuffixFileFilter(
        new String[] { Constants.LOG_WRITE_FINISHED_SUFFIX });
    StorageAdapter sa = null;
    try {
      sa = (StorageAdapter)ReflectionUtil.newInstance(Configuration.getString("storage.adapter.impl"));
    } catch (ReflectionException e) {
      LOG.error("Fail to reflection StorageAdapter instance",e);
      return ;
    }
    try {
      sa.initialize();
    } catch (IOException e1) {
      e1.printStackTrace();
    }

    while (true) {
      files = cacheFileDir.listFiles(filter);
      if (files.length == 0) {
        try {
          Thread.sleep(60000);
        } catch (InterruptedException e) {
        }
      } else {
        Arrays.sort(files);
        for (File file : files) {
          try {
            boolean res = sa.store(new CacheFileMeta(categories, file));
            if (res) {
              file.renameTo(new File(file.getAbsolutePath()
                  + Constants.LOG_SYNC_FINISHED_SUFFIX));
            }
          } catch (IOException e) {
            e.printStackTrace();
            continue;
          }
        }
      }

    }
  }
}
