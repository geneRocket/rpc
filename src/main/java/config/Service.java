package config;

import protocol.Invoker;
import proxy.ProxyFactory;

public class Service<T> extends AbstractConfig<T>{
    T obj;
    Invoker<T> invoker;
    GlobalConfig<T> globalConfig=new GlobalConfig<>();



    public Service(String interfaceName, Class<T> interfaceClass,T obj) {
        super(interfaceName, interfaceClass);
        this.obj=obj;
    }

    public void export(){
        this.invoker= ProxyFactory.createInvoker(interfaceName,interfaceClass,obj);
        globalConfig.getProtocol().export(invoker);
    }
}
