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

public abstract class _AgentDisp extends Ice.ObjectImpl implements Agent
{
    protected void
    ice_copyStateFrom(Ice.Object __obj)
        throws java.lang.CloneNotSupportedException
    {
        throw new java.lang.CloneNotSupportedException();
    }

    public static final String[] __ids =
    {
        "::Ice::Object",
        "::xlog::slice::Agent"
    };

    public boolean
    ice_isA(String s)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public boolean
    ice_isA(String s, Ice.Current __current)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public String[]
    ice_ids()
    {
        return __ids;
    }

    public String[]
    ice_ids(Ice.Current __current)
    {
        return __ids;
    }

    public String
    ice_id()
    {
        return __ids[1];
    }

    public String
    ice_id(Ice.Current __current)
    {
        return __ids[1];
    }

    public static String
    ice_staticId()
    {
        return __ids[1];
    }

    public final void
    add(LogData[] data)
    {
        add(data, null);
    }

    public final void
    addFailedLogData(LogData[] data)
    {
        addFailedLogData(data, null);
    }

    public final String[]
    subscribeClient(String prxStr)
    {
        return subscribeClient(prxStr, null);
    }

    public final String[]
    subscribeSubscriber(String[] categories, String prxStr)
    {
        return subscribeSubscriber(categories, prxStr, null);
    }

    public static Ice.DispatchStatus
    ___add(Agent __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        LogData[] data;
        data = LogDataSeqHelper.read(__is);
        __is.endReadEncaps();
        __obj.add(data, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___addFailedLogData(Agent __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        LogData[] data;
        data = LogDataSeqHelper.read(__is);
        __is.endReadEncaps();
        __obj.addFailedLogData(data, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___subscribeClient(Agent __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String prxStr;
        prxStr = __is.readString();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String[] __ret = __obj.subscribeClient(prxStr, __current);
        Ice.StringSeqHelper.write(__os, __ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___subscribeSubscriber(Agent __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String[] categories;
        categories = Ice.StringSeqHelper.read(__is);
        String prxStr;
        prxStr = __is.readString();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String[] __ret = __obj.subscribeSubscriber(categories, prxStr, __current);
        Ice.StringSeqHelper.write(__os, __ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    private final static String[] __all =
    {
        "add",
        "addFailedLogData",
        "ice_id",
        "ice_ids",
        "ice_isA",
        "ice_ping",
        "subscribeClient",
        "subscribeSubscriber"
    };

    public Ice.DispatchStatus
    __dispatch(IceInternal.Incoming in, Ice.Current __current)
    {
        int pos = java.util.Arrays.binarySearch(__all, __current.operation);
        if(pos < 0)
        {
            throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
        }

        switch(pos)
        {
            case 0:
            {
                return ___add(this, in, __current);
            }
            case 1:
            {
                return ___addFailedLogData(this, in, __current);
            }
            case 2:
            {
                return ___ice_id(this, in, __current);
            }
            case 3:
            {
                return ___ice_ids(this, in, __current);
            }
            case 4:
            {
                return ___ice_isA(this, in, __current);
            }
            case 5:
            {
                return ___ice_ping(this, in, __current);
            }
            case 6:
            {
                return ___subscribeClient(this, in, __current);
            }
            case 7:
            {
                return ___subscribeSubscriber(this, in, __current);
            }
        }

        assert(false);
        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeTypeId(ice_staticId());
        __os.startWriteSlice();
        __os.endWriteSlice();
        super.__write(__os);
    }

    public void
    __read(IceInternal.BasicStream __is, boolean __rid)
    {
        if(__rid)
        {
            __is.readTypeId();
        }
        __is.startReadSlice();
        __is.endReadSlice();
        super.__read(__is, true);
    }

    public void
    __write(Ice.OutputStream __outS)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type xlog::slice::Agent was not generated with stream support";
        throw ex;
    }

    public void
    __read(Ice.InputStream __inS, boolean __rid)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type xlog::slice::Agent was not generated with stream support";
        throw ex;
    }
}
