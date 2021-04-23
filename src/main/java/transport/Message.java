package transport;

import common.RPCRequest;
import common.RPCResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Message {
    private byte type;
    private RPCRequest request;
    private RPCResponse response;

    public static final byte PING = 1 << 0;
    public static final byte PONG = 1 << 1;
    public static final byte REQUEST = 1 << 2;
    public static final byte RESPONSE = 1 << 3;

    public Message(byte type) {
        this.type = type;
    }

    public static final Message PING_MSG = new Message(PING);
    public static final Message PONG_MSG = new Message(PONG);

    public static Message buildRequest(RPCRequest request) {
        return new Message(REQUEST,request,null);
    }

    public static Message buildResponse(RPCResponse response) {
        return new Message(RESPONSE,null,response);
    }

}
