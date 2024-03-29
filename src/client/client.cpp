#include "src/adapter/agent_adapter.h"
#include "src/client/client.h"
#include "src/common/logger.h"

namespace xlog
{

Client::Client(const ::Ice::StringSeq& defaultAgents,
        const bool is_udp_protocol, const bool isCompress) :
         _defaultAgents(defaultAgents), _is_udp_protocol(is_udp_protocol),
         _is_compress(isCompress)
{   
    _agentAdapter = new AgentAdapter;
   _flag=_agentAdapter->init(_defaultAgents,_is_udp_protocol, _is_compress);
}

AsyncClient::AsyncClient(const ::Ice::StringSeq& defaultAgents,
        const bool is_udp_protocol, const int maxQueueSize, const bool isCompress) :
    Client::Client(defaultAgents, is_udp_protocol, isCompress), _maxQueueSize(maxQueueSize)
{
   if (_flag)
   { 
      XLOG_INFO("AsyncClient:: success to init agent adapter!");
      start().detach(); 
   } else
   {
      XLOG_ERROR("AsyncClient:: failt to init agent adapter!");
   }
}

bool AsyncClient::doSend(const slice::LogDataSeq& data)
{
    ::IceUtil::Monitor<IceUtil::Mutex>::Lock lock(_dataMutex);
    if (_data.size() >= _maxQueueSize)
    {
        XLOG_ERROR("Client::append queue is full, maxQueueSize is " << _maxQueueSize);
        return false;
    }

    _data.insert(_data.end(), data.begin(), data.end());

    _dataMutex.notify();

    return true;
}

void AsyncClient::run()
{
    for (;;)
    {
        slice::LogDataSeq data;
        {
            ::IceUtil::Monitor<IceUtil::Mutex>::Lock lock(_dataMutex);
            if (_data.empty())
            {    
                _dataMutex.wait();
            }
            if(_data.size()>5)
            {
               slice::LogDataSeq::iterator begin_it=_data.begin();
               slice::LogDataSeq::iterator end_it=begin_it+5;
               data.assign(begin_it,end_it);
               _data.erase(begin_it,end_it);
            }else
            {
               data.swap(_data);
            }
            _dataMutex.notify();
        }
        if(!_agentAdapter->send(data)) {
           //XLOG_ERROR("Fail to send data to agent,data count:"<< data.size());
           //TODO failed data local cached.
        } 
    }
}

void AsyncClient::close()
{
   while(!_data.empty())
   {
      sleep(2);
   }   
}

SyncClient::SyncClient(const ::Ice::StringSeq& defaultAgents,
        const bool is_udp_protocol, const bool isCompress) :
        Client::Client(defaultAgents, is_udp_protocol, isCompress)        
{
   if (_flag)
   { 
      XLOG_INFO("SyncClient:: success to init agent adapter!");
   } else
   {
      XLOG_ERROR("SyncClient:: failt to init agent adapter!");
   }
}

bool SyncClient::doSend(const slice::LogDataSeq& data)
{
    return _agentAdapter->send(data);
}
}
