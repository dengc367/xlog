package com.renren.dp.xlog.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.AlreadyBeingCreatedException;
import org.apache.hadoop.hdfs.server.namenode.LeaseExpiredException;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.util.ReflectionUtils;

/**
 * Implementation for hdfs
 * <p>
 * copied from HBase 0.94.1
 * 
 * @see org.apache.hadoop.hbase.util.FSUtils
 */
public class FSUtils {
  private static final Log LOG = LogFactory.getLog(FSUtils.class);

  /**
   * Lease timeout constant, sourced from HDFS upstream. The upstream constant
   * is defined in a private interface, so we can't reuse for compatibility
   * reasons. NOTE: On versions earlier than Hadoop 0.23, the constant is in
   * o.a.h.hdfs.protocol.FSConstants, while for 0.23 and above it is in
   * o.a.h.hdfs.protocol.HdfsConstants cause of HDFS-1620.
   */
  public static final long LEASE_SOFTLIMIT_PERIOD = 60 * 1000;
  
  public static FSUtils getInstance(FileSystem fs, Configuration conf) {
    String scheme = fs.getUri().getScheme();
    if (scheme == null) {
      LOG.warn("Could not find scheme for uri " + 
          fs.getUri() + ", default to hdfs");
      scheme = "hdfs";
    }
    Class<?> fsUtilsClass = conf.getClass("hbase.fsutil." +
        scheme + ".impl", FSUtils.class); // Default to HDFS impl
    FSUtils fsUtils = (FSUtils)ReflectionUtils.newInstance(fsUtilsClass, conf);
    return fsUtils;
  }

  public void recoverFileLease(final FileSystem fs, final Path p, Configuration conf) throws IOException {
    if (!isAppendSupported(conf)) {
      LOG.warn("Running on HDFS without append enabled may result in data loss");
      return;
    }
    // lease recovery not needed for local file system case.
    // currently, local file system doesn't implement append either.
    if (!(fs instanceof DistributedFileSystem)) {
      return;
    }
    LOG.info("Recovering file " + p);
    long startWaiting = System.currentTimeMillis();

    // Trying recovery
    boolean recovered = false;
    while (!recovered) {
      try {
        try {
          if (fs instanceof DistributedFileSystem) {
            DistributedFileSystem dfs = (DistributedFileSystem) fs;
            DistributedFileSystem.class.getMethod("recoverLease", new Class[] { Path.class }).invoke(dfs, p);
          } else {
            throw new Exception("Not a DistributedFileSystem");
          }
        } catch (InvocationTargetException ite) {
          // function was properly called, but threw it's own exception
          throw (IOException) ite.getCause();
        } catch (Exception e) {
          LOG.debug("Failed fs.recoverLease invocation, " + e.toString() + ", trying fs.append instead");
          FSDataOutputStream out = fs.append(p);
          out.close();
        }
        recovered = true;
      } catch (IOException e) {
        e = RemoteExceptionHandler.checkIOException(e);
        if (e instanceof AlreadyBeingCreatedException) {
          // We expect that we'll get this message while the lease is still
          // within its soft limit, but if we get it past that, it means
          // that the RS is holding onto the file even though it lost its
          // znode. We could potentially abort after some time here.
          long waitedFor = System.currentTimeMillis() - startWaiting;
          if (waitedFor > LEASE_SOFTLIMIT_PERIOD) {
            LOG.warn("Waited " + waitedFor + "ms for lease recovery on " + p + ":" + e.getMessage());
          }
        } else if (e instanceof LeaseExpiredException && e.getMessage().contains("File does not exist")) {
          // This exception comes out instead of FNFE, fix it
          throw new FileNotFoundException("The given HLog wasn't found at " + p.toString());
        } else {
          throw new IOException("Failed to open " + p + " for append", e);
        }
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException ex) {
        InterruptedIOException iioe = new InterruptedIOException();
        iioe.initCause(ex);
        throw iioe;
      }
    }
    LOG.info("Finished lease recover attempt for " + p);
  }

  /**
   * Heuristic to determine whether is safe or not to open a file for append
   * Looks both for dfs.support.append and use reflection to search for
   * SequenceFile.Writer.syncFs() or FSDataOutputStream.hflush()
   * 
   * @param conf
   * @return True if append support
   */
  public static boolean isAppendSupported(final Configuration conf) {
    boolean append = conf.getBoolean("dfs.support.append", false);
    if (append) {
      try {
        // TODO: The implementation that comes back when we do a createWriter
        // may not be using SequenceFile so the below is not a definitive test.
        // Will do for now (hdfs-200).
        SequenceFile.Writer.class.getMethod("syncFs", new Class<?>[] {});
        append = true;
      } catch (SecurityException e) {
      } catch (NoSuchMethodException e) {
        append = false;
      }
    }
    if (!append) {
      // Look for the 0.21, 0.22, new-style append evidence.
      try {
        FSDataOutputStream.class.getMethod("hflush", new Class<?>[] {});
        append = true;
      } catch (NoSuchMethodException e) {
        append = false;
      }
    }
    return append;
  }
}