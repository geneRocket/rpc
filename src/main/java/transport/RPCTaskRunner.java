package transport;

import common.RPCRequest;
import common.RPCResponse;
import config.ServiceConfig;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;

public class RPCTaskRunner implements Runnable{
    private ChannelHandlerContext ctx;
    private RPCRequest request;
    ServiceConfig serviceConfig;

    public <T> RPCTaskRunner(ChannelHandlerContext ctx, RPCRequest request, ServiceConfig<T> referLocalService) {
        this.ctx=ctx;
        this.request=request;
        this.serviceConfig=referLocalService;
    }

    @Override
    public void run() {
        RPCResponse response = new RPCResponse();
        response.setRequestId(request.getRequestId());
        try {
            Object result = handle(request);
            response.setResult(result);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        ctx.writeAndFlush(Message.buildResponse(response));
    }

    private Object handle(RPCRequest request) throws Throwable {
        Object serviceBean = serviceConfig.getRef();

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Object[] parameters = request.getParameters();

        Method method = serviceClass.getMethod(methodName);
        method.setAccessible(true);


        return method.invoke(serviceBean, parameters);
    }
}
