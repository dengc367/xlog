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

public final class ErrorCodeHolder
{
    public
    ErrorCodeHolder()
    {
    }

    public
    ErrorCodeHolder(ErrorCode value)
    {
        this.value = value;
    }

    public ErrorCode value;
}
