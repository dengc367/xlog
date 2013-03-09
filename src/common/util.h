#ifndef __UTIL_HPP__
#define __UTIL_HPP__

#include <Ice/Ice.h>

#include <boost/algorithm/string/split.hpp>
#include <boost/algorithm/string/classification.hpp>
#include "src/common/logger.h"

namespace xlog
{
class Util
{
public:
    template<class T>
    static T getPrx(const ::Ice::CommunicatorPtr& ic, const std::string& prxStr, bool udp,
            const int timeout, const bool compress)
    {
        std::vector < std::string > parts;
        boost::algorithm::split(parts, prxStr, boost::algorithm::is_any_of(":"));
        if (parts.size() != 2)
        {
            XLOG_ERROR("Util::getPrx prx string " << prxStr
                    << " does not match the format : <host>:<port>!");
            return NULL;
        }
        std::string host = parts[0];
        std::string port = parts[1];
        std::ostringstream os;
        if(udp)
        {
	   os << "A:udp -h " << host << " -p " << port;
           return T::uncheckedCast(ic->stringToProxy(os.str())->ice_locatorCacheTimeout(60)->ice_compress(compress)->ice_datagram());
	}else
        {
	   os << "A:tcp -h " << host << " -p " << port;
           return T::checkedCast(ic->stringToProxy(os.str())->ice_twoway()->ice_timeout(timeout)->ice_compress(compress));
	}	
    }
};

}

#endif
