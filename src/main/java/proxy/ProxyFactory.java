package proxy;

import common.Request;
import common.Response;
import protocol.Invoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

public class ProxyFactory {

    static public <T> T createProxy(Invoker<T> invoker){
        return (T) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class<?>[]{invoker.getInterface()}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Request request=new Request();
                request.setUuid(UUID.randomUUID());
                request.setInterfaceName(invoker.getInterfaceName());
                request.setMethodName(method.getName());
                request.setArgs(args);
                Response response= invoker.invoke(request);
                return response.getResult();
            }
        });
    }

    static public <T> Invoker<T> createInvoker(String interfaceName,Class<T> interfaceClass,T obj){
        return new Invoker<T>() {
            @Override
            public Class<T> getInterface() {
                return interfaceClass;
            }

            @Override
            public String getInterfaceName() {
                return interfaceName;
            }

            @Override
            public Response invoke(Request request) {
                Response response=new Response();
                response.setUuid(request.getUuid());
                try {
                    Method method=obj.getClass().getMethod(request.getMethodName());
                    Object result = method.invoke(obj,request.getArgs());
                    response.setResult(result);
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                return response;
            }
        };
    }
}
