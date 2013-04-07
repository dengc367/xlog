package com.renren.dp.xlog.storage.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.dp.xlog.cache.CacheFileMeta;
import com.renren.dp.xlog.handler.FileNameHandlerFactory;
import com.renren.dp.xlog.logger.LogMeta;
import com.renren.dp.xlog.storage.RecoverableOutputStream;
import com.renren.dp.xlog.storage.StorageAdapter;
import com.renren.dp.xlog.util.FileSystemUtil;

public class HDFSAdapter implements StorageAdapter {

  protected FileSystem fs;

  private String uuid = null;
  private final int HDFS_BATCH_COMMIT_SIZE;

  private Map<String, String> currentFileNumberMap = new ConcurrentHashMap<String, String>();
  private ConcurrentHashMap<String, RecoverableOutputStream> outputStreamMap = new ConcurrentHashMap<String, RecoverableOutputStream>();

  protected static Logger LOG = LoggerFactory.getLogger(HDFSAdapter.class);

  public HDFSAdapter() {
    this.uuid = com.renren.dp.xlog.config.Configuration.getString("xlog.uuid");
    this.HDFS_BATCH_COMMIT_SIZE = com.renren.dp.xlog.config.Configuration.getInt("batch.commit.size", 1000);
  }

  @Override
  public synchronized boolean store(Object o) {
    if (o instanceof LogMeta) {
      return processLogMeta((LogMeta) o);
    } else if (o instanceof CacheFileMeta) {
      return processCacheFileMeta((CacheFileMeta) o);
    } else {
      throw new UnsupportedOperationException("it dosen't support this object !");
    }
  }

  private boolean processLogMeta(LogMeta logMeta) {
    RecoverableOutputStream categoryOutputStream = null;
    String strCategory = logMeta.getCategory();
    String logFileNum = FileNameHandlerFactory.getInstance().getHDFSLogFileNum(logMeta.getLogFileNum());
    categoryOutputStream = buildHDFSOutputStream(strCategory, logFileNum);
    if (categoryOutputStream == null) {
      return false;
    }
    int i = 0;
    String[] logs = logMeta.getLogData().logs;
    int len = logs == null ? 0 : logs.length;
    boolean res = true;
    for (i = 0; i < len; i++) {
      if (logs[i] == null) {
        continue;
      }
      try {
        categoryOutputStream.write((logs[i] + "\n").getBytes());
      } catch (IOException e) {
        LOG.error("Fail to write data to hdfs,and get hdfs ouputstream again! the exception is : " + e.getMessage());
        closeOutputStream(categoryOutputStream, strCategory);
        return false;
      }
      if (i % HDFS_BATCH_COMMIT_SIZE == 0) {
        try {
          categoryOutputStream.flush();
        } catch (IOException e) {
          return false;
        }
      }
    }
    if (i % HDFS_BATCH_COMMIT_SIZE > 0) {
      try {
        categoryOutputStream.flush();
      } catch (IOException e) {
        res = false;
      }
    }
    return res;
  }

  private void closeOutputStream(RecoverableOutputStream categoryOutputStream, String strCategory) {
    try {
      categoryOutputStream.close();
    } catch (IOException e) {
    } finally {
      categoryOutputStream = null;
    }
    outputStreamMap.remove(strCategory);
  }

  private boolean processCacheFileMeta(CacheFileMeta cacheFileMeta) {
    String logFileNum = FileNameHandlerFactory.getInstance().getHDFSLogFileNum(cacheFileMeta.getCacheFile().getName());
    RecoverableOutputStream categoryOutputStream = buildHDFSOutputStream(cacheFileMeta.getCategories(), logFileNum);
    FileReader fr = null;
    try {
      fr = new FileReader(cacheFileMeta.getCacheFile());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    BufferedReader br = new BufferedReader(fr);
    String line = null;
    int count = 0;
    boolean res = true;
    try {
      while ((line = br.readLine()) != null) {
        try {
          categoryOutputStream.write((line + "\n").getBytes());
        } catch (IOException e) {
          LOG.error("Fail to write data to hdfs,and get hdfs ouputstream again! the exception is : " + e.getMessage());
          closeOutputStream(categoryOutputStream, cacheFileMeta.getCategories());
          return false;
        }
        count++;
        if (count % HDFS_BATCH_COMMIT_SIZE == 0) {
          categoryOutputStream.flush();
        }
      }
    } catch (IOException e) {
      LOG.warn("Fail to read cache file and write to hdfs! the exception is :", e.getMessage());
      res = false;
    }

    if (res && count % HDFS_BATCH_COMMIT_SIZE > 0) {
      try {
        categoryOutputStream.flush();
      } catch (IOException e) {
        LOG.error("Fail to flush logdata! ", e.getMessage());
        res = false;
      }
    }

    try {
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      fr.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return res;
  }

  private RecoverableOutputStream buildHDFSOutputStream(String strCategory, String logFileNum) {
    RecoverableOutputStream os = outputStreamMap.get(strCategory);
    if (os != null && logFileNum.equals(currentFileNumberMap.get(strCategory))) {
      return os;
    } else if (os != null) {
      try {
        os.flush();
      } catch (Exception e) {
        LOG.error("Flush error with other exception:" + e);
      } finally {
        try {
          os.close();
        } catch (IOException e) {
          LOG.error("Fail to close hdfs outputstream", e.getMessage());
        }
      }
    }
    Path categoryPath = new Path(fs.getWorkingDirectory(), strCategory + "/" + logFileNum + "." + uuid);
    os = getHDFSOutputStream(categoryPath);
    if (os != null) {
      currentFileNumberMap.put(strCategory, logFileNum);
      outputStreamMap.put(strCategory, os);
      LOG.info("Success to get HDFSOutputStream for category " + strCategory + ": " + os.toString());
    }
    return os;
  }

  protected RecoverableOutputStream getHDFSOutputStream(Path path) {
    RecoverableOutputStream os;
    try {
      os = new RecoverableOutputStream(fs, path);
    } catch (Exception e) {
      LOG.error("Fail to get HDFSOutputStream,it can't store logdata to hdfs!", e);
      return null;
    }
    return os;
  }

  @Override
  public void initialize() throws IOException {
    try {
      fs = FileSystemUtil.createFileSystem();
    } catch (IOException e) {
      LOG.error("FileSystem create Failed. ", e);
    }
  }

  @Override
  public synchronized void destory() {
    LOG.info("Close all hdfs clients." + outputStreamMap);
    Collection<RecoverableOutputStream> c = outputStreamMap.values();
    for (RecoverableOutputStream o : c) {
      try {
        o.close();
      } catch (IOException e) {
        e.printStackTrace();
        continue;
      }
    }
    outputStreamMap.clear();
    currentFileNumberMap.clear();
    try {
      fs.close();
      LOG.info("Close FileSystem success. " + fs);
    } catch (IOException e) {
      LOG.error("Close FileSystem error. " + e);
    } finally {
      fs = null;
    }
  }
}
