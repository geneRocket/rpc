package transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.concurrent.atomic.AtomicInteger;
import static transport.Message.PING;
import static transport.Message.REQUEST;

public class ToyServerHandler extends SimpleChannelInboundHandler<Message> {
    private Server server;
    private AtomicInteger timeoutCount = new AtomicInteger(0);

    public ToyServerHandler(Server server) {
        this.server = server;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        timeoutCount.set(0);
        if (message.getType() == PING) {
            ctx.writeAndFlush(Message.PONG_MSG);
        } else if (message.getType() == REQUEST) {
            server.handleRPCRequest(message.getRequest(), ctx);
        }

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (timeoutCount.getAndIncrement() >= 3) {
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
