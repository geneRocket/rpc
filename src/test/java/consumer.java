import config.ReferenceConfig;



public class consumer {
    public static void main(String[] args) {
        ReferenceConfig referenceConfig= ReferenceConfig.createReferenceConfig(HelloWorld.class.getName(),HelloWorld.class,false);
        HelloWorld helloWorld=(HelloWorld) referenceConfig.get();
        System.out.println(helloWorld.get());
    }
}
