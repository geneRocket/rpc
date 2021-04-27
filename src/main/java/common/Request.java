package common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Request {
    UUID uuid;
    String interfaceName;
    String methodName;
    Object[] args;
}
