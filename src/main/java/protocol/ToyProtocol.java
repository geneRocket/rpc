package protocol;

import config.GlobalConfig;
import config.ReferenceConfig;
import config.ServiceConfig;
import registry.ServiceURL;
import transport.Client;
import transport.Server;
import transport.ToyClient;
import transport.ToyServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ToyProtocol implements Protocol {

    private Map<String, Client> clients = new ConcurrentHashMap<>();
//    private GlobalConfig globalConfig;
    private Server server;

    private Map<String, Exporter<?>> exporters = new ConcurrentHashMap<>();


    @Override
    public <T> Invoker<T> refer(ReferenceConfig<T> referenceConfig, ServiceURL serviceURL)  {
        ToyInvoker<T> invoker = new ToyInvoker<>();
        invoker.setInterfaceClass(referenceConfig.getInterfaceClass());
        invoker.setInterfaceName(referenceConfig.getInterfaceName());
        invoker.setClient(initClient(serviceURL));
        return invoker;
    }

    @Override
    public <T> ServiceConfig<T> referLocalService(String interfaceMame) {
        return (ServiceConfig<T>) exporters.get(interfaceMame).getServiceConfig();

    }

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker, ServiceConfig<T> serviceConfig)  {
        ToyExporter<T> exporter = new ToyExporter<>();
        exporter.setInvoker(invoker);
        exporter.setServiceConfig(serviceConfig);
        putExporter(invoker.getInterface(), exporter);
        openServer();
        // export

        try {
            serviceConfig.getRegistryInstance().register(InetAddress.getLocalHost().getHostAddress() + ":" + "8000", serviceConfig.getInterfaceName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return exporter;
    }

    protected synchronized final void openServer() {
        if(server == null) {
            server = doOpenServer();
        }
    }

    protected Server doOpenServer() {
        ToyServer toyServer = new ToyServer();
        toyServer.run();
        return toyServer;
    }

    protected void putExporter(Class<?> interfaceClass, Exporter<?> exporter) {

        this.exporters.put(interfaceClass.getName(), exporter);
    }

    public final Client initClient(ServiceURL serviceURL) {
        String address = serviceURL.getAddress();

        if (clients.containsKey(address)) {
            return clients.get(address);
        }
        Client client = doInitClient(serviceURL);
        clients.put(address, client);
        return client;

    }

    protected Client doInitClient(ServiceURL serviceURL) {
        ToyClient toyClient = new ToyClient();
        toyClient.init(serviceURL);
        return toyClient;
    }

    public final void closeEndpoint(String address) {
        clients.remove(address);
    }

    public final void updateEndpointConfig(ServiceURL serviceURL) {
        if (!clients.containsKey(serviceURL.getAddress())) {
            throw new RuntimeException( "无法找到该地址");
        }
        clients.get(serviceURL.getAddress()).updateServiceConfig(serviceURL);
    }
}
