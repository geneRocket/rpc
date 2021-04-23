package proxy;

import common.RPCRequest;
import common.RPCResponse;
import protocol.InvokeParam;
import protocol.Invoker;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

public class RPCProxyFactory {
    public <T> T createProxy(Invoker<T> invoker) {
        return (T) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
                new Class<?>[]{invoker.getInterface()},
                (proxy, method, args) -> {
                    RPCRequest request = new RPCRequest();
                    request.setRequestId(UUID.randomUUID().toString());
                    request.setInterfaceName(invoker.getInterface().getName());

                    request.setMethodName(method.getName());
                    request.setParameters(args);

                    InvokeParam invokeParam=new InvokeParam();
                    invokeParam.setRpcRequest(request);
                    RPCResponse response = invoker.invoke(invokeParam);
                    Object result = null;
                    // result == null when callback,oneway,async
                    if (response != null) {
                        result = response.getResult();
                    }
                    return result;
                }
        );
    }

    public <T> Invoker<T> getInvoker(T proxy, Class<T> type) {
        return new Invoker<T>() {
            @Override
            public Class<T> getInterface() {
                return type;
            }

            @Override
            public RPCResponse invoke(InvokeParam invokeParam)  {
                RPCResponse response = new RPCResponse();
                try {
                    Method method = proxy.getClass().getMethod(invokeParam.getRpcRequest().getMethodName());
                    response.setRequestId(invokeParam.getRpcRequest().getRequestId());
                    response.setResult(method.invoke(proxy, invokeParam.getRpcRequest().getParameters()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }
        };
    }
}
