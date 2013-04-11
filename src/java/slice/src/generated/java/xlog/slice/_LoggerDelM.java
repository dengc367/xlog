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

public final class _LoggerDelM extends Ice._ObjectDelM implements _LoggerDel
{
    public void
    add(LogData[] data, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __handler.getOutgoing("add", Ice.OperationMode.Normal, __ctx);
        try
        {
            try
            {
                IceInternal.BasicStream __os = __og.os();
                LogDataSeqHelper.write(__os, data);
            }
            catch(Ice.LocalException __ex)
            {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            if(!__og.is().isEmpty())
            {
                try
                {
                    if(!__ok)
                    {
                        try
                        {
                            __og.throwUserException();
                        }
                        catch(Ice.UserException __ex)
                        {
                            throw new Ice.UnknownUserException(__ex.ice_name());
                        }
                    }
                    __og.is().skipEmptyEncaps();
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
        }
        finally
        {
            __handler.reclaimOutgoing(__og);
        }
    }

    public void
    addLogData(LogData data, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __handler.getOutgoing("addLogData", Ice.OperationMode.Normal, __ctx);
        try
        {
            try
            {
                IceInternal.BasicStream __os = __og.os();
                data.__write(__os);
            }
            catch(Ice.LocalException __ex)
            {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            if(!__og.is().isEmpty())
            {
                try
                {
                    if(!__ok)
                    {
                        try
                        {
                            __og.throwUserException();
                        }
                        catch(Ice.UserException __ex)
                        {
                            throw new Ice.UnknownUserException(__ex.ice_name());
                        }
                    }
                    __og.is().skipEmptyEncaps();
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
        }
        finally
        {
            __handler.reclaimOutgoing(__og);
        }
    }
}
