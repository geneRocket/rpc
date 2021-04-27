package cluster;

import common.Request;
import common.Response;
import config.GlobalConfig;
import config.Refer;
import protocol.Invoker;
import protocol.Protocol;
import protocol.ProtocolInvoker;
import registry.Registry;
import registry.ServerChanged;

import java.sql.Ref;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ClusterInvoker<T> implements Invoker<T> {
    String interfaceName;
    Class<T> interfaceClass;
    Registry registry;
    Protocol protocol;

    ConcurrentHashMap<String,Invoker<T>> invokers;


    public ClusterInvoker(String interfaceName,Class<T> interfaceClass,Registry registry,Protocol protocol){
        this.interfaceName=interfaceName;
        this.interfaceClass=interfaceClass;
        this.registry=registry;
        this.protocol=protocol;


        invokers=new ConcurrentHashMap<>();
        registry.discover(interfaceName, new ServerChanged() {
            @Override
            public void serverChanged(List<String> address_list) {
                for(String address:address_list){
                    if(invokers.containsKey(address))
                        continue;
                    Invoker<T> invoker= new ProtocolInvoker<T>(address,interfaceName,interfaceClass,protocol);
                    invokers.put(address,invoker);
                }

                for(String address:invokers.keySet()){
                    if (!address_list.contains(address)){
                        //删除
                        invokers.remove(address);
                    }
                }
            }
        });
    }

    @Override
    public Class<T> getInterface() {
        return interfaceClass;
    }

    @Override
    public String getInterfaceName() {
        return interfaceName;
    }

    Invoker<T> select(ArrayList<Invoker<T>> invokers){
        return invokers.get(new Random().nextInt(invokers.size()));
    }

    @Override
    public Response invoke(Request request) {
        if(invokers.size()==0){
            throw new RuntimeException("no invoker");
        }

        Invoker<T> invoker=select(new ArrayList<>(invokers.values()));
        return invoker.invoke(request);
    }


}
