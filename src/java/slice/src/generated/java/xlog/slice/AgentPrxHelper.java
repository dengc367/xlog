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

public final class AgentPrxHelper extends Ice.ObjectPrxHelperBase implements AgentPrx
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
                _AgentDel __del = (_AgentDel)__delBase;
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
    addFailedLogData(LogData[] data)
    {
        addFailedLogData(data, null, false);
    }

    public void
    addFailedLogData(LogData[] data, java.util.Map<String, String> __ctx)
    {
        addFailedLogData(data, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    addFailedLogData(LogData[] data, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                _AgentDel __del = (_AgentDel)__delBase;
                __del.addFailedLogData(data, __ctx);
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

    public String[]
    subscribeClient(String prxStr)
    {
        return subscribeClient(prxStr, null, false);
    }

    public String[]
    subscribeClient(String prxStr, java.util.Map<String, String> __ctx)
    {
        return subscribeClient(prxStr, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String[]
    subscribeClient(String prxStr, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("subscribeClient");
                __delBase = __getDelegate(false);
                _AgentDel __del = (_AgentDel)__delBase;
                return __del.subscribeClient(prxStr, __ctx);
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

    public String[]
    subscribeSubscriber(String[] categories, String prxStr)
    {
        return subscribeSubscriber(categories, prxStr, null, false);
    }

    public String[]
    subscribeSubscriber(String[] categories, String prxStr, java.util.Map<String, String> __ctx)
    {
        return subscribeSubscriber(categories, prxStr, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String[]
    subscribeSubscriber(String[] categories, String prxStr, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("subscribeSubscriber");
                __delBase = __getDelegate(false);
                _AgentDel __del = (_AgentDel)__delBase;
                return __del.subscribeSubscriber(categories, prxStr, __ctx);
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

    public static AgentPrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        AgentPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (AgentPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::xlog::slice::Agent"))
                {
                    AgentPrxHelper __h = new AgentPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static AgentPrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        AgentPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (AgentPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::xlog::slice::Agent", __ctx))
                {
                    AgentPrxHelper __h = new AgentPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static AgentPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        AgentPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::xlog::slice::Agent"))
                {
                    AgentPrxHelper __h = new AgentPrxHelper();
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

    public static AgentPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        AgentPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::xlog::slice::Agent", __ctx))
                {
                    AgentPrxHelper __h = new AgentPrxHelper();
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

    public static AgentPrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        AgentPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (AgentPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                AgentPrxHelper __h = new AgentPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static AgentPrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        AgentPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            AgentPrxHelper __h = new AgentPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _AgentDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _AgentDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, AgentPrx v)
    {
        __os.writeProxy(v);
    }

    public static AgentPrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            AgentPrxHelper result = new AgentPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
