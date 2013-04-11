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

public enum ErrorCode implements java.io.Serializable
{
    NoSubscription,
    IllegalParameters,
    IOException,
    PubSubNotStartedException;

    public static final int _NoSubscription = 0;
    public static final int _IllegalParameters = 1;
    public static final int _IOException = 2;
    public static final int _PubSubNotStartedException = 3;

    public static ErrorCode
    convert(int val)
    {
        assert val >= 0 && val < 4;
        return values()[val];
    }

    public static ErrorCode
    convert(String val)
    {
        try
        {
            return valueOf(val);
        }
        catch(java.lang.IllegalArgumentException ex)
        {
            return null;
        }
    }

    public int
    value()
    {
        return ordinal();
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeByte((byte)value());
    }

    public static ErrorCode
    __read(IceInternal.BasicStream __is)
    {
        int __v = __is.readByte(4);
        return ErrorCode.convert(__v);
    }
}
