#include <Ice/Ice.h>
#include <boost/algorithm/string/split.hpp>
#include <boost/algorithm/string/classification.hpp>

#include "src/common/logger.h"
#include "src/common/zk_manager.h"
#include "src/agent/AgentI.h"
#include "src/config/agent_config_manager.h"
#include "src/config/client_config_manager.h"
#include "src/config/dispatcher_config.h"
#include "src/adapter/client_adapter.h"
#include "src/adapter/dispatcher_adapter.h"

using namespace std;
using namespace xlog;
class AgentApp: virtual public Ice::Application
{
public:
    virtual int run(int argc , char* argv[])
    {

        INIT_LOG4CPLUS_PROPERTIES("../conf/log4cplus.properties"); // init the log4cplus configurator.

        XLOG_DEBUG("Checking the args valid.");
        if(argc==1)
        {
            XLOG_ERROR( "Usage:zk_host:zk_port [-udp|-tcp] agent_host:agent_port while_list_file_name" );
            return 1;
        }
        *argv++;

        shutdownOnInterrupt();

        ZKConnectionPtr conn = ZKConnectionPtr(new ZKConnection);
        if (!conn->init(*argv++))
        {
            XLOG_ERROR(appName() << ": can not init zk, exit");
            return 2;
        }
        XLOG_INFO( "ZooKeeper inited. Now initializing ICE. ");
        std::vector < std::string > parts;
        std::string udp("-udp");
        bool is_udp=udp.compare(*argv++)==0?true:false;

        boost::algorithm::split(parts, *argv++, boost::algorithm::is_any_of(":"));
        if (parts.size() != 2)
        {
            XLOG_ERROR("agent host:port is " << *argv 
                    << ",does not match the format : <host>:<port>!" );
            return 3;
        }

        // start the agent now.
        XLOG_DEBUG("The args are valid, Start the agent now.");

        std::ostringstream os;
        Ice::PropertiesPtr props=Ice::createProperties();
        if(is_udp)
        {
            props->setProperty("Ice.UDP.RcvSize",ICE_UDP_RCVSIZE);
            os << "udp -h " << parts[0] << " -p " << parts[1];
        }else
        {
            os << "tcp -h " << parts[0] << " -p " << parts[1];
            props->setProperty("Ice.ThreadPool.Server.Size", "10");
            props->setProperty("Ice.ThreadPool.Server.SizeMax", "3000");
            props->setProperty("Ice.ThreadPool.Server.StackSize", "131072");
        }
        props->setProperty("Ice.MessageSizeMax",ICE_MESSAGE_SIZE_MAX);
        props->setProperty("Ice.Override.Timeout", ICE_TIMEOUT_MILLISECONDS);
        Ice::InitializationData id;
        id.properties=props;
        Ice::CommunicatorPtr ic=Ice::initialize(id);
        Ice::ObjectAdapterPtr adapter = ic->createObjectAdapterWithEndpoints(appName(),
                os.str());
        XLOG_INFO( "initializing agent. ");
        AgentIPtr agent = new AgentI;
        agent->init(ic, conn, *argv);
        XLOG_INFO(std::cout << "Agent started." );
        Ice::ObjectPrx prx = adapter->add(agent, ic->stringToIdentity("A"));
        XLOG_INFO( "Activating agent");
        adapter->activate();
        XLOG_INFO( "Agent activated.") ;
        communicator()->waitForShutdown();
        if (interrupted())
        {
            XLOG_ERROR("Agent " << appName() << ": received signal, shutting down" );
        }
        return 0;
    }
    ;
};

int main(int argc, char* argv[])
{
    AgentApp app;
    return app.main(argc, argv);
}
