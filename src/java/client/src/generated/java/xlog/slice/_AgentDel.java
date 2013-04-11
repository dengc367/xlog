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

public interface _AgentDel extends Ice._ObjectDel
{
    void add(LogData[] data, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void addFailedLogData(LogData[] data, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String[] subscribeClient(String prxStr, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String[] subscribeSubscriber(String[] categories, String prxStr, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;
}
