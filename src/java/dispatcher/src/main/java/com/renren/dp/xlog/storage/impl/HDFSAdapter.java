package com.renren.dp.xlog.storage.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.dp.xlog.cache.CacheFileMeta;
import com.renren.dp.xlog.handler.FileNameHandlerFactory;
import com.renren.dp.xlog.logger.LogMeta;
import com.renren.dp.xlog.storage.StorageAdapter;

public abstract class HDFSAdapter implements StorageAdapter {

  protected FileSystem fs = null;
  protected String hdfsURI = null;
  protected String dfsReplication = null;
  protected Long socketTimeOut;

  private String currentFileNameNumber;
  private String uuid = null;
  private int bufferSize = 0;
  private final int HDFS_BATCH_COMMIT_SIZE;
  private final String RECREATED_FILE_SUFFIX=".recreated";

  private ConcurrentHashMap<String, FSDataOutputStream> categoryOfHdfsOS = new ConcurrentHashMap<String, FSDataOutputStream>();

  private static Logger logger = LoggerFactory.getLogger(HDFSAdapter.class);

  public HDFSAdapter() {
    this.uuid =com.renren.dp.xlog.config.Configuration
        .getString("xlog.uuid");;
    this.bufferSize =com.renren.dp.xlog.config.Configuration.getInt("hdfs.buffer.size", 4000);
    this.hdfsURI = com.renren.dp.xlog.config.Configuration
        .getString("storage.uri");
    this.dfsReplication = com.renren.dp.xlog.config.Configuration.getString(
        "storage.replication", "3");
    this.socketTimeOut = com.renren.dp.xlog.config.Configuration.getLong(
        "dfs.socket.timeout", 180) * 1000;
    this.HDFS_BATCH_COMMIT_SIZE = com.renren.dp.xlog.config.Configuration
        .getInt("batch.commit.size", 1000);
  }

  @Override
  public synchronized boolean store(Object o) {
    if (o instanceof LogMeta) {
      return processLogMeta((LogMeta) o);
    } else if (o instanceof CacheFileMeta) {
      return processCacheFileMeta((CacheFileMeta)o);
    } else {
      throw new UnsupportedOperationException(
          "it dosen't support this object !");
    }
  }

  private boolean processLogMeta(LogMeta logMeta) {
    FSDataOutputStream hdfsOutput = null;
    String strCategory = logMeta.getCategory();
    String logFileNum = FileNameHandlerFactory.getInstance().getHDFSLogFileNum(
        logMeta.getLogFileNum());
    hdfsOutput = buildHDFSOutputStream(strCategory, logFileNum);
    if (hdfsOutput == null) {
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
        hdfsOutput.write((logs[i] + "\n").getBytes());
      } catch (IOException e) {
        logger
            .error("fail to write data to hdfs,and get hdfs ouputstream again! the exception is : "
                + e.getMessage());
        try {
          hdfsOutput.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
        categoryOfHdfsOS.remove(strCategory);
        return false;
      }
      if (i % HDFS_BATCH_COMMIT_SIZE == 0) {
        res = flush(hdfsOutput, strCategory + "/" + logFileNum);
        if (!res) {
          return false;
        }
      }
    }
    if (i % HDFS_BATCH_COMMIT_SIZE > 0) {
      res = flush(hdfsOutput, strCategory + "/" + logFileNum);
    }
    return res;
  }

