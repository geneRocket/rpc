package common;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RPCRequest {
    String requestId;
    String interfaceName;
    String methodName;
    Object[] parameters;
}
