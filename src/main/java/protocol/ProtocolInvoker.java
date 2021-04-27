package protocol;

import common.Request;
import common.Response;
import transport.Client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ProtocolInvoker<T> implements Invoker<T>{
    String address;
    String interfaceName;
    Class<T> interfaceClass;
    Client client;
    Protocol protocol;


    public ProtocolInvoker(String address,String interfaceName,Class<T> interfaceClass,Protocol protocol){
        this.address=address;
        this.interfaceName=interfaceName;
        this.interfaceClass=interfaceClass;
        this.protocol=protocol;
        this.client=protocol.getClient((address));


    }

    @Override
    public Class<T> getInterface() {
        return interfaceClass;
    }

    @Override
    public String getInterfaceName() {
        return interfaceName;
    }

    @Override
    public Response invoke(Request request) {
        Future<Response> responseFuture= client.submit(request);
        try {
            return responseFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
