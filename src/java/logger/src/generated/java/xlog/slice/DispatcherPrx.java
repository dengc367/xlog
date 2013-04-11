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

public interface DispatcherPrx extends Ice.ObjectPrx
{
    public void add(LogData[] data);
    public void add(LogData[] data, java.util.Map<String, String> __ctx);

    public void addLogData(LogData data);
    public void addLogData(LogData data, java.util.Map<String, String> __ctx);

    public void createZNode(int slot);
    public void createZNode(int slot, java.util.Map<String, String> __ctx);

    public boolean register(LoggerPrx subscriber, int frequence);
    public boolean register(LoggerPrx subscriber, int frequence, java.util.Map<String, String> __ctx);

    public int subscribe(Subscription sub)
        throws XLogException;
    public int subscribe(Subscription sub, java.util.Map<String, String> __ctx)
        throws XLogException;

    public int unsubscribe(Subscription sub)
        throws XLogException;
    public int unsubscribe(Subscription sub, java.util.Map<String, String> __ctx)
        throws XLogException;

    public byte[] getBytes(int categoryId)
        throws XLogException;
    public byte[] getBytes(int categoryId, java.util.Map<String, String> __ctx)
        throws XLogException;
}
