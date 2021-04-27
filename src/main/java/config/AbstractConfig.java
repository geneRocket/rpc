package config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AbstractConfig<T> {
    String interfaceName;
    Class<T> interfaceClass;
}
