package invocation;

import common.RPCRequest;
import common.RPCResponse;
import config.ReferenceConfig;
import protocol.InvokeParam;

import java.util.concurrent.Future;
import java.util.function.Function;

public abstract class AbstractInvocation implements Invocation{
    @Override
    public RPCResponse invoke(InvokeParam invokeParam, Function<RPCRequest, Future<RPCResponse>> requestProcessor)  {
        RPCResponse response;
        ReferenceConfig referenceConfig = invokeParam.getReferenceConfig();
        RPCRequest rpcRequest = invokeParam.getRpcRequest();;
        try {
            response = doInvoke(rpcRequest, referenceConfig,requestProcessor);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException("transport异常");
        }
        return response;
    }

    protected RPCResponse doInvoke(RPCRequest rpcRequest, ReferenceConfig referenceConfig, Function<RPCRequest, Future<RPCResponse>> requestProcessor) {
        return null;
    }
}
