package transport;

import common.Request;
import common.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class NettyClient implements Client {

    String address;

    EventLoopGroup bossGroup = new NioEventLoopGroup();

    Bootstrap bootstrap = new Bootstrap();
    Channel channel;

    static ConcurrentHashMap<UUID,CompletableFuture<Response>> futureRecord=new ConcurrentHashMap<>();

    public NettyClient(String address){
        this.address=address;
        init();
    }

    static class ClientHandler extends SimpleChannelInboundHandler<Response> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
            CompletableFuture<Response> future=futureRecord.get(response.getUuid());
            future.complete(response);
        }
    }

    void init() {
        bootstrap.group(bossGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new LengthFieldPrepender(4));
                        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(1000,0,4,0,4));
                        socketChannel.pipeline().addLast(new Encoder());
                        socketChannel.pipeline().addLast(new Decoder(Response.class));
                        socketChannel.pipeline().addLast(new ClientHandler());
                    }
                });
        connectServer();
    }

    void connectServer(){
        String host=address.split(":")[0];
        String port_str=address.split(":")[1];
        int port=Integer.parseInt(port_str);
        ChannelFuture channelFuture= bootstrap.connect(host,port);
        try {
            channelFuture.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.channel=channelFuture.channel();
    }

    @Override
    public Future<Response> submit(Request request) {

        CompletableFuture<Response> responseFuture= new CompletableFuture<>();
        futureRecord.put(request.getUuid(),responseFuture);
        this.channel.writeAndFlush(request);
        return responseFuture;
    }
}
