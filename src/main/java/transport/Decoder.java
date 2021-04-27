package transport;

import common.Message;
import common.Request;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import serializer.JsonSerilizer;
import serializer.Serializer;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;

public class Decoder extends ByteToMessageDecoder {
    Serializer serializer=new JsonSerilizer();
    Class<?> decodeClass;

    public Decoder(Class<?> decodeClass){
        this.decodeClass=decodeClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        out.add(serializer.deserilize(bytes, decodeClass));
    }
}
