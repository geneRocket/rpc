package protocol;

import config.ServiceConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToyExporter<T> implements Exporter{
    protected Invoker<T> invoker;
    protected ServiceConfig<T> serviceConfig;
}
