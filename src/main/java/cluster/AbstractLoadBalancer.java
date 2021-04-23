package cluster;

import config.GlobalConfig;
import config.ReferenceConfig;
import protocol.Invoker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractLoadBalancer implements LoadBalancer {
    private Map<String, ClusterInvoker> interfaceInvokers = new ConcurrentHashMap<>();



    @Override
    public <T> Invoker<T> referCluster(ReferenceConfig<T> referenceConfig) {
        String interfaceName = referenceConfig.getInterfaceName();

        ClusterInvoker clusterInvoker;
        if (!interfaceInvokers.containsKey(interfaceName)) {
            clusterInvoker = new ClusterInvoker(referenceConfig.getInterfaceClass(), interfaceName);
            interfaceInvokers.put(interfaceName, clusterInvoker);
            return clusterInvoker;
        }
        return interfaceInvokers.get(interfaceName);
    }
}
