package Client;

import java.io.IOException;

public class Test {
    public static void main(String[] args) {
        Client[] cls = new Client[100];
        Thread[] tls = new Thread[100];
        Client cl = new Client();
        try {
            cl.handle_input("add_user manuel 100");
            cl.handle_input("login manuel 100");
            Thread.sleep(1000);
            for(int i = 0; i < 100; i++) {
                cls[i] = new Client();
                cls[i].handle_input("login manuel 100");
                tls[i] = new Thread(cls[i]);
                tls[i].start();
            }
            for(int i = 0; i < 100; i++)
                tls[i].join();
        }
        catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