  private boolean processCacheFileMeta(CacheFileMeta cacheFileMeta) {
    FSDataOutputStream hdfsOutput = null;
    String logFileNum = FileNameHandlerFactory.getInstance().getHDFSLogFileNum(
        cacheFileMeta.getCacheFile().getName());
    hdfsOutput = buildHDFSOutputStream(cacheFileMeta.getCategories(),
        logFileNum);
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
          hdfsOutput.write((line + "\n").getBytes());
        } catch (IOException e) {
          logger
              .error("fail to write data to hdfs,and get hdfs ouputstream again! the exception is : "
                  + e.getMessage());
          try {
            hdfsOutput.close();
          } catch (IOException e1) {
            logger.error("fail to close hdfs outputstream", e1.getMessage());
          }
          return false;
        }
        count++;
        if (count % HDFS_BATCH_COMMIT_SIZE == 0) {
          res = flush(hdfsOutput, cacheFileMeta.getCategories() + "/"
              + logFileNum);
          if (!res) {
            break;
          }
        }
      }
    } catch (IOException e) {
      logger
          .error("fail to read cache file and write to hdfs! the exception is :"
              + e.getMessage());
      res = false;
    }

    if (res && count % HDFS_BATCH_COMMIT_SIZE > 0) {
      res = flush(hdfsOutput, cacheFileMeta.getCategories() + "/" + logFileNum);
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

  private FSDataOutputStream buildHDFSOutputStream(String strCategory,
      String logFileNum) {
    FSDataOutputStream hdfsOutput = null;
    if (categoryOfHdfsOS.containsKey(strCategory)) {
      hdfsOutput = categoryOfHdfsOS.get(strCategory);
      if (!logFileNum.equals(currentFileNameNumber)) {
        flush(hdfsOutput, strCategory + "/" + logFileNum);
        try {
          hdfsOutput.close();
        } catch (IOException e) {
          logger.error("fail to close hdfs outputstream", e.getMessage());
        }
        hdfsOutput = getHDFSOutputStream(strCategory + "/" + logFileNum);
        if (hdfsOutput == null) {
          logger
              .error("fail to get HDFSOutputStream,it can't store logdata to hdfs!");
          return null;
        } else {
          logger.debug("success to get HDFSOutputStream!");
        }
        currentFileNameNumber = logFileNum;
        categoryOfHdfsOS.put(strCategory, hdfsOutput);
      }
    } else {
      hdfsOutput = getHDFSOutputStream(strCategory + "/" + logFileNum);
      if (hdfsOutput == null) {
        logger
            .error("fail to get HDFSOutputStream,it can't store logdata to hdfs!");
        return null;
      } else {
        logger.debug("success to get HDFSOutputStream!");
      }
      currentFileNameNumber = logFileNum;
      categoryOfHdfsOS.put(strCategory, hdfsOutput);
    }

    return hdfsOutput;
  }

  public abstract boolean flush(FSDataOutputStream hdfsOutput, String path);

  protected FSDataOutputStream getHDFSOutputStream(String path) {
    Path p = new Path(path + "." + uuid);
    try {
      /***
       * 异常情况下重建文件一次，防止分布式锁的租期未到无法释放租期导致重复创建文件失败。
       * 1.首先判断目标文件是否存在，如果不存在直接create
       * 2.如果存在判断二次重建的那个文件是否存在，如果不存在那么就可以创建一个重建文件
       * 3.如果重建文件存在，那么直接append原来的目标文件
       */
      if (fs.exists(p)) {
        Path recreatedPath=new Path(path + "." + uuid+RECREATED_FILE_SUFFIX);
        if(fs.exists(recreatedPath)){
          return fs.append(p);
        }else{
          return fs.create(recreatedPath,false, bufferSize);
        }
        
      } else {
        return fs.create(p,false, bufferSize);
      }
    } catch (Exception e) {
      logger
          .error("fail to create HDFSOutputstream,then reinitialize hdfs and recreate!the exception is "
              + e.getMessage());
    }
    try {
      Thread.sleep(500);
    } catch (InterruptedException e1) {}
    try {
      initialize();
    } catch (IOException e) {
      logger.error("fail to reinitialize hdfs,the exception is "
          + e.getMessage());
      return null;
    }
    try {
      if (fs.exists(p)) {
        return fs.append(p);
      } else {
        return fs.create(p,false, bufferSize);
      }
    } catch (IOException e) {
      logger.error("fail to recreate HDFSOutputstream,the exception is ",
          e.getMessage());
      return null;
    }
  }

  @Override
  public void destory() {
    Collection<FSDataOutputStream> c = categoryOfHdfsOS.values();
    for (FSDataOutputStream o : c) {
      try {
        o.close();
      } catch (IOException e) {
        e.printStackTrace();
        continue;
      }
    }
    categoryOfHdfsOS.clear();
    categoryOfHdfsOS = null;

    try {
      fs.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
