#include "src/client/xlog_appender.h"
#include "src/client/client.h"
#include <boost/lexical_cast.hpp>
#include <Ice/BuiltinSequences.h>
#include <IceUtil/Thread.h>

using namespace std;
class TestThread : public IceUtil::Thread{

    public:
        TestThread(char* name, int count);
        virtual void run();
    private:
        char* _name;
        int _count;

};

TestThread::TestThread(char* name, int count){
    _name = name;
    _count = count;
}

void TestThread::run(){

    Ice::StringSeq categories;
    categories.push_back("test");
    categories.push_back("www");
    xlog::XLogAppender batchClient(categories, "xlog.conf"); 

    for(int i = 0; i < _count; i++){
        std::cout << "thread: " << _name << ", count: " << i << " " << string(_name) + boost::lexical_cast<std::string>(i)<< std::endl;
        int ret = batchClient.append(string(_name) + boost::lexical_cast<std::string>(i));
        //cout << "the ret is " << ret << endl;
    }
}


int main(int argc, char** argv){
    int count = 10;
    ++argv;
    if(argc > 1 ){
        count =  boost::lexical_cast< int >(*argv);
    }

    string name("test_thread");
    for ( int i =0; i < 10; i++){
        string a = name + boost::lexical_cast<std::string>(i);
        char* cstr = new char[a.size() + 1];
        strcpy (cstr, a.c_str());
        IceUtil::ThreadPtr tp = new TestThread(cstr, count);
        IceUtil::ThreadControl tc = tp->start();
        tc.detach();
        //tc.join();
        cout << a.c_str() << endl;
    }
    sleep(2000);

    return 0;


}
