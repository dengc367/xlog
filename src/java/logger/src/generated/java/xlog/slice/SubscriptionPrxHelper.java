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

public final class SubscriptionPrxHelper extends Ice.ObjectPrxHelperBase implements SubscriptionPrx
{
    public static SubscriptionPrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        SubscriptionPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (SubscriptionPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::xlog::slice::Subscription"))
                {
                    SubscriptionPrxHelper __h = new SubscriptionPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static SubscriptionPrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        SubscriptionPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (SubscriptionPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::xlog::slice::Subscription", __ctx))
                {
                    SubscriptionPrxHelper __h = new SubscriptionPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static SubscriptionPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        SubscriptionPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::xlog::slice::Subscription"))
                {
                    SubscriptionPrxHelper __h = new SubscriptionPrxHelper();
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

    public static SubscriptionPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        SubscriptionPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::xlog::slice::Subscription", __ctx))
                {
                    SubscriptionPrxHelper __h = new SubscriptionPrxHelper();
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

    public static SubscriptionPrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        SubscriptionPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (SubscriptionPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                SubscriptionPrxHelper __h = new SubscriptionPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static SubscriptionPrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        SubscriptionPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            SubscriptionPrxHelper __h = new SubscriptionPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _SubscriptionDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _SubscriptionDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, SubscriptionPrx v)
    {
        __os.writeProxy(v);
    }

    public static SubscriptionPrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            SubscriptionPrxHelper result = new SubscriptionPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
