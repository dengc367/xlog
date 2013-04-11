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

public final class LogData implements java.lang.Cloneable, java.io.Serializable
{
    public String[] categories;

    public String[] logs;

    public String checkSum;

    public LogData()
    {
    }

    public LogData(String[] categories, String[] logs, String checkSum)
    {
        this.categories = categories;
        this.logs = logs;
        this.checkSum = checkSum;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        LogData _r = null;
        try
        {
            _r = (LogData)rhs;
        }
        catch(ClassCastException ex)
        {
        }

        if(_r != null)
        {
            if(!java.util.Arrays.equals(categories, _r.categories))
            {
                return false;
            }
            if(!java.util.Arrays.equals(logs, _r.logs))
            {
                return false;
            }
            if(checkSum != _r.checkSum && checkSum != null && !checkSum.equals(_r.checkSum))
            {
                return false;
            }

            return true;
        }

        return false;
    }

    public int
    hashCode()
    {
        int __h = 0;
        if(categories != null)
        {
            for(int __i0 = 0; __i0 < categories.length; __i0++)
            {
                if(categories[__i0] != null)
                {
                    __h = 5 * __h + categories[__i0].hashCode();
                }
            }
        }
        if(logs != null)
        {
            for(int __i1 = 0; __i1 < logs.length; __i1++)
            {
                if(logs[__i1] != null)
                {
                    __h = 5 * __h + logs[__i1].hashCode();
                }
            }
        }
        if(checkSum != null)
        {
            __h = 5 * __h + checkSum.hashCode();
        }
        return __h;
    }

    public java.lang.Object
    clone()
    {
        java.lang.Object o = null;
        try
        {
            o = super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            assert false; // impossible
        }
        return o;
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        Ice.StringSeqHelper.write(__os, categories);
        Ice.StringSeqHelper.write(__os, logs);
        __os.writeString(checkSum);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        categories = Ice.StringSeqHelper.read(__is);
        logs = Ice.StringSeqHelper.read(__is);
        checkSum = __is.readString();
    }
}
