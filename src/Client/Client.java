package Client;

import Utils.ParserPatterns;
import Utils.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {

    private Socket request;
    private Socket notify;
    private Socket file_transfer;
    private int port;
    private NotifyReciver not;
    private UpldQueue queue;
    private PrintWriter out;
    private BufferedReader in;

    Client(ServerSocket file_transfer, int port) {
        try {
            this.request = new Socket("0.0.0.0", 12345);
            this.in = new BufferedReader(new InputStreamReader(request.getInputStream()));
            this.out = new PrintWriter(request.getOutputStream());

            this.notify = new Socket("0.0.0.0", 12346);
            this.not = new NotifyReciver(notify);
            new Thread(this.not).start();

            this.port = port;
            out.println("{type='connect', content=['" + port + "',]}");
            out.flush();

            System.out.println(in.readLine());
            this.file_transfer = file_transfer.accept();

            this.queue = new UpldQueue(this.file_transfer.getOutputStream());
            new Thread(this.queue).start();
        } catch (IOException ignored){}
    }

    Result<String, String> format(String[] splits) {
        switch(splits[0]) {
            case "download":
                if(splits.length != 2 || Pattern.matches("[0-9].*", splits[1])) break;
                return Result.Ok("{type='download', content=['" + splits[1] + "',]}");
            case "add_music":
                if(splits.length != 6) break;
                return Result.Ok("{type='add_music', content=['Music{title='" + splits[1]
                                         + "':artist='" + splits[2]
                                         + "':year=" + splits[3]
                                         + ":tags=[" + splits[4] +"]}',]}");
            case "login":
            case "add_user":
                if(splits.length != 3) break;
                return Result.Ok("{type='" + splits[0] + "', content=['User{name='" + splits[1]
                                         + "':passwd='" + splits[2] + "'}',]}");
            case "search":
                if(splits.length != 2) break;
                return Result.Ok("{type='search', content=['" + splits[1] + "',]}");

        }
        return Result.Err("Wrong format request");
    }

    void handle_input(String user_input) throws IOException {
        String[] splits = user_input.split(" ");
        Result<String, String> parse = this.format(splits);
        String read;
        switch(splits[0]) {
            case "add_music":
                if(splits.length != 6) break;
                if(parse.is_err()) {
                    System.out.println(parse.unwrap_err());
                    break;
                }
                if(Files.exists(Path.of(splits[5]))) {
                    out.println(parse.unwrap());
                    out.flush();
                    read = in.readLine();
                    Matcher o = ParserPatterns.replies.matcher(read);
                    o.matches();
                    this.queue.put(new Upload(o.group("result"), splits[5]));
                    System.out.println(o.group("result"));
                    break;
                }
                System.out.println("Invalid or non existent file");
                break;
            case "login":
            case "add_user":
                if(splits.length != 3) break;
                if(parse.is_err()) {
                    System.out.println(parse.unwrap_err());
                    break;
                }
                out.println(parse.unwrap());
                out.flush();
                read = in.readLine();
                System.out.println(read);
                break;
            case "search":
            case "download":
                if(splits.length != 2) break;
                if(parse.is_err()) {
                    System.out.println(parse.unwrap_err());
                    break;
                }
                out.println(parse.unwrap());
                out.flush();
                read = in.readLine();
                System.out.println(read);
                break;
        }
    }

    public static void main(String[] args) {
        try {
            boolean weDidIt = false;
            ServerSocket s = null;
            int port = 0;
            while(!weDidIt) {
                try {
                    port = new Random().nextInt(65535);
                    s = new ServerSocket(port);
                    weDidIt = true;
                } catch (IOException ignored){}
            }

            Client c = new Client(s, port);

            BufferedReader user = new BufferedReader(new InputStreamReader(System.in));

            String user_read;
            while((user_read = user.readLine()) != null) {
                c.handle_input(user_read);
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
