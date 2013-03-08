#ifndef __XLOG_APPENDER_H__
#define __XLOG_APPENDER_H__
#include "xlog.h"
#include <Ice/BuiltinSequences.h>
#include "src/client/client.h"
#include "src/client/xlog_properties.h"
#include <IceUtil/IceUtil.h>
#include "src/common/logger.h"


using namespace std;
namespace xlog{
    class XLogAppender : virtual public IceUtil::Shared
    {
        public:
            XLogAppender(const vector<string>& categories, const char* properties_file_path);
            XLogAppender(const vector<string>& categories, const bool is_udp_protocol = true, const int maxSendSize = 10000, int maxQueueSize = 100000, const bool isCompress = true, const bool isAsync = true, string& hosts = xlog::XLogProperties::DEFAULT_HOSTS);
            int append(const string& msg);
            int append(const char* msg, int const len);
            void close();
        private:
            void init(const XLogProperties& properties);
             xlog::Client* _client;
             int _maxSendSize;
             vector<string> _categories;
             Ice::StringSeq _logSeq;
             int _strlen;
             ::IceUtil::Monitor<IceUtil::Mutex> _mutex;
    };
    typedef IceUtil::Handle<XLogAppender> XLogAppenderPtr;
    //typedef XLogAppender* XLogAppenderPtr;
}


#endif
