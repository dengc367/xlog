package com.renren.dp.xlog.handler.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.renren.dp.xlog.handler.AbstractFileNameHandler;
import com.renren.dp.xlog.util.Constants;

public class FileNameHandlerOf10Min extends AbstractFileNameHandler {

  public FileNameHandlerOf10Min() {
    threadLocal = new ThreadLocal<SimpleDateFormat>() {
      protected synchronized SimpleDateFormat initialValue() {
        return new SimpleDateFormat(Constants.FILE_NAME_FORMAT_MIN);
      }
    };
  }

  public String getCacheLogFileNum() {
    return getDateFormat().format(new Date()).substring(0, 15) + "0";
  }

  @Override
  public int getFileNameDataFormatLen() {
    // TODO Auto-generated method stub
    return Constants.FILE_NAME_FORMAT_MIN.length();
  }

  @Override
  public String getCacheLogFileNum(Date date) {
    return getDateFormat().format(date).substring(0, 15) + "0";
  }

  @Override
  public String NextLogFileNum(String currentFileNum) throws ParseException {
    Calendar currentCalendar = Calendar.getInstance();
    Date time = getFileNameDataFormat().parse(currentFileNum);
    currentCalendar.setTime(time);
    currentCalendar.add(Calendar.MINUTE, 10);
    return getCacheLogFileNum(currentCalendar.getTime());
  }
}
