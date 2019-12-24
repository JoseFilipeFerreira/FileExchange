package Server;

import Utils.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class ServerThread implements Runnable {

    private Socket socket;
    private MusicCenter musicCenter;

    ServerThread(Socket socket, MusicCenter musicCenter) {
        this.socket = socket;
        this.musicCenter = musicCenter;
    }

    private String parse(String args) {
        Matcher a = ParserPatterns.requests.matcher(args);
        if(a.matches()) {
            Matcher o = ParserPatterns.list_objetcs.matcher(a.group("content"));
            List<String> contents = new ArrayList<>();
            while(o.find())
                contents.add(o.group(1));
            switch(a.group("type")) {
                case "add_music":
                    return Music.from_string(contents.get(0))
                            .and_then(musicCenter::upload_music)
                            .toString();
                case "add_user":
                    return User.from_string(contents.get(0))
                            .and_then(musicCenter::create_user)
                            .toString();
                case "login":
                    return User.from_string(contents.get(0))
                            .and_then(musicCenter::check_login)
                            .toString();
                case "search":
                    return Result.Ok(musicCenter
                                             .search_tags(contents.get(0)))
                            .toString();
            }
        }
        return Result.Err("Invalid format request").toString();
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            String read;
            while((read = in.readLine()) != null) {
                out.println(this.parse(read));
                out.flush();
            }
        }
        catch(IOException ignored) {}
    }
}
