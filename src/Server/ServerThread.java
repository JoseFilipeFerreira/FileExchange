package Server;

import Utils.Result;

import java.io.*;
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

    private String parse(String args, InputStream in) throws IOException {
        Matcher a = ParserPatterns.requests.matcher(args);
        if(a.matches()) {
            Matcher o = ParserPatterns.list_objetcs.matcher(a.group("content"));
            List<String> contents = new ArrayList<>();
            while(o.find())
                contents.add(o.group(1));
            switch(a.group("type")) {
                case "add_music":
                    Result<Music, String> m = Music.from_string(contents.get(0))
                            .and_then(musicCenter::upload_music);
//                    if(m.is_err())
//                        return m.toString();
//                    Music music = m.unwrap();
//                    byte[] fContent = new byte[4096];
////                    FileOutputStream
//                    for(int i = 0; in.read(fContent, 0, 4096) > 0; i++)
                    return m.toString();
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
            while((read = in.readLine()) != null ) {
                out.println(this.parse(read, this.socket.getInputStream()));
                out.flush();
            }
        }
        catch(IOException ignored) {}
    }
}
