package cluster;

import common.RPCRequest;
import config.ReferenceConfig;
import protocol.Invoker;

import java.util.List;

public interface LoadBalancer {
    Invoker select(List<Invoker> invokers, RPCRequest request);
    <T> Invoker<T> referCluster(ReferenceConfig<T> referenceConfig);
}
