package transport;

import common.RPCRequest;
import common.RPCResponse;
import common.RPCThreadSharedContext;
import config.GlobalConfig;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import registry.ServiceURL;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static transport.FrameConstant.*;

public class ToyClient implements Client{
    private ServiceURL serviceURL;
    private volatile boolean initialized = false;
    EventLoopGroup group;
    Bootstrap bootstrap;
    volatile Channel futureChannel;

    public void init(ServiceURL serviceURL) {
        this.serviceURL = serviceURL;
        // 初始化的时候建立连接，才能检测到服务器是否可用
        connect();
    }

    void connect() {
        if (initialized) {
            return;
        }
        this.group = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(group).channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                .handler(initPipeline())
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);
        try {
            doConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ChannelInitializer initPipeline() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline()
                        .addLast("IdleStateHandler", new IdleStateHandler(0, ToyConstant.HEART_BEAT_TIME_OUT, 0))
                        // ByteBuf -> Message
                        .addLast("LengthFieldPrepender", new LengthFieldPrepender(LENGTH_FIELD_LENGTH, LENGTH_ADJUSTMENT))
                        // Message -> ByteBuf
                        .addLast("ToyEncoder", new ToyEncoder(GlobalConfig.globalConfig.getSerializer()))
                        // ByteBuf -> Message
                        .addLast("LengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH, LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP))
                        // Message -> Message
                        .addLast("ToyDecoder", new ToyDecoder(GlobalConfig.globalConfig.getSerializer()))

                        .addLast("ToyClientHandler", new ToyClientHandler(ToyClient.this));
            }
        };
    }

    void doConnect() throws InterruptedException {
        ChannelFuture future;
        String address = serviceURL.getAddress();
        String host = address.split(":")[0];
        Integer port = Integer.parseInt(address.split(":")[1]);
        future = bootstrap.connect(host, port).sync();
        this.futureChannel = future.channel();
        initialized = true;
    }

    @Override
    public Future<RPCResponse> submit(RPCRequest request) {
        if (!initialized) {
            connect();
        }
        CompletableFuture<RPCResponse> responseFuture = new CompletableFuture<>();
        RPCThreadSharedContext.registerResponseFuture(request.getRequestId(), responseFuture);
        Object data = Message.buildRequest(request);
        this.futureChannel.writeAndFlush(data);
        return responseFuture;
    }

    @Override
    public void handleRPCResponse(RPCResponse response) {
        CompletableFuture<RPCResponse> future = RPCThreadSharedContext.getAndRemoveResponseFuture(response.getRequestId());
        future.complete(response);
    }

    public void updateServiceConfig(ServiceURL serviceURL) {
        this.serviceURL = serviceURL;
    }
}
