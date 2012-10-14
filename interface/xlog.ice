#include <Ice/BuiltinSequences.ice>

module xlog {
module slice {
  struct LogData {
    Ice::StringSeq categories;
    Ice::StringSeq logs;
    string checkSum;
  };
  sequence<LogData> LogDataSeq;

  // pub sub structure
  dictionary<string, string> Str2StrDict;
  struct Subscription{
      string host;
      Ice::StringSeq categories;
      Str2StrDict options;
  };
  //end

  interface Subscriber {
    void notify(Ice::StringSeq config);
  };

  interface Agent {
    void add(LogDataSeq data);
    void addFailedLogData(LogDataSeq data);

    Ice::StringSeq subscribeClient(string prxStr);
    Ice::StringSeq subscribeSubscriber(Ice::StringSeq categories, string prxStr);
  };

  interface Logger {
    void add(LogDataSeq data);
    void addLogData(LogData data);
  };

  interface Dispatcher {
    void add(LogDataSeq data);
    void addLogData(LogData data);
    void createZNode(int slot);
    bool register(Logger* subscriber, int frequence);
    int subscribe(Subscription sub); // pub sub 
    int unsubscribe(Subscription sub); // pub sub 
  };

  //pub sub 
  interface PublisherService{
    int publish(LogData data);
  };

};
};

