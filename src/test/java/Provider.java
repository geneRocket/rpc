import config.Service;
import transport.NettyServer;
import transport.Server;

public class Provider {
    public static void main(String[] args) {
        Service<HelloWorld> helloWorldService=new Service<>("HelloWorld",HelloWorld.class,new HelloWorldImpl());
        helloWorldService.export();
    }
}
