package protocol;

import common.RPCRequest;
import config.ReferenceConfig;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InvokeParam {
    RPCRequest rpcRequest;
    ReferenceConfig referenceConfig;
}
