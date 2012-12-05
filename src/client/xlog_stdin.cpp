#include "src/client/xlog_appender.h"
#include "src/client/xlog_properties.h"
#include "src/client/client.h"
#include <boost/lexical_cast.hpp>
#include <boost/algorithm/string.hpp>
#include <vector>
#include <string>
#include <map>

using namespace std;
using namespace boost::algorithm;

void help(int = 0);
class CliParser{
    public:
        CliParser(int argc1, char** argv1);
        int parse();
        string getOption(string const& key);
    private:
        int argc;
        char** argv;
        map<string, string> configMap;
};


int main(int argc , char** argv)
{
    if(argc == 1){
        help(1);
    }
    using namespace xlog;
    XLogAppender* batchClient;
    vector<string> categories;
    char* c = *(argv + argc -1);
    string cstr(c, strlen(c));
    boost::algorithm::split(categories, cstr, boost::is_any_of("."));
    if(categories.size() <= 1){
        std::cerr << "the categories error, separated by colon(.)" << endl;
        help(2);
    }
    ++argv;
    if(argc > 2){
        CliParser parser(argc - 2, argv);
        try{
            parser.parse();
        }catch(...){
            cerr << "parse parameters error! " << endl;
            help(3);
        }
        string file = parser.getOption("file");
        if(file != ""){
            batchClient = new XLogAppender(categories, file.c_str()); 
        }else{
            int mss = (parser.getOption("mss") == "") ? xlog::XLogProperties::MAX_SEND_SIZE : boost::lexical_cast<int>(parser.getOption("mss"));
            int mqs = (parser.getOption("mqs") == "") ? xlog::XLogProperties::MAX_QUEUE_SIZE : boost::lexical_cast<int>(parser.getOption("mqs"));
            string hosts = (parser.getOption("hosts") == "" ) ? xlog::XLogProperties::DEFAULT_HOSTS : parser.getOption("hosts");
            bool is_udp_protocol = (parser.getOption("protocol") != "" && parser.getOption("protocol") == "tcp") ? false : true;
            batchClient = new XLogAppender(categories, is_udp_protocol, mss, mqs, hosts); 
        }
    }else{
        batchClient = new XLogAppender(categories, "/etc/xlog_client.conf"); 
    }
    std::string input_line;
    while(cin) {
        getline(cin, input_line);
        boost::algorithm::trim(input_line);
        if(!input_line.empty()){
            batchClient->append(input_line);
        }
    };
    sleep(10); // s
    cout << "xlog cli end" << endl;
}

void help(int exitcode ){
    cerr << "the exit code: " << exitcode << endl;
    cout << "using : command [options ] categories: " << endl;
    cout << "command options: " << endl;
    cout << "   --hosts|-h:         the xlog hosts, the default is " << xlog::XLogProperties::DEFAULT_HOSTS << endl;
    cout << "   --mss:              the maxSendSize, the default diable the batch send size(0)" << endl;
    cout << "   --mqs:              the maxQueueSize, the default is (100000)" << endl;
    cout << "   --tcp|--udp:        the communication protocol, the default is (udp)" << endl;
    cout << "   --file|-f:          the config_file, execlude other options, the default is (/etc/xlog_client.conf)" << endl;
    cout << "   --help|-h:          help manual" << endl;
    exit(exitcode);
}


CliParser::CliParser(int argc1, char** argv1){
    argc = argc1;
    argv = argv1;
}

string CliParser::getOption(string const& key){
    map<string, string>::iterator it = configMap.find(key);
    if(it != configMap.end()){
        return (*it).second;
    }
    return "";

}
int CliParser::parse(){
    int cnt = argc;
    for(char** cursor = argv; cursor < argv + argc ; cursor++){
        char* arg = *cursor;
        string key(arg, strlen(arg));
        if(key.compare("--hosts")== 0  || key.compare("-h") == 0){
            string value(*(++cursor));
            configMap.insert(pair<string, string>("hosts", value));
            cnt = cnt -2;
        }else if(key.compare("--mss")== 0){
            string value(*(++cursor));
            configMap.insert(pair<string, string>("mss", value));
            cnt = cnt -2;
        }else if(key.compare("--mqs")== 0){
            string value(*(++cursor));
            configMap.insert(pair<string, string>("mqs", value));
            cnt = cnt -2;
        }else if(key.compare("--udp")== 0 || key.compare("-u") ==0 || key.compare("--tcp")== 0 || key.compare("-t") ==0){
            if(configMap.find("protocol") != configMap.end()){
                help(100);
            }
            if(key.compare("--tcp")== 0 || key.compare("-t") ==0){
                configMap.insert(pair< string, string>("protocol", "tcp"));
            }else{
                configMap.insert(pair< string, string>("protocol", "udp"));
            }
            cnt--;
        }else if(key.compare("--file")== 0 || key.compare("-f") ==0){
            if(argc != 2){
                help(101);
            }
            string value(*(++cursor));
            configMap.insert(pair< string, string>("file", value));
            cnt = cnt -2;
        }else if(key.compare("--help")== 0 || key.compare("-H") ==0){
            help();
        }else{
            help(103);
        }
    }
    return 0;
}
