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

public class XLogException extends Ice.UserException
{
    public XLogException()
    {
    }

    public XLogException(ErrorCode code, String msg)
    {
        this.code = code;
        this.msg = msg;
    }

    public String
    ice_name()
    {
        return "xlog::slice::XLogException";
    }

    public ErrorCode code;

    public String msg;

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeString("::xlog::slice::XLogException");
        __os.startWriteSlice();
        code.__write(__os);
        __os.writeString(msg);
        __os.endWriteSlice();
    }

    public void
    __read(IceInternal.BasicStream __is, boolean __rid)
    {
        if(__rid)
        {
            __is.readString();
        }
        __is.startReadSlice();
        code = ErrorCode.__read(__is);
        msg = __is.readString();
        __is.endReadSlice();
    }

    public void
    __write(Ice.OutputStream __outS)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "exception xlog::slice::XLogException was not generated with stream support";
        throw ex;
    }

    public void
    __read(Ice.InputStream __inS, boolean __rid)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "exception xlog::slice::XLogException was not generated with stream support";
        throw ex;
    }
}
