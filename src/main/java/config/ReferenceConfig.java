package config;

import common.RPCRequest;
import common.RPCResponse;
import lombok.Builder;
import lombok.Getter;
import protocol.InvokeParam;
import protocol.Invoker;


import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Builder
@Getter
public class ReferenceConfig <T> {
    private String interfaceName;
    private Class<T> interfaceClass;
    private volatile Invoker<T> invoker;
    private volatile T ref;
    private volatile boolean initialized;
    private boolean isGeneric;

    private static final Map<String, ReferenceConfig<?>> CACHE = new ConcurrentHashMap<>();

    public static <T> ReferenceConfig<T> createReferenceConfig(String interfaceName,
                                                               Class<T> interfaceClass,
                                                               boolean isGeneric) {
        if (CACHE.containsKey(interfaceName)){
            return (ReferenceConfig<T>) CACHE.get(interfaceName);
        }

        ReferenceConfig config = ReferenceConfig.builder()
                .interfaceName(interfaceName)
                .interfaceClass((Class<Object>) interfaceClass)
                .isGeneric(isGeneric)
                .build();
        CACHE.put(interfaceName, config);
        return config;
    }

    private void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        // ClusterInvoker
        invoker = GlobalConfig.globalConfig.getClusterConfig().getLoadBalanceInstance().referCluster(this);
        if (!isGeneric) {
            ref = GlobalConfig.globalConfig.getApplicationConfig().getProxyFactoryInstance().createProxy(invoker);
        }
    }

    public Object invokeForGeneric(String methodName, Object[] args) {
        if (!initialized) {
            init();
        }
        if (isGeneric) {
            RPCRequest request = new RPCRequest();
            request.setRequestId(UUID.randomUUID().toString());
            request.setInterfaceName(interfaceName);
            request.setMethodName(methodName);
            request.setParameters(args);
            // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应
            // ClusterInvoker
            InvokeParam invokeParam = InvokeParam.builder()
                    .rpcRequest(request)
                    .referenceConfig(this)
                    .build();
            RPCResponse response = invoker.invoke(invokeParam);
            if (response == null) {
                // callback,oneway,async
                return null;
            } else {
                return response.getResult();
            }
        } else {
            throw new RuntimeException("只有泛化调用的refernce才可以调用invoke方法");
        }
    }

    public T get() {
        if (!initialized) {
            init();
        }
        return ref;
    }

    public static ReferenceConfig getReferenceConfigByInterfaceName(String interfaceName) {
        return CACHE.get(interfaceName);
    }
}
