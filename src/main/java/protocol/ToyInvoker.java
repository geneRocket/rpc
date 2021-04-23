package protocol;

import common.RPCRequest;
import common.RPCResponse;
import config.GlobalConfig;
import invocation.SyncInvocation;
import lombok.Getter;
import lombok.Setter;
import transport.Client;

import java.util.concurrent.Future;
import java.util.function.Function;

@Setter
@Getter
public class ToyInvoker<T> implements Invoker<T>{
    private Class<T> interfaceClass;
    private String interfaceName;
    private GlobalConfig globalConfig;
    private Client client;

    @Override
    public Class<T> getInterface() {
        return interfaceClass;
    }

    @Override
    public RPCResponse invoke(InvokeParam invokeParam)  {
        Function<RPCRequest, Future<RPCResponse>> logic = getProcessor();

        return new SyncInvocation().invoke(invokeParam,logic);
    }

    Function<RPCRequest, Future<RPCResponse>> getProcessor() {
        return rpcRequest -> getClient().submit(rpcRequest);
    }
}
