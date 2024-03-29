#include "src/client/xlog_appender.h"
#include "src/client/client.h"
#include <boost/lexical_cast.hpp>
#include <Ice/BuiltinSequences.h>

int main(int argc , char** argv)
{
    int count = 10;
    ++argv;
    if(argc > 1 ){
        count =  boost::lexical_cast< int >(*argv);
    }

    //xlog::Client my_client(agent_seq,false);
    Ice::StringSeq categories;
    categories.push_back("test");
    categories.push_back("www");
    //xlog::XLogAppender batchClient(categories, true,  50, 100); 
    xlog::XLogAppender batchClient(categories, "xlog.conf"); 
    xlog::XLogAppender batchClient2(categories, "xlog.conf"); 
    xlog::XLogAppender batchClient3(categories, "xlog.conf"); 
    xlog::XLogAppender* batchClientPtr = new xlog::XLogAppender(categories, "xlog.conf");

    for(int i = 0; i < count; i++){
        std::cout << "count: " << i << std::endl;
        int ret = batchClient.append("test test test test test test test test" + boost::lexical_cast<std::string>(i));
        ret = batchClient2.append("test test test test test test test test" + boost::lexical_cast<std::string>(i));
        ret = batchClient3.append("test test test test test test test test" + boost::lexical_cast<std::string>(i));
        ret = batchClientPtr->append("test test test test test test test test" + boost::lexical_cast<std::string>(i));
        cout << "the ret is " << ret << endl;


    }

}
