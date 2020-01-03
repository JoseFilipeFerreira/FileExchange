package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.locks.Condition;

public class Notifyd implements Runnable {

    private Socket socket;
    private NotificationCenter notification;

    public Notifyd(Socket s, NotificationCenter n) {
        this.notification = n;
        this.socket = s;
    }

    public void run() {
        try {
            PrintWriter p = new PrintWriter(socket.getOutputStream());
            int pos = this.notification.get_pos();
            while(true) {
                Music m = this.notification.get_music(pos++);
                p.println("{type='notify', content=['" + m + "';]}");
                p.flush();
            }
        }
        catch(IOException ignored) {}
    }
}
