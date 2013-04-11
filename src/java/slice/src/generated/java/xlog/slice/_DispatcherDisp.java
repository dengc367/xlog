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

public abstract class _DispatcherDisp extends Ice.ObjectImpl implements Dispatcher
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
        "::xlog::slice::Dispatcher"
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
    addLogData(LogData data)
    {
        addLogData(data, null);
    }

    public final void
    createZNode(int slot)
    {
        createZNode(slot, null);
    }

    public final byte[]
    getBytes(int categoryId)
        throws XLogException
    {
        return getBytes(categoryId, null);
    }

    public final boolean
    register(LoggerPrx subscriber, int frequence)
    {
        return register(subscriber, frequence, null);
    }

    public final int
    subscribe(Subscription sub)
        throws XLogException
    {
        return subscribe(sub, null);
    }

    public final int
    unsubscribe(Subscription sub)
        throws XLogException
    {
        return unsubscribe(sub, null);
    }

    public static Ice.DispatchStatus
    ___add(Dispatcher __obj, IceInternal.Incoming __inS, Ice.Current __current)
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
    ___addLogData(Dispatcher __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        LogData data;
        data = new LogData();
        data.__read(__is);
        __is.endReadEncaps();
        __obj.addLogData(data, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___createZNode(Dispatcher __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int slot;
        slot = __is.readInt();
        __is.endReadEncaps();
        __obj.createZNode(slot, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___register(Dispatcher __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        LoggerPrx subscriber;
        subscriber = LoggerPrxHelper.__read(__is);
        int frequence;
        frequence = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.register(subscriber, frequence, __current);
        __os.writeBool(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___subscribe(Dispatcher __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        SubscriptionHolder sub = new SubscriptionHolder();
        __is.readObject(sub.getPatcher());
        __is.readPendingObjects();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        try
        {
            int __ret = __obj.subscribe(sub.value, __current);
            __os.writeInt(__ret);
            return Ice.DispatchStatus.DispatchOK;
        }
        catch(XLogException ex)
        {
            __os.writeUserException(ex);
            return Ice.DispatchStatus.DispatchUserException;
        }
    }

    public static Ice.DispatchStatus
    ___unsubscribe(Dispatcher __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        SubscriptionHolder sub = new SubscriptionHolder();
        __is.readObject(sub.getPatcher());
        __is.readPendingObjects();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        try
        {
            int __ret = __obj.unsubscribe(sub.value, __current);
            __os.writeInt(__ret);
            return Ice.DispatchStatus.DispatchOK;
        }
        catch(XLogException ex)
        {
            __os.writeUserException(ex);
            return Ice.DispatchStatus.DispatchUserException;
        }
    }

    public static Ice.DispatchStatus
    ___getBytes(Dispatcher __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int categoryId;
        categoryId = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        try
        {
            byte[] __ret = __obj.getBytes(categoryId, __current);
            Ice.ByteSeqHelper.write(__os, __ret);
            return Ice.DispatchStatus.DispatchOK;
        }
        catch(XLogException ex)
        {
            __os.writeUserException(ex);
            return Ice.DispatchStatus.DispatchUserException;
        }
    }

    private final static String[] __all =
    {
        "add",
        "addLogData",
        "createZNode",
        "getBytes",
        "ice_id",
        "ice_ids",
        "ice_isA",
        "ice_ping",
        "register",
        "subscribe",
        "unsubscribe"
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
                return ___addLogData(this, in, __current);
            }
            case 2:
            {
                return ___createZNode(this, in, __current);
            }
            case 3:
            {
                return ___getBytes(this, in, __current);
            }
            case 4:
            {
                return ___ice_id(this, in, __current);
            }
            case 5:
            {
                return ___ice_ids(this, in, __current);
            }
            case 6:
            {
                return ___ice_isA(this, in, __current);
            }
            case 7:
            {
                return ___ice_ping(this, in, __current);
            }
            case 8:
            {
                return ___register(this, in, __current);
            }
            case 9:
            {
                return ___subscribe(this, in, __current);
            }
            case 10:
            {
                return ___unsubscribe(this, in, __current);
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
        ex.reason = "type xlog::slice::Dispatcher was not generated with stream support";
        throw ex;
    }

    public void
    __read(Ice.InputStream __inS, boolean __rid)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type xlog::slice::Dispatcher was not generated with stream support";
        throw ex;
    }
}
