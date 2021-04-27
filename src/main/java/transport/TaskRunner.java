package transport;

import common.Request;
import common.Response;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import protocol.Invoker;

@AllArgsConstructor
public class TaskRunner implements Runnable{

    ChannelHandlerContext ctx;
    Request request;
    Invoker<?> invoker;

    @Override
    public void run() {
        //获取暴露接口
        Response result= invoker.invoke(request);
        Response response = new Response();
        response.setUuid(request.getUuid());
        response.setResult(result.getResult());
        ctx.writeAndFlush(response);
    }
}
