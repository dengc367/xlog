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

public interface SubscriberPrx extends Ice.ObjectPrx
{
    public void _notify(String[] config);
    public void _notify(String[] config, java.util.Map<String, String> __ctx);
}
