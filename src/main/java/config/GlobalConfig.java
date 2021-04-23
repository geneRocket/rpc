package config;

import cluster.LoadBalancer;
import cluster.RandomLoadBalancer;
import lombok.Getter;
import lombok.Setter;
import protocol.Protocol;
import serialize.Serializer;

@Getter
public class GlobalConfig {
    ClusterConfig clusterConfig;
    RegistryConfig registryConfig;
    ApplicationConfig applicationConfig;
    ProtocolConfig protocolConfig;

    public static GlobalConfig globalConfig = new GlobalConfig();
    static {
        globalConfig.clusterConfig=new ClusterConfig();

        globalConfig.registryConfig=new RegistryConfig();


        globalConfig.applicationConfig=new ApplicationConfig();
        globalConfig.protocolConfig=new ProtocolConfig();

        globalConfig.registryConfig.getRegistryInstance().init();
    }

    public LoadBalancer getLoadBalancer() {
        return clusterConfig.getLoadBalanceInstance();
    }

    public Protocol getProtocol() {
        return protocolConfig.getProtocolInstance();
    }

    public Serializer getSerializer() {
        return applicationConfig.getSerializerInstance();
    }

}
