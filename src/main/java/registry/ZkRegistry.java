package registry;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ZkRegistry implements Registry{
    String rpc_dir="/my_rpc"; //rpc_dir interfaceName server
    ZkClient zkClient;

    public ZkRegistry()  {
        try {
            zkClient=new ZkClient();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void watchInterfaceServer(String interfaceName, ServerChanged serverChanged)  {
        String path=rpc_dir+"/"+interfaceName;
        Watcher watcher=new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if(event.getType()== Event.EventType.NodeChildrenChanged){//监听到有children改变，重新获取新的children
                    watchInterfaceServer(path,serverChanged);
                }
            }
        };
        List<String> address_list = null;
        try {
            if(zkClient.pathExisted(path))
                address_list= zkClient.getChildren(path,watcher);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }

        //call back
        if(address_list!=null){
            serverChanged.serverChanged(address_list);
        }
    }




    @Override
    public void discover(String interfaceName, ServerChanged serverChanged) {
        watchInterfaceServer(interfaceName,serverChanged);
    }

    @Override
    public void register(String interfaceName, String address) {
        try {
            if(!zkClient.pathExisted(rpc_dir))
                zkClient.createPath(rpc_dir);
            if(!zkClient.pathExisted(rpc_dir+"/"+interfaceName))
                zkClient.createPath(rpc_dir+"/"+interfaceName);
            zkClient.createNode(rpc_dir+"/"+interfaceName,address);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }
}
