package registry;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZkClient {
    ZooKeeper zooKeeper;

    public ZkClient() throws IOException, InterruptedException {
        CountDownLatch countDownLatch=new CountDownLatch(1);
        Watcher watcher=new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    if(event.getType()==Event.EventType.None){
                        countDownLatch.countDown();
                    }
                }
            }
        };
        zooKeeper=new ZooKeeper("127.0.0.1", 5000 ,watcher);
        countDownLatch.await();
    }

    public boolean pathExisted(String path) throws InterruptedException, KeeperException {
        return zooKeeper.exists(path,false)!=null;
    }

    public void createPath(String path) throws InterruptedException, KeeperException {
        zooKeeper.create(path,null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    public List<String> getChildren(String path,Watcher watcher) throws InterruptedException, KeeperException {
        return zooKeeper.getChildren(path,watcher);
    }

    public byte[] getData(String path,Watcher watcher) throws InterruptedException, KeeperException {
        return zooKeeper.getData(path,watcher,null);
    }

    public void createNode(String path,String data) throws InterruptedException, KeeperException {
        zooKeeper.create(path+"/"+data,data.getBytes(StandardCharsets.UTF_8),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
    }


}
