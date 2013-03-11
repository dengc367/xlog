
#include <Ice/BuiltinSequences.h>
#include "src/client/client.h"
#include "xlog.h"
#include "src/client/xlog_appender.h"
#include "src/client/xlog_properties.h"
#include <boost/algorithm/string.hpp>
#include <boost/lexical_cast.hpp>
#include <string>
#include <iostream>

using namespace std;

namespace xlog
{
    template<class P> string vector2String(vector<P> v);



    XLogAppender::XLogAppender(const vector<string>& categories, const char* properties_file_path):_categories(categories),_strlen(0){

        XLogProperties properties(properties_file_path);
        init(properties);
    }

    XLogAppender::XLogAppender(
            const vector<string>& categories,
            const bool is_udp_protocol, 
            const int maxSendSize, 
            int maxQueueSize, 
            const bool isCompress, 
            const bool isAsync, 
            string& hosts):
        _categories(categories),
        _strlen(0)
    {
        XLogProperties properties(hosts, is_udp_protocol, maxSendSize, maxQueueSize, isCompress, isAsync);
        init(properties);
    }

    void XLogAppender::init(const XLogProperties& properties)
    {
         vector<string> temp =properties.getHosts();
        XLOG_INFO( "Xlog client configuration parameters: " );
         if(properties.isAsync())
         {
            _client = new AsyncClient(temp, properties.isUdpProtocol(), properties.getMaxQueueSize(), properties.isCompress());
            XLOG_INFO( "hosts: " << xlog::vector2String(temp) 
                    << ", isUdpProtocol: "<< properties.isUdpProtocol() 
                    << ", maxSendSize:" << properties.getMaxSendSize() 
                    << ", maxQueueSize: "<< properties.getMaxQueueSize() 
                    << ", isCompress: " << properties.isCompress() 
                    << ", isAsync: " << properties.isAsync() );
         }
         else
         {
            _client = new SyncClient(temp, properties.isUdpProtocol(), properties.isCompress());
            XLOG_INFO( "hosts: " << xlog::vector2String(temp) 
                    << ", isUdpProtocol: "<< properties.isUdpProtocol() 
                    << ", maxSendSize:" << properties.getMaxSendSize() 
                    << ", isCompress: " << properties.isCompress() 
                    << ", isAsync: " << properties.isAsync() );
         }
        _maxSendSize = properties.getMaxSendSize();
        _lastSendTime = time(NULL);
    }


    int XLogAppender::append(const char* msg, int const len)
    {
        string str(msg, len);
        return append(str);
    }

    int XLogAppender::append(const std::string& msg)
    {
        ::IceUtil::Monitor<IceUtil::Mutex>::Lock lock(_mutex);
        _strlen += msg.length();
        _logSeq.push_back(msg);
        if(_strlen >= _maxSendSize || time(NULL) - _lastSendTime > MAX_WAIT_MILLSECONDS)
        {
            xlog::slice::LogDataSeq logDataSeq;
            xlog::slice::LogData logData;
            logData.categories = _categories;
            logData.logs.swap(_logSeq);
            logDataSeq.push_back(logData);
            bool ret = _client->doSend(logDataSeq);
            _strlen = 0;
            _lastSendTime = time(NULL);
            return ! ret;
        }
        _mutex.notify();
        return 0;
    }

    int XLogAppender::append(std::vector<std::string>& msg)
    {
        xlog::slice::LogDataSeq logDataSeq;
        xlog::slice::LogData logData;
        logData.categories = _categories;
        logData.logs.swap(msg);
        logDataSeq.push_back(logData);
        return ! _client->doSend(logDataSeq);
    }

    void XLogAppender::close()
    {
        _client->close();
    }

    template<typename P> inline string vector2String(vector<P> v)
    {
        stringstream ss;
        ss << "["; 
        for(unsigned int i = 0; i < v.size(); i++)
        {
            ss << boost::lexical_cast<string>(v[i]);
            if(i < v.size() -1){
                ss << ","; 
            }
        }
        ss << "]";
        return ss.str();
    }

}
