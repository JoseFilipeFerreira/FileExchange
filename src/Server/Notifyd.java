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
            while(true) {
                this.notification.get_notify();
                Music m = this.notification.get_music();
                p.println("{type='notify', content=['" + m + "',]}");
                p.flush();
            }
        }
        catch(IOException ignored) {}
    }
}
