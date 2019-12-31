package Server;

import Utils.Defaults;

import java.io.*;

public class Download {
    private Music m;
    private OutputStream stream;

    Download(Music m, OutputStream stream) {
        this.m = m;
        this.stream = stream;
    }

    public void download() {
        PrintWriter pr = new PrintWriter(stream);
        File f = new File(m.get_path());
        try {
            FileInputStream fr = new FileInputStream(f);
            pr.println(m.get_id());
            long n_writes = f.length()/ Defaults.MAXSIZE;
            pr.println(n_writes);
            pr.flush();
            byte[] buffer = new byte[Defaults.MAXSIZE];
            for(long i = 0; i < n_writes && fr.read(buffer) > 0; i++)
                stream.write(buffer);
            stream.flush();
            fr.close();
        }
        catch(IOException ignored) {}
    }
}
