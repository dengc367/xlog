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

public final class LoggerPrxHelper extends Ice.ObjectPrxHelperBase implements LoggerPrx
{
    public void
    add(LogData[] data)
    {
        add(data, null, false);
    }

    public void
    add(LogData[] data, java.util.Map<String, String> __ctx)
    {
        add(data, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    add(LogData[] data, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __delBase = __getDelegate(false);
                _LoggerDel __del = (_LoggerDel)__delBase;
                __del.add(data, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public void
    addLogData(LogData data)
    {
        addLogData(data, null, false);
    }

    public void
    addLogData(LogData data, java.util.Map<String, String> __ctx)
    {
        addLogData(data, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    addLogData(LogData data, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __delBase = __getDelegate(false);
                _LoggerDel __del = (_LoggerDel)__delBase;
                __del.addLogData(data, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public static LoggerPrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        LoggerPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (LoggerPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::xlog::slice::Logger"))
                {
                    LoggerPrxHelper __h = new LoggerPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static LoggerPrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        LoggerPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (LoggerPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::xlog::slice::Logger", __ctx))
                {
                    LoggerPrxHelper __h = new LoggerPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static LoggerPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        LoggerPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::xlog::slice::Logger"))
                {
                    LoggerPrxHelper __h = new LoggerPrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static LoggerPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        LoggerPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::xlog::slice::Logger", __ctx))
                {
                    LoggerPrxHelper __h = new LoggerPrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static LoggerPrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        LoggerPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (LoggerPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                LoggerPrxHelper __h = new LoggerPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static LoggerPrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        LoggerPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            LoggerPrxHelper __h = new LoggerPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _LoggerDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _LoggerDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, LoggerPrx v)
    {
        __os.writeProxy(v);
    }

    public static LoggerPrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            LoggerPrxHelper result = new LoggerPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
