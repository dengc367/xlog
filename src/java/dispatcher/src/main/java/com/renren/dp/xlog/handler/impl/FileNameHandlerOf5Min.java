package com.renren.dp.xlog.handler.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.renren.dp.xlog.handler.AbstractFileNameHandler;
import com.renren.dp.xlog.util.Constants;

public class FileNameHandlerOf5Min extends AbstractFileNameHandler {

  public FileNameHandlerOf5Min() {
    threadLocal = new ThreadLocal<SimpleDateFormat>() {
      protected synchronized SimpleDateFormat initialValue() {
        return new SimpleDateFormat(Constants.FILE_NAME_FORMAT_MIN);
      }
    };
  }

  public String getCacheLogFileNum() {
    String strDate = getDateFormat().format(new Date());
    int min = Integer.parseInt(strDate.substring(15, 16));
    if (min >= 5) {
      return strDate.substring(0, 15) + "5";
    } else {
      return strDate.substring(0, 15) + "0";
    }
  }

  @Override
  public int getFileNameDataFormatLen() {
    // TODO Auto-generated method stub
    return Constants.FILE_NAME_FORMAT_MIN.length();
  }

  @Override
  public String getCacheLogFileNum(Date date) {
    String strDate = getDateFormat().format(date);
    int min = Integer.parseInt(strDate.substring(15, 16));
    if (min >= 5) {
      return strDate.substring(0, 15) + "5";
    } else {
      return strDate.substring(0, 15) + "0";
    }
  }

  @Override
  public String NextLogFileNum(String currentFileNum) throws ParseException {
    Calendar currentCalendar = Calendar.getInstance();
    Date time = getFileNameDataFormat().parse(currentFileNum);
    currentCalendar.setTime(time);
    currentCalendar.add(Calendar.MINUTE, 5);
    return getCacheLogFileNum(currentCalendar.getTime());
  }
}
