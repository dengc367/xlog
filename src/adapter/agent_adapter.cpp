#include <boost/lexical_cast.hpp>

#include "src/common/util.h"
#include "src/common/common.h"
#include "src/adapter/agent_adapter.h"
#include "src/common/logger.h"

namespace xlog
{

bool AgentAdapter::init(const ::Ice::StringSeq& defaultAgents,const bool is_udp_protocol, const bool is_compress)
{  
    if (defaultAgents.empty())
    {
        XLOG_ERROR("AgentAdapter::init defaultAgent is empty!");
        return false;
    }

    srand(unsigned(time(NULL)));
    Ice::PropertiesPtr props=Ice::createProperties();
    props->setProperty("Ice.MessageSizeMax", ICE_MESSAGE_SIZE_MAX);
    props->setProperty("Ice.Override.Timeout", ICE_TIMEOUT_MILLISECONDS);
	Ice::InitializationData id;
    id.properties=props;
    _ic = ::Ice::initialize(id);

    srand((unsigned) time(NULL));
    current_agent_prx_number=0;

    std::vector<slice::AgentPrx> _prxs;
    for (::Ice::StringSeq::const_iterator it = defaultAgents.begin(); it != defaultAgents.end();
            ++it)
    {
        slice::AgentPrx prx = Util::getPrx<slice::AgentPrx>(_ic, *it, is_udp_protocol, 1000, is_compress);
	_prxs.push_back(prx);
    }
    agent_prxs.swap(_prxs);

    return true;
}

slice::AgentPrx AgentAdapter::getAgentPrx()
{
    int size=agent_prxs.size();
    if(current_agent_prx_number==size)
    {
        current_agent_prx_number=0;
    }
    return agent_prxs.at(current_agent_prx_number++);
    
}

bool AgentAdapter::send(const slice::LogDataSeq& data)
{
    int size=agent_prxs.size();
    for(int i=0;i<size;i++)
    {
       try
       {
           getAgentPrx()->add(data);
           return true;
       } catch (::Ice::TimeoutException& e)
       {
           XLOG_WARN("AgentAdapter::send failed timeout in " << i+1  << " times, repeat again! The Exception: " << e);
       } catch (::Ice::Exception& e)
       {
           XLOG_WARN("AgentAdapter::send failed in " << i+1  << " times, repeat again! The Exception: " << e);
       }
    }
    XLOG_ERROR("AgentAdapter::send failed after " << size  << " times. The data size: " << data.size() << "." );
    return false;
}

}
