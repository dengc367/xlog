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

public interface _AgentOperations
{
    void add(LogData[] data, Ice.Current __current);

    void addFailedLogData(LogData[] data, Ice.Current __current);

    String[] subscribeClient(String prxStr, Ice.Current __current);

    String[] subscribeSubscriber(String[] categories, String prxStr, Ice.Current __current);
}
