package registry;

public interface Registry {
    void discover(String interfaceName,ServerChanged serverChanged);
    void register(String interfaceName,String address);
}
