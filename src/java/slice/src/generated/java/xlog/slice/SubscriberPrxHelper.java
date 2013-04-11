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

public final class SubscriberPrxHelper extends Ice.ObjectPrxHelperBase implements SubscriberPrx
{
    public void
    _notify(String[] config)
    {
        _notify(config, null, false);
    }

    public void
    _notify(String[] config, java.util.Map<String, String> __ctx)
    {
        _notify(config, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    _notify(String[] config, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                _SubscriberDel __del = (_SubscriberDel)__delBase;
                __del._notify(config, __ctx);
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

    public static SubscriberPrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        SubscriberPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (SubscriberPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::xlog::slice::Subscriber"))
                {
                    SubscriberPrxHelper __h = new SubscriberPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static SubscriberPrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        SubscriberPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (SubscriberPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::xlog::slice::Subscriber", __ctx))
                {
                    SubscriberPrxHelper __h = new SubscriberPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static SubscriberPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        SubscriberPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::xlog::slice::Subscriber"))
                {
                    SubscriberPrxHelper __h = new SubscriberPrxHelper();
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

    public static SubscriberPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        SubscriberPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::xlog::slice::Subscriber", __ctx))
                {
                    SubscriberPrxHelper __h = new SubscriberPrxHelper();
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

    public static SubscriberPrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        SubscriberPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (SubscriberPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                SubscriberPrxHelper __h = new SubscriberPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static SubscriberPrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        SubscriberPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            SubscriberPrxHelper __h = new SubscriberPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _SubscriberDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _SubscriberDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, SubscriberPrx v)
    {
        __os.writeProxy(v);
    }

    public static SubscriberPrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            SubscriberPrxHelper result = new SubscriberPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
