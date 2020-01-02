package Server;

import Utils.Defaults;

import java.io.*;

public class Download {
    private Music m;
    private DataOutputStream stream;

    Download(Music m, OutputStream stream) {
        this.m = m;
        this.stream = new DataOutputStream(stream);
    }

    public void download() {
        File f = new File(m.get_path());
        try {
            FileInputStream fr = new FileInputStream(f);
            stream.writeLong(m.get_id());
            long remaning = f.length();
            long n_reads = (f.length()/Defaults.MAXSIZE) - 1;
            stream.writeLong(remaning);
            stream.writeLong(n_reads);
            stream.flush();
            byte[] buffer = new byte[Defaults.MAXSIZE];
            int r;
            while((r = fr.read(buffer)) > 0) {
                stream.writeInt(r);
                stream.write(buffer, 0, r);
                stream.flush();
            }
            stream.writeInt(0);
            fr.close();
            stream.flush();
        }
        catch(IOException ignored) {}
    }
}
