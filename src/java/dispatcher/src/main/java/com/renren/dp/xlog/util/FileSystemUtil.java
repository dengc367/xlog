package com.renren.dp.xlog.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

public class FileSystemUtil {
  public static FileSystem createFileSystem() throws IOException {
    Configuration conf = new Configuration();
    conf.set("dfs.replication", com.renren.dp.xlog.config.Configuration.getString("storage.replication", "3"));
    conf.setLong("dfs.socket.timeout",
        com.renren.dp.xlog.config.Configuration.getLong("dfs.socket.timeout", 180) * 1000);
    conf.set("mapred.task.id", "xlog_dispatcher_" + com.renren.dp.xlog.config.Configuration.getString("xlog.uuid"));
    conf.setBoolean("dfs.support.append", true);
    conf.set("fs.hdfs.impl", conf.get("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem"));
    conf.set("fs.default.name", com.renren.dp.xlog.config.Configuration.getString("storage.uri"));
    conf.setBoolean("fs.hdfs.impl.disable.cache", true);
    return createFileSystem(conf);
  }

  public static FileSystem createFileSystem(Configuration conf) throws IOException {
    try {
      Method method = FileSystem.class.getDeclaredMethod("get", Configuration.class);
      method.setAccessible(true);
      return (FileSystem) method.invoke(null, conf);
    } catch (SecurityException e) {
      throw new IOException(e);
    } catch (NoSuchMethodException e) {
      throw new IOException(e);
    } catch (IllegalArgumentException e) {
      throw new IOException(e);
    } catch (IllegalAccessException e) {
      throw new IOException(e);
    } catch (InvocationTargetException e) {
      throw new IOException(e);
    }

  }
}
