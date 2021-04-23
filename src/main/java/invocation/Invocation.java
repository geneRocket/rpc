package invocation;

import common.RPCRequest;
import common.RPCResponse;
import protocol.InvokeParam;

import java.util.concurrent.Future;
import java.util.function.Function;

public interface Invocation {
    RPCResponse invoke(InvokeParam invokeParam, Function<RPCRequest, Future<RPCResponse>> requestProcessor);
}
