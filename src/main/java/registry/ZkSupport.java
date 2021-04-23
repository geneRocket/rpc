package registry;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZkSupport {
    ZooKeeper zookeeper = null;
    CountDownLatch connectedSemaphore = new CountDownLatch(1);
    static final int ZK_SESSION_TIMEOUT = 5000;

    public void connect(String address) {
        try {
            this.zookeeper = new ZooKeeper(address, ZK_SESSION_TIMEOUT, (WatchedEvent event) -> {
                //获取事件的状态
                Watcher.Event.KeeperState keeperState = event.getState();
                Watcher.Event.EventType eventType = event.getType();
                //如果是建立连接
                if (Watcher.Event.KeeperState.SyncConnected == keeperState) {
                    if (Watcher.Event.EventType.None == eventType) {
                        //如果建立连接成功，则发送信号量，让后续阻塞程序向下执行
                        connectedSemaphore.countDown();
                    }
                }
            });
            connectedSemaphore.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getChildren(final String path, Watcher watcher) throws KeeperException, InterruptedException {
        return zookeeper.getChildren(path, watcher);
    }

    public byte[] getData(String path, Watcher watcher) throws KeeperException, InterruptedException {
        return zookeeper.getData(path, watcher, null);
    }

    public void createPathIfAbsent(String path, CreateMode createMode) throws KeeperException, InterruptedException {
        String[] split = path.split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            if (!split[i].equals("")) {
                sb.append(split[i]);
                Stat s = zookeeper.exists(sb.toString(), false);
                if (s == null) {
                    zookeeper.create(sb.toString(), new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
                }
            }
            if (i < split.length - 1) {
                sb.append("/");
            }
        }
    }

    public void createNodeIfAbsent(String data, String path) {
        try {
            byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            zookeeper.create(path + "/" + data, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } catch (KeeperException e) {
            if (e instanceof KeeperException.NodeExistsException) {
                throw new RuntimeException("ZK路径已经存在 : 建议重启解决");
            } else {
                e.printStackTrace();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
