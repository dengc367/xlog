// **********************************************************************
//
// Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.3.1

package xlog.slice;

public interface LoggerPrx extends Ice.ObjectPrx
{
    public void add(LogData[] data);
    public void add(LogData[] data, java.util.Map<String, String> __ctx);

    public void addLogData(LogData data);
    public void addLogData(LogData data, java.util.Map<String, String> __ctx);
}
