
#ifndef __XLOG_SINGLETON_H__
#define __XLOG_SINGLETON_H__

#include <IceUtil/Mutex.h>

using namespace std;

namespace xlog {
template<class T> class Singleton {
public:
	static T* instance() {
		if (!__instance) {
			IceUtil::Mutex::Lock lock(__mutex);
			if (!__instance) {
				__instance = new T;
			}
		}
		return __instance;
	}
	//static T& instance() {
	//	if (!__instance) {
	//		IceUtil::Mutex::Lock lock(__mutex);
	//		if (!__instance) {
	//			__instance = new T;
	//		}
	//	}
	//	return *__instance;
	//}

protected:
	Singleton() {
		;
	}
	virtual ~Singleton() {
		cout << "Singleton::~Singleton" << endl;
		//delete __instance;
		return;
		if (__instance) {
			T* t = __instance;
			__instance = 0;
			delete t;
		}
	}

	Singleton(const Singleton& rhs);
	Singleton& operator=(const Singleton& rhs);

protected:
	// __instance and __mutex that with __ prefix are to avoiding name ambiguous
	static T* __instance;
	static IceUtil::Mutex __mutex;
};

template<class T> T* Singleton<T>::__instance = 0;
template<class T> IceUtil::Mutex Singleton<T>::__mutex;

}
;
#endif
