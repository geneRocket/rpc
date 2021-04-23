package protocol;

import config.ReferenceConfig;
import config.ServiceConfig;
import registry.ServiceURL;

public interface Protocol {

    <T> Invoker<T> refer(ReferenceConfig<T> referenceConfig, ServiceURL serviceURL);

    void closeEndpoint(String address);

    void updateEndpointConfig(ServiceURL serviceURL);

    <T> ServiceConfig<T> referLocalService(String interfaceMame) ;

    <T> Exporter<T> export(Invoker<T> invoker, ServiceConfig<T> serviceConfig) ;


}
