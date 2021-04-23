package protocol;

import common.RPCResponse;

public interface Invoker<T> {
    Class<T> getInterface();
    RPCResponse invoke(InvokeParam invokeParam) ;
}
