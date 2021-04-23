package common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RPCResponse {
    String requestId;
    Object result;
}
