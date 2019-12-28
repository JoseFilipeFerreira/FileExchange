package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class NotifyReciver implements Runnable {
    private Socket notify;

    public NotifyReciver(Socket n) {
        this.notify = n;
    }

    public void run() {
        try {
            BufferedReader read = new BufferedReader(new InputStreamReader(notify.getInputStream()));
            String r;
            while((r = read.readLine()) != null)
                System.out.println(r);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
