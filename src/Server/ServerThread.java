package Server;

import Utils.ParserPatterns;
import Utils.Result;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class ServerThread implements Runnable {

    private Socket socket;
    private Socket file_socket;
    private MusicCenter musicCenter;
    private DwnlQueue down;
    private User logged;

    ServerThread(Socket socket, MusicCenter musicCenter, DwnlMgr man) {
        this.socket = socket;
        this.musicCenter = musicCenter;
        this.file_socket = null;
        this.down = new DwnlQueue(man);
        new Thread(this.down).start();
        this.logged = null;
    }

    private String parse(String args) throws IOException {
        Matcher a = ParserPatterns.requests.matcher(args);
        if(a.matches()) {
            Matcher o = ParserPatterns.list_objetcs.matcher(a.group("content"));
            List<String> contents = new ArrayList<>();
            while(o.find())
                contents.add(o.group(1));
            if(this.logged == null) {
                switch(a.group("type")) {
                    case "connect":
                        this.file_socket = new Socket("0.0.0.0", Integer.parseInt(contents.get(0)));
                        UploadThread upld = new UploadThread(this.file_socket.getInputStream());
                        new Thread(upld).start();
                        return Result.Ok("Connected")
                                .toString();
                    case "login":
                        return User.from_string(contents.get(0))
                                .and_then(musicCenter::check_login)
                                .also(x -> this.logged = x)
                                .map(User::get_name)
                                .toString();
                    case "add_user":
                        return User.from_string(contents.get(0))
                                .and_then(musicCenter::create_user)
                                .map(User::get_name)
                                .toString();
                }
            }
            else {
                switch(a.group("type")) {
                    case "connect":
                        this.file_socket = new Socket("0.0.0.0", Integer.parseInt(contents.get(0)));
                        UploadThread upld = new UploadThread(this.file_socket.getInputStream());
                        new Thread(upld).start();
                        return Result.Ok("Connected")
                                .toString();
                    case "add_music":
                        Result<Long, String> m = Music.from_string(contents.get(0))
                                .and_then(musicCenter::upload_music);
                        if(m.is_err())
                            return m.toString();
                        return m.toString();
                    case "search":
                        return Result.Ok(musicCenter
                                                 .search_tags(contents.get(0)))
                                .toString();
                    case "download":
                        var out = this.file_socket.getOutputStream();
                        return musicCenter.download(Long.parseLong(contents.get(0)))
                                .also(x -> this.down.put(new Download(x, out)))
                                .toString();

                }
                return Result.Err("Invalid format request").toString();
            }
            return Result.Err("Please login").toString();
        }
        return Result.Err("Invalid format request").toString();
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            String read;
            while((read = in.readLine()) != null ) {
                out.println(this.parse(read));
                out.flush();
            }
            in.close();
            out.close();
            this.file_socket.close();
            this.socket.close();
        }
        catch(IOException ignored) {}
    }
}
