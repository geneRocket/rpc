package transport;

import common.RPCRequest;
import io.netty.channel.ChannelHandlerContext;

public interface Server {
    void run();
    void handleRPCRequest(RPCRequest request, ChannelHandlerContext ctx);
}
