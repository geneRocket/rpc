package transport;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import serializer.JsonSerilizer;
import serializer.Serializer;

public class Encoder extends MessageToByteEncoder {
    Serializer serializer=new JsonSerilizer();

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        out.writeBytes(serializer.serilize(msg));
    }
}
