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

public final class DispatcherPrxHelper extends Ice.ObjectPrxHelperBase implements DispatcherPrx
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
                _DispatcherDel __del = (_DispatcherDel)__delBase;
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
                _DispatcherDel __del = (_DispatcherDel)__delBase;
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

    public void
    createZNode(int slot)
    {
        createZNode(slot, null, false);
    }

    public void
    createZNode(int slot, java.util.Map<String, String> __ctx)
    {
        createZNode(slot, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    createZNode(int slot, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                _DispatcherDel __del = (_DispatcherDel)__delBase;
                __del.createZNode(slot, __ctx);
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

    public byte[]
    getBytes(int categoryId)
        throws XLogException
    {
        return getBytes(categoryId, null, false);
    }

    public byte[]
    getBytes(int categoryId, java.util.Map<String, String> __ctx)
        throws XLogException
    {
        return getBytes(categoryId, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private byte[]
    getBytes(int categoryId, java.util.Map<String, String> __ctx, boolean __explicitCtx)
        throws XLogException
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
                __checkTwowayOnly("getBytes");
                __delBase = __getDelegate(false);
                _DispatcherDel __del = (_DispatcherDel)__delBase;
                return __del.getBytes(categoryId, __ctx);
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

    public boolean
    register(LoggerPrx subscriber, int frequence)
    {
        return register(subscriber, frequence, null, false);
    }

    public boolean
    register(LoggerPrx subscriber, int frequence, java.util.Map<String, String> __ctx)
    {
        return register(subscriber, frequence, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    register(LoggerPrx subscriber, int frequence, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("register");
                __delBase = __getDelegate(false);
                _DispatcherDel __del = (_DispatcherDel)__delBase;
                return __del.register(subscriber, frequence, __ctx);
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

    public int
    subscribe(Subscription sub)
        throws XLogException
    {
        return subscribe(sub, null, false);
    }

    public int
    subscribe(Subscription sub, java.util.Map<String, String> __ctx)
        throws XLogException
    {
        return subscribe(sub, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    subscribe(Subscription sub, java.util.Map<String, String> __ctx, boolean __explicitCtx)
        throws XLogException
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
                __checkTwowayOnly("subscribe");
                __delBase = __getDelegate(false);
                _DispatcherDel __del = (_DispatcherDel)__delBase;
                return __del.subscribe(sub, __ctx);
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

    public int
    unsubscribe(Subscription sub)
        throws XLogException
    {
        return unsubscribe(sub, null, false);
    }

    public int
    unsubscribe(Subscription sub, java.util.Map<String, String> __ctx)
        throws XLogException
    {
        return unsubscribe(sub, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    unsubscribe(Subscription sub, java.util.Map<String, String> __ctx, boolean __explicitCtx)
        throws XLogException
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
                __checkTwowayOnly("unsubscribe");
                __delBase = __getDelegate(false);
                _DispatcherDel __del = (_DispatcherDel)__delBase;
                return __del.unsubscribe(sub, __ctx);
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

    public static DispatcherPrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        DispatcherPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (DispatcherPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::xlog::slice::Dispatcher"))
                {
                    DispatcherPrxHelper __h = new DispatcherPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static DispatcherPrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        DispatcherPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (DispatcherPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::xlog::slice::Dispatcher", __ctx))
                {
                    DispatcherPrxHelper __h = new DispatcherPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static DispatcherPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        DispatcherPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::xlog::slice::Dispatcher"))
                {
                    DispatcherPrxHelper __h = new DispatcherPrxHelper();
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

    public static DispatcherPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        DispatcherPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::xlog::slice::Dispatcher", __ctx))
                {
                    DispatcherPrxHelper __h = new DispatcherPrxHelper();
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

    public static DispatcherPrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        DispatcherPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (DispatcherPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                DispatcherPrxHelper __h = new DispatcherPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static DispatcherPrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        DispatcherPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            DispatcherPrxHelper __h = new DispatcherPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _DispatcherDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _DispatcherDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, DispatcherPrx v)
    {
        __os.writeProxy(v);
    }

    public static DispatcherPrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            DispatcherPrxHelper result = new DispatcherPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
