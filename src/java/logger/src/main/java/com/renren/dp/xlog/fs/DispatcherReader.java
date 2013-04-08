package com.renren.dp.xlog.fs;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xlog.slice.ErrorCode;
import xlog.slice.XLogException;

/**
 * one dispatcher vs one reader
 * 
 * @author Zhancheng Deng {@mailto: zhancheng.deng@renren-inc.com}
 * @since 4:27:41 PM Apr 7, 2013
 */
public class DispatcherReader implements Closeable {

  private String endpoint;
  private String[] categories;
  DispatcherAdapter adapter;
  BufferedReader reader;
  XLogConfiguration conf;
  private int categoryId;
  private static final Logger LOG = LoggerFactory.getLogger(DispatcherReader.class);

  public DispatcherReader(String endpoint, String[] categories, XLogConfiguration conf) throws IOException {
    this.endpoint = endpoint;
    this.categories = categories;
    this.conf = conf;
    adapter = DispatcherAdapter.getInstance();
    init();
  }

  private void init() throws IOException {
    try {
      categoryId = adapter.subscribe(endpoint, categories, conf.map());
      LOG.debug("successfully init the categories " + Arrays.toString(categories) + " with the endpoint " + endpoint
          + ".");
    } catch (XLogException e) {
      throw new IOException("the xlog exception. ", e);
    }
    reader = new BufferedReader(new InputStreamReader(new DispatcherInputStream()));
  }

  public String readLine() throws IOException {
    return reader.readLine();
  }

  @Override
  public void close() throws IOException {
    try {
      adapter.unsubscribe(endpoint, categories, null);
      reader.close();
      conf = null;
      LOG.debug("successfully close the categories " + Arrays.toString(categories) + " with the endpoint " + endpoint
          + ".");
    } catch (XLogException e) {
      e.printStackTrace();
    }
  }

  private byte[] getBytes() throws IOException {
    try {
      return adapter.getBytes(endpoint, categoryId);
    } catch (XLogException e) {
      if (e.code == ErrorCode.NoSubscription) {
        init();
      } else {
        throw new IOException("the xlog exception. ", e);
      }
    }
    return (new byte[0]);
  }

  @Override
  public String toString() {
    return "[DispatcherReader] endpoint: " + reader + ", categories: " + Arrays.toString(categories);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof DispatcherReader) {
      DispatcherReader reader = (DispatcherReader) obj;
      return Arrays.equals(categories, reader.categories) && endpoint.equals(reader.endpoint);
    }
    return super.equals(obj);
  }

  public String getEndpoint() {
    return endpoint;
  }

  private class DispatcherInputStream extends InputStream {

    private ByteBuffer buffer;

    public DispatcherInputStream() {
      int capacity = NumberUtils.toInt(conf.getFetchSize(), 1048576) + 10;
      buffer = ByteBuffer.allocate(capacity);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
      if (b == null) {
        throw new NullPointerException();
      } else if (off < 0 || len < 0 || len > b.length - off) {
        throw new IndexOutOfBoundsException();
      } else if (len == 0) {
        return 0;
      }

      int size = -1;
      if (buffer.hasRemaining()) {
        if (buffer.remaining() < len) {
          size = buffer.remaining();
        } else {
          size = len;
        }
        buffer.get(b, off, size);
      }
      if (!buffer.hasRemaining()) {
        byte[] tmp = getBytes();
        if (tmp == null || tmp.length == 0) {
          return size;
        }
        buffer.clear();
        buffer.put(tmp);
        buffer.flip();
        size = (size == -1) ? 0 : size;
        if (len - size > 0) {
          int remain = len - size;
          if (remain - buffer.remaining() > 0) {
            remain = buffer.remaining();
          }
          buffer.get(b, off + size, remain);
          return size + remain;
        }
      }
      return size;
    }

    @Override
    public int read() throws IOException {
      if (buffer.hasRemaining()) {
        return (buffer.get() & 0xff);
      }
      byte[] tmp = getBytes();
      if (tmp == null || tmp.length == 0) {
        return -1;
      }
      buffer.clear();
      buffer.put(tmp);
      buffer.flip();
      return (buffer.get() & 0xff);
    }
  }
}
