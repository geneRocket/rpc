package config;

import cluster.Cluster;
import lombok.Getter;
import protocol.Protocol;
import registry.Registry;
import registry.ZkRegistry;

@Getter
public class GlobalConfig<T> {
    Cluster<T> cluster=new Cluster<>();
    Registry registry=new ZkRegistry();
    Protocol protocol=new Protocol(registry);
}
