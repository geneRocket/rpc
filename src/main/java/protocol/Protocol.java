package protocol;

import registry.Registry;
import transport.Client;
import transport.NettyClient;
import transport.NettyServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

public class Protocol {
    static ConcurrentHashMap<String,Client> clients=new ConcurrentHashMap<>();

    NettyServer nettyServer;

    Registry registry;

    public Protocol(Registry registry){
        this.registry=registry;
    }

    public Client getClient(String address){
        if(clients.containsKey(address)){
            return clients.get(address);
        }
        NettyClient client=new NettyClient(address);

        clients.put(address,client);
        return client;
    }


    public <T> void export(Invoker<T> invoker){
        nettyServer=new NettyServer(invoker);


        //registry
        try {
            registry.register(invoker.getInterfaceName(), InetAddress.getLocalHost().getHostAddress() + ":"+"8000");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        nettyServer.run();

    }


}
