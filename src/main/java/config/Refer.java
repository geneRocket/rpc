package config;

import protocol.Invoker;
import proxy.ProxyFactory;

public class Refer<T> extends AbstractConfig<T>{
    GlobalConfig<T> globalConfig=new GlobalConfig<>();
    Invoker<T> invoker;
    T ref;

    public Refer(String interfaceName, Class<T> classs) {
        super(interfaceName, classs);
        init();
    }

    void init(){
        invoker= globalConfig.getCluster().referCluster(interfaceName, interfaceClass, globalConfig.getRegistry(), globalConfig.getProtocol());
        ref= ProxyFactory.createProxy(invoker);
    }

    public T get(){
        return ref;
    }


}
