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

public interface _DispatcherOperations
{
    void add(LogData[] data, Ice.Current __current);

    void addLogData(LogData data, Ice.Current __current);

    void createZNode(int slot, Ice.Current __current);

    boolean register(LoggerPrx subscriber, int frequence, Ice.Current __current);

    int subscribe(Subscription sub, Ice.Current __current)
        throws XLogException;

    int unsubscribe(Subscription sub, Ice.Current __current)
        throws XLogException;

    byte[] getBytes(int categoryId, Ice.Current __current)
        throws XLogException;
}
