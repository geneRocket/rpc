package invocation;

import common.RPCRequest;
import common.RPCResponse;
import config.ReferenceConfig;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

public class SyncInvocation extends AbstractInvocation {
    @Override
    protected RPCResponse doInvoke(RPCRequest rpcRequest, ReferenceConfig referenceConfig, Function<RPCRequest, Future<RPCResponse>> requestProcessor) {
        Future<RPCResponse> future = requestProcessor.apply(rpcRequest);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

}
