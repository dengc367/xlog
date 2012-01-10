#include <src/common/ZooKeeperListener.h>
#include <src/common/ZkManager.h>

namespace xlog
{

/**
 * 转换zookeeper watcher收到的state信息
 */
const char* state2String(int state)
{
    switch (state)
    {
    case 0:
        return "ZOO_CLOSED_STATE";
    case 1:
        return "ZOO_CONNECTING_STATE";
    case 2:
        return "ZOO_ASSOCIATING_STATE";
    case 3:
        return "ZOO_CONNECTED_STATE";
    case -112:
        return "ZOO_EXPIRED_SESSION_STATE";
    case -113:
        return "ZOO_AUTH_FAILED_STATE";
    }
    return "INVALID_STATE";
}

/**
 * 转换zookeeper watcher收到的event信息
 */
const char* watcherEvent2String(const int ev)
{
    switch (ev)
    {
    case 0:
        return "ZOO_ERROR_EVENT";
    case 1:
        return "ZOO_CREATED_EVENT";
    case 2:
        return "ZOO_DELETED_EVENT";
    case 3:
        return "ZOO_CHANGED_EVENT";
    case 4:
        return "ZOO_CHILD_EVENT";
    case -1:
        return "ZOO_SESSION_EVENT";
    case -2:
        return "ZOO_NOTWATCHING_EVENT";
    }
    return "INVALID_EVENT";
}

/**
 * 设置全局的ZkManager，只能在进程初始化时调用一次
 */
void setZkManager(const ZkManagerPtr& zm)
{
    zm__ = zm;
}

/**
 * zookeeper watcher 用于接收zookeeper发送的信息
 */
void ZkWatcher(zhandle_t *zzh, int type, int state, const char *path, void *watcherCtx)
{
    static bool fromExpired = false;

    if (type == ZOO_SESSION_EVENT)
    {
        if (state == ZOO_CONNECTED_STATE)
        {
            //如果是从expired状态到connected状态，通知更新zookeeper数据
            //否则通知连接已经创建
            if (fromExpired)
            {
                fromExpired = false;
                zm__->notifyChange();
            }
            else
            {
                zm__->notifyConnected();
            }
            return;
        }

        //zookeeper连接expired之后，原来的连接失效，需要重新建立新的连接
        if (state == ZOO_EXPIRED_SESSION_STATE)
        {
            fromExpired = true;
            zm__->reInit();
            return;
        }
    }
    else
    {
        zm__->notifyChange();
    }
}

ZkManager::ZkManager()
{
    zkAddress_ = "";

    zhandle_t* zh_ = NULL;
}

void ZkManager::addListener(const ZooKeeperListenerPtr& listener)
{
    listeners_.push_back(listener);
}

bool ZkManager::createEphemeralNode(const std::string& path)
{
    ::IceUtil::Monitor<::IceUtil::Mutex>::Lock lock(zhMonitor_);

    if (zh_)
    {
        int rc = zoo_create(zh_, path.c_str(), "", 0, &ZOO_OPEN_ACL_UNSAFE, 0, 0, 0);

        return rc == ZOK;
    }

    return false;
}

std::vector<std::string> ZkManager::getChildren(const std::string& path)
{
    ::IceUtil::Monitor<::IceUtil::Mutex>::Lock lock(zhMonitor_);

    std::vector<std::string> res;

    if (zh_)
    {
        struct String_vector nodes;

        int rc = zoo_get_children(zh_, path.c_str(), 0, &nodes);

        if (rc != ZOK)
        {
            std::cerr << "ZkManager::getChildren failed for path " << path << "!" << std::endl;
            return res;
        }

        for (int i = 0; i < nodes.count; ++i)
        {
            res.push_back(nodes.data[i]);
        }
    }

    return res;
}

bool ZkManager::init(const std::string& zkAddress)
{
    if (zkAddress == "")
    {
        std::cerr << "ZkManager::init failed, because the zk address is null!" << std::endl;
        return false;
    }

    zkAddress_ = zkAddress;

    ::IceUtil::Monitor<::IceUtil::Mutex>::Lock lock(zhMonitor_);

    zh_ = zookeeper_init(zkAddress_.c_str(), xlog::ZkWatcher, 1000, 0, 0, 0);

    if (!zh_)
    {
        std::cerr << "ZkManager::init failed, because zookeeper_init return a null pointer!"
                << std::endl;
        return false;
    }

    //因为zookeeper建立连接是异步的，所以在这里等待zookeeper连接成功
    zhMonitor_.wait();

    return true;
}

void ZkManager::notifyConnected()
{
    ::IceUtil::Monitor<::IceUtil::Mutex>::Lock lock(zhMonitor_);
    zhMonitor_.notify();
}

void ZkManager::reInit()
{
    while (true)
    {
        ::IceUtil::Monitor<::IceUtil::Mutex>::Lock lock(zhMonitor_);

        if (zh_)
        {
            zookeeper_close(zh_);
        }

        zh_ = zookeeper_init(zkAddress_.c_str(), xlog::ZkWatcher, 1000, 0, 0, 0);

        if (!zh_)
        {
            //如果创建失败，则过5秒再进行创建，不重复连接是为了降低性能开销，防止出现恶性循环
            std::cerr << "ZkManager::reInit failed, we will try after 5 seconds!" << std::endl;
            sleep(5);
            continue;
        }

        break;
    }
}

void ZkManager::notifyChange()
{
    for (std::vector<ZooKeeperListenerPtr>::const_iterator it = listeners_.begin();
            it != listeners_.end(); ++it)
    {
        (*it)->handle();
    }
}

}
