package cluster;

import config.GlobalConfig;
import config.Refer;
import protocol.Invoker;
import protocol.Protocol;
import registry.Registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Cluster<T> {
    public Invoker<T> referCluster(String interfaceName, Class<T> interfaceClass, Registry registry, Protocol protocol){
        return new ClusterInvoker<>(interfaceName, interfaceClass, registry,protocol);
    }



}
