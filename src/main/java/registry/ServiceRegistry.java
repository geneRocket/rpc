package registry;

public interface ServiceRegistry {
    void init();
    void discover(String interfaceName, ServiceURLRemovalCallback callback, ServiceURLAddOrUpdateCallback serviceURLAddOrUpdateCallback);
    void register(String address,String interfaceName);
}
