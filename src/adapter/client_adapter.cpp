#include <boost/algorithm/string/split.hpp>

#include "src/common/util.h"
#include "src/config/client_config_manager.h"

#include "src/adapter/client_adapter.h"

namespace xlog
{

ClientAdapter::ClientAdapter(const ClientConfigPtr& clientCM) :
        _clientCM(clientCM)
{
    _ic = Ice::initialize();
}

void ClientAdapter::notify(const std::vector<std::string>& agentConfig)
{
    std::vector < std::string > prxVec = _clientCM->getConfig();

    for (std::vector<std::string>::const_iterator it = prxVec.begin(); it != prxVec.end(); ++it)
    {
        slice::SubscriberPrx prx = Util::getPrx<slice::SubscriberPrx>(_ic, *it, true, 300, true);

        try
        {
            prx->notify(agentConfig);
            continue;
        } catch (Ice::Exception& e)
        {
            XLOG_ERROR("ClientAdapter::notify Ice::Exception of prx " << prx->ice_toString());
        } catch (...)
        {
            XLOG_ERROR("ClientAdapter::notify UnknownException of prx " << prx->ice_toString());
        }

        _clientCM->remove(*it);
    }
}

}
