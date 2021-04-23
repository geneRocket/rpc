package cluster;

import common.RPCResponse;
import config.GlobalConfig;
import config.ReferenceConfig;
import lombok.AllArgsConstructor;
import protocol.InvokeParam;
import protocol.Invoker;
import protocol.Protocol;
import registry.ServiceURL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class ClusterInvoker<T> implements Invoker<T> {
    Map<String, Invoker<T>> addressInvokers = new ConcurrentHashMap<>();

    private Class<T> interfaceClass;
    private String interfaceName;

    public ClusterInvoker(Class<T> interfaceClass, String interfaceName) {
        this.interfaceClass = interfaceClass;
        this.interfaceName = interfaceName;
        init();
    }

    Invoker doSelect(List<Invoker> availableInvokers, InvokeParam invokeParam) {
        Invoker invoker;
        if (availableInvokers.size() == 0) {
            throw new RuntimeException("未找到可用服务器");
        } else if (availableInvokers.size() == 1) {
            invoker = availableInvokers.get(0);
        } else {
            invoker = GlobalConfig.globalConfig.getLoadBalancer().select(availableInvokers, invokeParam.getRpcRequest());
        }
        return invoker;
    }

    void init() {
        GlobalConfig.globalConfig.getRegistryConfig().getRegistryInstance().discover(interfaceName,
                (newServiceURLs -> removeNotExisted(newServiceURLs)),
                (serviceURL -> addOrUpdate(serviceURL)));
    }

    @Override
    public Class<T> getInterface() {
        return interfaceClass;
    }

    @Override
    public RPCResponse invoke(InvokeParam invokeParam) {
        Invoker invoker = doSelect(getInvokers(), invokeParam);
        RPCResponse response = invoker.invoke(invokeParam);
        // response有可能是null，比如callback、oneway和future
        if (response == null) {
            return null;
        }
        return response;
    }

    public List<Invoker> getInvokers() {
        // 拷贝一份返回
        return new ArrayList<>(addressInvokers.values());
    }

    public void removeNotExisted(List<ServiceURL> newServiceURLs) {
        Map<String, ServiceURL> newAddressesMap = newServiceURLs.stream().collect(Collectors.toMap(
                ServiceURL::getAddress, url -> url
        ));

        // 地址少了
        // 说明一个服务器挂掉了或出故障了，我们需要把该服务器对应的所有invoker都关掉。
        for (Iterator<Map.Entry<String, Invoker<T>>> it = addressInvokers.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Invoker<T>> curr = it.next();
            if (!newAddressesMap.containsKey(curr.getKey())) {
                    Protocol protocol =  GlobalConfig.globalConfig.getProtocol();
                    protocol.closeEndpoint(curr.getKey());
                    it.remove();
            }
        }
    }

    private synchronized void addOrUpdate(ServiceURL serviceURL) {

        // 地址多了/更新
        // 更新
        if (addressInvokers.containsKey(serviceURL.getAddress())) {

            // 我们知道只有远程服务才有可能会更新
            // 更新配置与invoker无关，只需要Protocol负责

            Protocol protocol = GlobalConfig.globalConfig.getProtocol();
            protocol.updateEndpointConfig(serviceURL);

        } else {

            // 添加
            // 需要修改
            Invoker invoker = GlobalConfig.globalConfig.getProtocol().refer(ReferenceConfig.getReferenceConfigByInterfaceName(interfaceName), serviceURL);
            // refer拿到的是InvokerDelegate
            addressInvokers.put(serviceURL.getAddress(), invoker);

        }
    }
}
