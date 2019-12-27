package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        MusicCenter mc = new MusicCenter();

        try {
            ServerSocket socket = new ServerSocket(12345);
            while(true) {
                Socket clSocket = socket.accept();
                ServerThread st = new ServerThread(clSocket, mc);

                Thread t = new Thread(st);
                t.start();
            }
        }
        catch(IOException ignored) {}
    }
}
