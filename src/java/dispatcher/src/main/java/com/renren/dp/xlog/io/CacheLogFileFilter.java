package com.renren.dp.xlog.io;

import java.io.File;
import java.io.FileFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.renren.dp.xlog.handler.FileNameHandlerFactory;

public class CacheLogFileFilter implements FileFilter{

	private long threshold;
	private SimpleDateFormat sdf=null;
	private int len;
	
	public CacheLogFileFilter(int oplogCacheTime){
		sdf=FileNameHandlerFactory.getInstance().getFileNameDataFormat();
		len=FileNameHandlerFactory.getInstance().getFileNameDataFormatLen();
		threshold=System.currentTimeMillis()-oplogCacheTime;
	} 
	
	@Override
	public boolean accept(File oplogFile) {
		if(oplogFile.isDirectory()){
			return true;
		}
		String fileName=oplogFile.getName();
		Date d=null;
		try {
			d = sdf.parse(fileName.substring(0,len));
		} catch (ParseException e) {
			return false;
		}
		long logFileNum=d.getTime();
		if(logFileNum<=threshold){
			return true;
		}
		
		return false;
	}
}
