#ifndef _DISPATCHER_ADAPTER_H__
#define _DISPATCHER_ADAPTER_H__

#include <Ice/Ice.h>

#include <xlog.h>
#include "src/common/common.h"
#include "src/common/DispatcherConfigManager.h"
namespace xlog
{

class DispatcherAdapter: public Ice::Object
{
public:

    DispatcherAdapter(const DispatcherConfigManagerPtr& dispatcherCM);

public:

    bool send(const LogDataSeq& data);

    bool sendFailedLogData(const LogDataSeq& data);

private:

    DispatcherConfigManagerPtr dispatcherCM_;

};

}

#endif
