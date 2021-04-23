package config;

import lombok.Getter;
import lombok.Setter;
import registry.ServiceRegistry;
import registry.ZkServiceRegistry;

@Getter
@Setter
public class RegistryConfig {
    private String address="127.0.0.1";
    private ServiceRegistry registryInstance=new ZkServiceRegistry();
}
