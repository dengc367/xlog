#include "src/rsyslog/xlog_rsyslog.h"
#include <cstring>
#include <iostream>
#include "src/client/xlog_appender.h"
#include <boost/lexical_cast.hpp>
#include <boost/algorithm/string.hpp>

using namespace std;

vector<string> categories;
xlog::XLogAppender* xlogclient;
char conf_file_path[] = "/etc/rsyslog-xlog.conf";

int init_xlog(char* const catStr){
    boost::algorithm::split(categories, catStr, boost::is_any_of("."));
    xlogclient = new xlog::XLogAppender(categories, conf_file_path); 
    //cout << "xlog rsyslog inited" << endl;
    return 0;
}

int send_to_xlog(char* const msg, int const len ){	
	//cout << "xlog_cpp" << msg << endl;
    int ret = xlogclient->append(msg, len);
	//cout << "xlog_cpp" << msg << "is OK: " << ret << endl;
    return ret;
}

int close_xlog(){
    categories.clear();
    xlogclient = NULL;
}
