package com.renren.dp.xlog.storage;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.renren.dp.xlog.util.Constants;

public abstract class StorageRepositoryInitialization {

  protected String cacheLogRootDir;
  protected String slaveLogRootDir;

  private final static Logger LOG = LoggerFactory.getLogger(StorageRepositoryInitialization.class);
  
  public abstract void initialise();
  
  protected void check(File logFileDir,FileFilter fileFilter) {
    File[] logFiles = logFileDir.listFiles(fileFilter);
    if (logFiles == null || logFiles.length == 0) {
      return;
    }
    for (File logFile : logFiles) {
      if (logFile.isFile()) {
        if (!logFile.getName().contains(Constants.LOG_WRITE_ERROR_SUFFIX)) {
          Configuration conf=new Configuration();
          String logFileStr=logFile.getAbsolutePath();
          String cacheLogStr=cacheLogRootDir+"/"+logFileStr.substring(slaveLogRootDir.length());
           /**
           * 将错误日志文件重命名为要删除的文件，因为下面要从缓存目录中读写对应的文件替换
           */
          logFile.renameTo(new File(logFileStr+Constants.LOG_WRITE_ERROR_TMP+Constants.LOG_SYNC_FINISHED_SUFFIX));
          Path srcPath=new Path(cacheLogStr);
          FileSystem fs=null;
          try {
            fs = FileSystem.getLocal(conf);
          } catch (IOException e) {
            LOG.error("Fail to get Local FileSystem!",e);
            continue;
           }
          try {
            if(!fs.exists(srcPath)){
              continue;
              }
          } catch (IOException e) {
            LOG.error("Fail to judge file exsit : "+cacheLogStr,e);
            continue;
           }
          try {
            FileUtil.copy(fs,srcPath , fs, new Path(logFileStr+Constants.LOG_WRITE_ERROR_SUFFIX), false, conf);
          } catch (IOException e) {
            LOG.error("Fail to copy cache log :"+cacheLogStr+ " to slave log category.",e);
            continue;
          }
        }
      } else {
        check(logFile,fileFilter);
      }
    }
  }

}
