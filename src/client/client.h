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

class Client
{
public:

    Client(const ::Ice::StringSeq& defaultAgents,
            const bool is_udp_protocol, const bool isCompress);
    virtual bool doSend(const slice::LogDataSeq& data) = 0;
    virtual void close(){}
    virtual ~Client(){}
protected:
    ::Ice::StringSeq _defaultAgents;
    bool _is_udp_protocol;
    bool _is_compress;
    bool _flag;
    AgentAdapter *_agentAdapter;
};

class SyncClient : public Client{
public:
    SyncClient(const ::Ice::StringSeq& defaultAgents,
            const bool is_udp_protocol, const bool isCompress);
    ~SyncClient(){}
    bool doSend(const slice::LogDataSeq& data);
};

class AsyncClient : public Client, public ::IceUtil::Thread{
public:
    AsyncClient(const ::Ice::StringSeq& defaultAgents,
            const bool is_udp_protocol, const int maxQueueSize, const bool isCompress);
    ~AsyncClient(){}
    bool doSend(const slice::LogDataSeq& data);
    void close();
protected:
    void run();
    ::IceUtil::Monitor<IceUtil::Mutex> _dataMutex;
    slice::LogDataSeq _data;
    int _maxQueueSize;
};
}

#endif
