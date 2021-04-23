package transport;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import serialize.Serializer;

public class ToyEncoder extends MessageToByteEncoder {
    Serializer serializer;

    public ToyEncoder(Serializer serializer) {
        this.serializer=serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        Message message = (Message) msg;
        out.writeByte((message.getType()));
        if (message.getType() == Message.REQUEST) {
            byte[] bytes = serializer.serialize(message.getRequest());
            out.writeBytes(bytes);
        } else if (message.getType() == Message.RESPONSE) {
            byte[] bytes = serializer.serialize(message.getResponse());
            out.writeBytes(bytes);
        }
    }
}
