package transport;

import common.Request;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import protocol.Invoker;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NettyServer implements Server {
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup(4);
    int port = 8000;

    Executor executor = new ThreadPoolExecutor(4, 8, 500, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    Invoker<?> invoker;

    public NettyServer(Invoker<?> invoker){
        this.invoker=invoker;
    }

    class ServerHandler extends SimpleChannelInboundHandler<Request> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {
            executor.execute(new TaskRunner(ctx,request,invoker));
        }
    }

    @Override
    public void run() {
        try {
            // 2. 创建netty对应的入口核心类 ServerBootstrap
            ServerBootstrap b = new ServerBootstrap();
            // 3. 设置server的各项参数，以及应用处理器
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // 3.2. 最重要的，将各channelHandler绑定到netty的上下文中（暂且这么说吧）
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new LengthFieldPrepender(4));
                            p.addLast(new LengthFieldBasedFrameDecoder(1000,0,4,0,4));
                            p.addLast(new Encoder());
                            p.addLast(new Decoder(Request.class));
                            p.addLast(new ServerHandler());
                        }
                    });

            ChannelFuture f = b.bind(port).sync();

            // 5. 等待关闭信号，让业务线程去服务业务了
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 6. 收到关闭信号后，优雅关闭server的线程池，保护应用
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


}
