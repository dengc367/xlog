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

public interface AgentPrx extends Ice.ObjectPrx
{
    public void add(LogData[] data);
    public void add(LogData[] data, java.util.Map<String, String> __ctx);

    public void addFailedLogData(LogData[] data);
    public void addFailedLogData(LogData[] data, java.util.Map<String, String> __ctx);

    public String[] subscribeClient(String prxStr);
    public String[] subscribeClient(String prxStr, java.util.Map<String, String> __ctx);

    public String[] subscribeSubscriber(String[] categories, String prxStr);
    public String[] subscribeSubscriber(String[] categories, String prxStr, java.util.Map<String, String> __ctx);
}
