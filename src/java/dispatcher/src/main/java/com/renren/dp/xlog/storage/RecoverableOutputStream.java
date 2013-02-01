package com.renren.dp.xlog.storage;

import java.io.IOException;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

public class RecoverableOutputStream implements Closeable {
  private static Logger LOG = LoggerFactory.getLogger(RecoverableOutputStream.class);

  private Path path;
  private FileSystem fs;
  private FSDataOutputStream os;

  public RecoverableOutputStream(FileSystem fs, Path p) throws IOException {
    this.fs = fs;
    this.path = p;
    init();
  }

  private void init() throws IOException {
    if (fs.exists(path)) {
      recoverLease();
      os = fs.append(path);
    } else {
      os = fs.create(path);
    }
  }

  private void recoverLease() throws IOException {
    FSUtils.getInstance(fs, fs.getConf()).recoverFileLease(fs, path, fs.getConf());
  }

  @Override
  public void close() throws IOException {
    if (os != null) {
      try {
        os.close();
        LOG.info("close os success: " + os);
      } catch (IOException e) {
        LOG.error("fail to close hdfs DFSOutputStream of the " + path + ". the Exception is " + e.getMessage());
      } finally {
        os = null;
      }
    }
  }

  public void flush() throws IOException {
    os.flush();
  }

  public void write(byte[] bytes) throws IOException {
    os.write(bytes);
  }

}
