package com.renren.dp.xlog.handler.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.renren.dp.xlog.handler.AbstractFileNameHandler;
import com.renren.dp.xlog.util.Constants;

public class FileNameHandlerOfHour extends AbstractFileNameHandler {

  public FileNameHandlerOfHour() {
    threadLocal = new ThreadLocal<SimpleDateFormat>() {
      protected synchronized SimpleDateFormat initialValue() {
        return new SimpleDateFormat(Constants.FILE_NAME_FORMAT_HOUR);
      }
    };
  }

  @Override
  public String getCacheLogFileNum() {
    return getDateFormat().format(new Date());
  }

  @Override
  public int getFileNameDataFormatLen() {
    return Constants.FILE_NAME_FORMAT_HOUR.length();
  }

  @Override
  public String getCacheLogFileNum(Date date) {
    return getDateFormat().format(date);
  }

  @Override
  public String NextLogFileNum(String currentFileNum) throws ParseException {
    Calendar currentCalendar = Calendar.getInstance();
    Date time = getFileNameDataFormat().parse(currentFileNum);
    currentCalendar.setTime(time);
    currentCalendar.add(Calendar.HOUR_OF_DAY, 1);
    return getCacheLogFileNum(currentCalendar.getTime());
  }
}
