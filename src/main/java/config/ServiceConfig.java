package config;

import lombok.Getter;
import lombok.Setter;
import protocol.Exporter;
import protocol.Invoker;
import registry.ServiceRegistry;
import registry.ZkServiceRegistry;

@Getter
@Setter
public class ServiceConfig<T> {
    private String interfaceName;
    private Class<T> interfaceClass;
    private T ref;
    private Exporter<T> exporter;
    private ServiceRegistry registryInstance=GlobalConfig.globalConfig.getRegistryConfig().getRegistryInstance();

    public void export() {
        Invoker<T> invoker = GlobalConfig.globalConfig.getApplicationConfig().getProxyFactoryInstance().getInvoker(ref, interfaceClass);
        exporter = GlobalConfig.globalConfig.getProtocolConfig().getProtocolInstance().export(invoker, this);
    }

    public ServiceConfig(String interfaceName, Class<T> interfaceClass, T ref){
        this.interfaceName=interfaceName;
        this.interfaceClass=interfaceClass;
        this.ref=ref;
    }
}
