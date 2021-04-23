package config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import proxy.RPCProxyFactory;
import serialize.JsonSerializer;
import serialize.Serializer;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationConfig {
    private Serializer serializerInstance=new JsonSerializer();
    private RPCProxyFactory proxyFactoryInstance=new RPCProxyFactory();

}
