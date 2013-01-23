#ifndef __XLOG_APPENDER_H__
#define __XLOG_APPENDER_H__
#include "xlog.h"
#include <Ice/BuiltinSequences.h>
#include "src/client/client.h"
#include "src/client/xlog_properties.h"
#include <IceUtil/IceUtil.h>


using namespace std;
namespace xlog{
    class XLogAppender
    {
        public:
            XLogAppender(const vector<string>& categories, const char* properties_file_path);
            XLogAppender(const vector<string>& categories, const bool is_udp_protocol = true, const int maxSendSize = 0, int maxQueueSize = 100000 , string& hosts = xlog::XLogProperties::DEFAULT_HOSTS);
            int append(const string& msg);
            int append(const char* msg, int const len);
        private:
            void init(const XLogProperties& properties);
             xlog::Client* _client;
             int _maxSendSize;
             vector<string> _categories;
             Ice::StringSeq _logSeq;
             int _strlen;
             ::IceUtil::Monitor<IceUtil::Mutex> _mutex;
    };
    //typedef IceUtil::Handle<XLogAppender> XLogAppenderPtr;
    typedef XLogAppender* XLogAppenderPtr;
}


#endif
