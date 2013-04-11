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

public final class PushSubscriptionPrxHelper extends Ice.ObjectPrxHelperBase implements PushSubscriptionPrx
{
    public static PushSubscriptionPrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        PushSubscriptionPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (PushSubscriptionPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::xlog::slice::PushSubscription"))
                {
                    PushSubscriptionPrxHelper __h = new PushSubscriptionPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static PushSubscriptionPrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        PushSubscriptionPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (PushSubscriptionPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::xlog::slice::PushSubscription", __ctx))
                {
                    PushSubscriptionPrxHelper __h = new PushSubscriptionPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static PushSubscriptionPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        PushSubscriptionPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::xlog::slice::PushSubscription"))
                {
                    PushSubscriptionPrxHelper __h = new PushSubscriptionPrxHelper();
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

    public static PushSubscriptionPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        PushSubscriptionPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::xlog::slice::PushSubscription", __ctx))
                {
                    PushSubscriptionPrxHelper __h = new PushSubscriptionPrxHelper();
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

    public static PushSubscriptionPrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        PushSubscriptionPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (PushSubscriptionPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                PushSubscriptionPrxHelper __h = new PushSubscriptionPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static PushSubscriptionPrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        PushSubscriptionPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            PushSubscriptionPrxHelper __h = new PushSubscriptionPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _PushSubscriptionDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _PushSubscriptionDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, PushSubscriptionPrx v)
    {
        __os.writeProxy(v);
    }

    public static PushSubscriptionPrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            PushSubscriptionPrxHelper result = new PushSubscriptionPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
