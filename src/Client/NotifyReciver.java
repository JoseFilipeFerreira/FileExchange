package Client;

import Utils.ParserPatterns;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.regex.Matcher;

public class NotifyReciver implements Runnable {
    private Socket notify;

    public NotifyReciver(Socket n) {
        this.notify = n;
    }

    private String pretty_notifications(String input) {
        Matcher p = ParserPatterns.requests.matcher(input);
        p.matches();
        Matcher n = ParserPatterns.list_objetcs.matcher(p.group("content"));
        n.find();
        Matcher m = ParserPatterns.complete_music.matcher(n.group(1));
        m.matches();
        System.out.println(m.group("title") + " by " + m.group("artist") + " was uploaded");
        return null;
    }

    public void run() {
        try {
            BufferedReader read = new BufferedReader(new InputStreamReader(notify.getInputStream()));
            String r;
            while((r = read.readLine()) != null)
                pretty_notifications(r);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
