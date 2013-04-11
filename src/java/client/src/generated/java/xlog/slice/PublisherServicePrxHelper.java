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

public final class PublisherServicePrxHelper extends Ice.ObjectPrxHelperBase implements PublisherServicePrx
{
    public int
    publish(LogData data)
    {
        return publish(data, null, false);
    }

    public int
    publish(LogData data, java.util.Map<String, String> __ctx)
    {
        return publish(data, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    publish(LogData data, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("publish");
                __delBase = __getDelegate(false);
                _PublisherServiceDel __del = (_PublisherServiceDel)__delBase;
                return __del.publish(data, __ctx);
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

    public static PublisherServicePrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        PublisherServicePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (PublisherServicePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::xlog::slice::PublisherService"))
                {
                    PublisherServicePrxHelper __h = new PublisherServicePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static PublisherServicePrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        PublisherServicePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (PublisherServicePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::xlog::slice::PublisherService", __ctx))
                {
                    PublisherServicePrxHelper __h = new PublisherServicePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static PublisherServicePrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        PublisherServicePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::xlog::slice::PublisherService"))
                {
                    PublisherServicePrxHelper __h = new PublisherServicePrxHelper();
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

    public static PublisherServicePrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        PublisherServicePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::xlog::slice::PublisherService", __ctx))
                {
                    PublisherServicePrxHelper __h = new PublisherServicePrxHelper();
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

    public static PublisherServicePrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        PublisherServicePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (PublisherServicePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                PublisherServicePrxHelper __h = new PublisherServicePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static PublisherServicePrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        PublisherServicePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            PublisherServicePrxHelper __h = new PublisherServicePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _PublisherServiceDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _PublisherServiceDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, PublisherServicePrx v)
    {
        __os.writeProxy(v);
    }

    public static PublisherServicePrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            PublisherServicePrxHelper result = new PublisherServicePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
