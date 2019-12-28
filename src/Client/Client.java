package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            Socket request = new Socket("0.0.0.0", 12345);
            Socket notify = new Socket("0.0.0.0", 12346);

            NotifyReciver not = new NotifyReciver(notify);
            new Thread(not).start();

            BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
            PrintWriter out = new PrintWriter(request.getOutputStream());

            BufferedReader user = new BufferedReader(new InputStreamReader(System.in));

            String user_read, socket_read;
            while((user_read = user.readLine()) != null) {
                out.println(user_read);
                out.flush();
                if((socket_read = in.readLine()) == null) break;
                System.out.println(socket_read);
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
