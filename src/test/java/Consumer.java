import config.Refer;

public class Consumer {
    public static void main(String[] args) {
        Refer<HelloWorld> refer=new Refer<>("HelloWorld",HelloWorld.class);
        HelloWorld helloWorld=refer.get();
        for (int i=0;;i++){
            System.out.println(i);
            System.out.println(helloWorld.get());

        }
    }
}
