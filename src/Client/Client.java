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

public class Client implements Runnable {

    private Socket request;
    private Socket notify;
    private Socket file_transfer;
    private int port;
    private NotifyReciver not;
    private UpldQueue queue;
    private PrintWriter out;
    private BufferedReader in;
    private DwnlThread dl;

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

            in.readLine();
            this.file_transfer = file_transfer.accept();

            this.queue = new UpldQueue(this.file_transfer.getOutputStream());
            new Thread(this.queue).start();

            this.dl = new DwnlThread(this.file_transfer.getInputStream());
            new Thread(this.dl).start();
        } catch (IOException ignored){}
    }

    Result<String, String> format(String[] splits) {
        switch(splits[0]) {
            case "download":
                if(splits.length != 2 || !Pattern.matches("[0-9].*", splits[1])) break;
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
        return Result.Err("Error: Wrong format request");
    }

    void format_output(String output, String type) {
        Matcher out = ParserPatterns.replies.matcher(output);
        out.matches();
        if(out.group("status").equals("failed"))
            System.out.println("Error: " + out.group("result"));
        else {
            switch(type) {
                case "add_music":
                    System.out.println("New music added: " + out.group("result"));
                    break;
                case "login":
                    System.out.println("User logged: " + out.group("result"));
                    break;
                case "add_user":
                    System.out.println("User added: " + out.group("result"));
                    break;
                case "download":
                    System.out.println("Music " + out.group("result") + " was added to your download queue");
                    break;
                case "search":
                    Matcher content = ParserPatterns.list_objetcs.matcher(out.group("result"));
                    while(content.find()) {
                        Matcher music = ParserPatterns.complete_music.matcher(content.group(1));
                        if(music.matches())
                            System.out.println("Id: " + music.group("id")
                                                       + ", Title: " + music.group("title")
                                                       + ", Artist: " + music.group("artist")
                                                       + ", Year: " + music.group("year")
                                                       + ", Downloads: " + music.group("downloads")
                                                       + ", Tags: " + music.group("tags"));
                    }
                    break;
            }
        }
    }

    void handle_input(String user_input) throws IOException {
        String[] splits = user_input.split(" ");
        Result<String, String> parse = this.format(splits);
        String read;
        switch(splits[0]) {
            case "help":
                System.out.println("Login : `login user passwd`");
                System.out.println("Add user: `add_user user passwd`");
                System.out.println("Add music: `add_music title artist year 'tag1','tag2',... path/to/file`");
                System.out.println("Download: `download music_id`");
                System.out.println("Search songs: `search tag`");
                System.out.println("To show this info again: `help`");
                break;
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
                    if(o.group("status").equals("ok"))
                        this.queue.put(new Upload(o.group("result"), splits[5]));
                    this.format_output(read, splits[0]);
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
                this.format_output(read, splits[0]);
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
                this.format_output(read, splits[0]);
                break;
        }
    }

    public Client() {
            boolean weDidIt = false;
            ServerSocket s = null;
            int port = 0;
            while(!weDidIt) {
                try {
                    port = new Random().nextInt(65535);
                    s = new ServerSocket(port);
                    weDidIt = true;
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

                        in.readLine();
                        this.file_transfer = s.accept();

                        this.queue = new UpldQueue(this.file_transfer.getOutputStream());
                        new Thread(this.queue).start();

                        this.dl = new DwnlThread(this.file_transfer.getInputStream());
                        new Thread(this.dl).start();
                    } catch (IOException ignored){}
                }
                catch(IOException ignored) {
                }
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

            c.handle_input("help");
            System.out.println("In order to use the system please login using the command above");
            String user_read;
            while((user_read = user.readLine()) != null) {
                c.handle_input(user_read);
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        for(int i = 0; i < 4; i++) {
            try {
                this.handle_input("download 1");
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
