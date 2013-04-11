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

public interface _DispatcherDel extends Ice._ObjectDel
{
    void add(LogData[] data, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void addLogData(LogData data, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void createZNode(int slot, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean register(LoggerPrx subscriber, int frequence, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int subscribe(Subscription sub, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper,
               XLogException;

    int unsubscribe(Subscription sub, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper,
               XLogException;

    byte[] getBytes(int categoryId, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper,
               XLogException;
}
