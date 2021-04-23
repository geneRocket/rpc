package cluster;

import common.RPCRequest;
import config.GlobalConfig;
import protocol.Invoker;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomLoadBalancer extends AbstractLoadBalancer{


    @Override
    public Invoker select(List<Invoker> invokers, RPCRequest request) {
        if (invokers.size() == 0) {
            return null;
        }
        return invokers.get(ThreadLocalRandom.current().nextInt(invokers.size()));
    }
}
