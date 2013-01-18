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
import com.renren.dp.xlog.util.Constants;
import com.renren.dp.xlog.util.LogDataFormat;

public abstract class HDFSAdapter implements StorageAdapter {

  protected FileSystem fs = null;
  protected String hdfsURI = null;
  protected String dfsReplication = null;
  protected Long socketTimeOut;

  private String currentFileNameNumber;
  private String uuid = null;
  private int bufferSize = 0;

  private ConcurrentHashMap<String, FSDataOutputStream> categoryOfHdfsOS = new ConcurrentHashMap<String, FSDataOutputStream>();

  private static Logger logger = LoggerFactory.getLogger(HDFSAdapter.class);

  public HDFSAdapter(String uuid, int bufferSize) {
    this.uuid = uuid;
    this.bufferSize = bufferSize;
    this.hdfsURI = com.renren.dp.xlog.config.Configuration.getString("storage.uri");
    this.dfsReplication = com.renren.dp.xlog.config.Configuration.getString("storage.replication", "3");
    this.socketTimeOut = com.renren.dp.xlog.config.Configuration.getLong("dfs.socket.timeout", 180) * 1000;
  }

  @Override
  public boolean store(Object o) {
    FSDataOutputStream hdfsOutput = null;
    if (o instanceof LogMeta) {
      LogMeta logMeta = (LogMeta) o;
      String strCategory = LogDataFormat.transformCategories(logMeta.getLogData().categories);
      String logFileNum = FileNameHandlerFactory.getInstance().getHDFSLogFileNum(logMeta.getLogFileNum());
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
          logger.error("fail to write data to hdfs,and get hdfs ouputstream again! the exception is : "
              + e.getMessage());
          try {
            hdfsOutput.close();
          } catch (IOException e1) {
            logger.error("fail to close hdfs outputstream", e1.getMessage());
          }
          hdfsOutput = getHDFSOutputStream(strCategory + "/" + logFileNum);

          try {
            if (hdfsOutput != null) {
              hdfsOutput.write((logs[i] + "\n").getBytes());
              logger.info("success to rewrite data to hdfs! ");
            } else {
              logger.error("fail to rewrite data to hdfs,because it does not get hdfs outputstream!");
              return false;
            }
          } catch (IOException e1) {
            logger.error("fail to rewrite data to hdfs! the exception is : " + e.getMessage());
            return false;
          }
        }
        if (i == Constants.HDFS_BATCH_COMMIT_SIZE) {
          res = flush(hdfsOutput, strCategory + "/" + logFileNum);
          if (!res) {
            return false;
          }
        }
      }
      if (i > 0) {
        res = flush(hdfsOutput, strCategory + "/" + logFileNum);
      }
      return res;
    } else if (o instanceof CacheFileMeta) {
      CacheFileMeta cacheFileMeta = (CacheFileMeta) o;
      String logFileNum = FileNameHandlerFactory.getInstance()
          .getHDFSLogFileNum(cacheFileMeta.getCacheFile().getName());
      hdfsOutput = buildHDFSOutputStream(cacheFileMeta.getCategories(), logFileNum);
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
            logger.error("fail to write data to hdfs,and get hdfs ouputstream again! the exception is : "
                + e.getMessage());
            try {
              hdfsOutput.close();
            } catch (IOException e1) {
              logger.error("fail to close hdfs outputstream", e1.getMessage());
            }
            hdfsOutput = getHDFSOutputStream(cacheFileMeta.getCategories() + "/" + logFileNum);
            if (hdfsOutput != null) {
              hdfsOutput.write((line + "\n").getBytes());
              logger.info("success to rewrite data to hdfs !");
            } else {
              logger.error("fail to rewrite data to hdfs !");
              res = false;
              break;
            }
          }
          count++;
          if (count == Constants.HDFS_BATCH_COMMIT_SIZE) {
            count = 0;
            res = flush(hdfsOutput, cacheFileMeta.getCategories() + "/" + logFileNum);
            if (!res) {
              break;
            }
          }
        }
      } catch (IOException e) {
        logger.error("fail to read cache file and wirte to hdfs! the exception is :" + e.getMessage());
        res = false;
      }

      if (res && count > 0) {
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
    } else {
      throw new UnsupportedOperationException("it dosen't support this object !");
    }
  }

  private FSDataOutputStream buildHDFSOutputStream(String strCategory, String logFileNum) {
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
          logger.error("fail to get HDFSOutputStream,it can't store logdata to hdfs!");
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
        logger.error("fail to get HDFSOutputStream,it can't store logdata to hdfs!");
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
      // 判断文件是否存在，因为有可能别的节点已经创建了该文件
      if (fs.exists(p)) {
        return fs.append(p, bufferSize);
      } else {
        return fs.create(p, false, bufferSize);
      }
    } catch (Exception e) {
      logger.error("fail to create HDFSOutputstream,then reinitialize hdfs and recreate!the exception is "
          + e.getMessage());
    }
    try {
      initialize();
    } catch (IOException e) {
      logger.error("fail to reinitialize hdfs,the exception is " + e.getMessage());
      return null;
    }
    try {
      if (fs.exists(p)) {
        return fs.append(p);
      } else {
        return fs.create(p);
      }
    } catch (IOException e) {
      logger.error("fail to recreate HDFSOutputstream,the exception is ", e.getMessage());
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
