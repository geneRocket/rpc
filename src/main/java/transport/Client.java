package transport;

import common.RPCRequest;
import common.RPCResponse;
import registry.ServiceURL;

import java.util.concurrent.Future;

public interface Client {
    Future<RPCResponse> submit(RPCRequest request);
    void handleRPCResponse(RPCResponse response);
    void updateServiceConfig(ServiceURL serviceURL);

}
