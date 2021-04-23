import config.ReferenceConfig;
import config.ServiceConfig;

public class provider {
    public static void main(String[] args) {
        ServiceConfig serverConfig=new ServiceConfig("HelloWorld",HelloWorld.class,new HelloWorldImpl());
        serverConfig.export();
    }
}
