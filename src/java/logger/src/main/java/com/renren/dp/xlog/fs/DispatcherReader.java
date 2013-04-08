package com.renren.dp.xlog.fs;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

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
  Map<String, String> options;
  private int categoryId;
  private static final Logger LOG = LoggerFactory.getLogger(DispatcherReader.class);

  public DispatcherReader(String endpoint, String[] categories, Map<String, String> options) throws IOException {
    this.endpoint = endpoint;
    this.categories = categories;
    this.options = options;
    adapter = DispatcherAdapter.getInstance();
    init();
  }

  private void init() throws IOException {
    try {
      categoryId = adapter.subscribe(endpoint, categories, options);
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
      options = null;
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
    private byte[] oneByteBuf = new byte[1];;

    private ByteBuffer buffer = ByteBuffer.allocate(0);

    @Override
    public int read() throws IOException {
      for (;;) {
        if (buffer.hasRemaining()) {
          oneByteBuf[0] = buffer.get();
          return (oneByteBuf[0] & 0xff);
        } else {
          buffer = ByteBuffer.wrap(getBytes());
        }
      }
    }
  }
}
