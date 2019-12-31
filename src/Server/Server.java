package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        NotificationCenter nc = new NotificationCenter();
        MusicCenter mc = new MusicCenter(nc);
        DwnlMgr man = new DwnlMgr();

        try {
            ServerSocket socket = new ServerSocket(12345);
            ServerSocket notify = new ServerSocket(12346);
            while(true) {
                Socket cl_socket = socket.accept();
                Socket cl_notify = notify.accept();
                ServerThread st = new ServerThread(cl_socket, mc, man);
                Notifyd nd = new Notifyd(cl_notify, nc);

                Thread t = new Thread(st);
                t.start();
                t = new Thread(nd);
                t.start();
            }
        }
        catch(IOException ignored) {}
    }
}
