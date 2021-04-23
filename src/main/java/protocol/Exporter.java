package protocol;

import config.ServiceConfig;

public interface Exporter<T> {
    Invoker<T> getInvoker();
    ServiceConfig<T> getServiceConfig();
}
