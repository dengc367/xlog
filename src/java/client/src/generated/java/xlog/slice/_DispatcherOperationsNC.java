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

public interface _DispatcherOperationsNC
{
    void add(LogData[] data);

    void addLogData(LogData data);

    void createZNode(int slot);

    boolean register(LoggerPrx subscriber, int frequence);

    int subscribe(Subscription sub)
        throws XLogException;

    int unsubscribe(Subscription sub)
        throws XLogException;

    byte[] getBytes(int categoryId)
        throws XLogException;
}
