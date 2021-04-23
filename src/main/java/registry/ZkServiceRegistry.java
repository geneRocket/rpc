package registry;


import config.GlobalConfig;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

public class ZkServiceRegistry implements ServiceRegistry{
    ZkSupport zkSupport;
    static final String ZK_REGISTRY_PATH = "/toy";


    public void init() {
        zkSupport = new ZkSupport();
        zkSupport.connect(GlobalConfig.globalConfig.getRegistryConfig().getAddress());

    }

    @Override
    public void discover(String interfaceName, ServiceURLRemovalCallback callback, ServiceURLAddOrUpdateCallback serviceURLAddOrUpdateCallback) {
        // 如果该接口对应的地址不存在，那么watchNode
        watchInterface(interfaceName, callback, serviceURLAddOrUpdateCallback);
    }

    private void watchInterface(String interfaceName, ServiceURLRemovalCallback serviceURLRemovalCallback, ServiceURLAddOrUpdateCallback serviceURLAddOrUpdateCallback) {
        try {
            String path = generatePath(interfaceName);
            List<String> addresses = zkSupport.getChildren(path, event -> {
                if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                    watchInterface(interfaceName, serviceURLRemovalCallback, serviceURLAddOrUpdateCallback);
                }
            });
            List<ServiceURL> dataList = new ArrayList<>();
            for (String node : addresses) {
                dataList.add(watchService(interfaceName, node, serviceURLAddOrUpdateCallback));
            }
            serviceURLRemovalCallback.removeNotExisted(dataList);
        } catch (KeeperException | InterruptedException e) {
            System.out.println(e);
            throw new RuntimeException("ZK故障");
        }
    }

    private static String generatePath(String interfaceName) {
        return ZK_REGISTRY_PATH + "/" + interfaceName;
    }

    private ServiceURL watchService(String interfaceName, String address, ServiceURLAddOrUpdateCallback serviceURLAddOrUpdateCallback) {
        String path = generatePath(interfaceName);
        try {
            byte[] bytes = zkSupport.getData(path + "/" + address, (Watcher) event -> {
                if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
                    watchService(interfaceName, address, serviceURLAddOrUpdateCallback);
                }
            });
            ServiceURL serviceURL = ServiceURL.parse(new String(bytes, StandardCharsets.UTF_8));
            serviceURLAddOrUpdateCallback.addOrUpdate(serviceURL);
            return serviceURL;
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException("ZK故障");
        }
    }

    @Override
    public void register(String address, String interfaceName) {
        String path = generatePath(interfaceName);
        try {
            zkSupport.createPathIfAbsent(path, CreateMode.PERSISTENT);
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException("ZK故障");
        }
        zkSupport.createNodeIfAbsent(address, path);
    }




}
