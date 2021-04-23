package transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.concurrent.atomic.AtomicInteger;

public class ToyClientHandler extends SimpleChannelInboundHandler<Message> {
    private Client client;
    private AtomicInteger timeoutCount = new AtomicInteger(0);

    public ToyClientHandler(Client client){
        this.client=client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        timeoutCount.set(0);
        //服务器不会PING客户端
        if (message.getType() == Message.RESPONSE) {
            client.handleRPCResponse(message.getResponse());
        }
    }

}
