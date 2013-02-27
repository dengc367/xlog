#ifndef __CLIENT_H__
#define __CLIENT_H__

#include <string>

#include <IceUtil/Monitor.h>
#include <IceUtil/Mutex.h>
#include <IceUtil/Thread.h>

#include "xlog.h"
#include "src/common/common.h"

namespace xlog
{

class Client : public ::IceUtil::Thread
{
public:

    Client(const ::Ice::StringSeq& defaultAgents,
            const bool is_udp_protocol, const int maxQueueSize, const bool isCompress);
    bool doSend(const slice::LogDataSeq& data);
    void close();
protected:
    void run();

    slice::LogDataSeq _data;

    ::IceUtil::Monitor<IceUtil::Mutex> _dataMutex;

    ::Ice::StringSeq _defaultAgents;

    int _maxQueueSize;
     
    bool _is_udp_protocol;

    bool _is_compress;

    ::IceUtil::Mutex _agentMutex;

    AgentAdapter *_agentAdapter;
};

}

#endif
