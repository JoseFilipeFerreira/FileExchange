package Client;

import Server.Download;
import Utils.Defaults;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DwnlThread implements Runnable {
   private DataInputStream in;

    DwnlThread(InputStream in) {
        this.in = new DataInputStream(in);
    }

    public void run() {
        byte[] fContent = new byte[Defaults.MAXSIZE];
        try {
            while(true) {
                long id = in.readLong();
                File file = new File(String.valueOf(id));
                System.out.println("Music " + id + " donwnload is starting");
                long size = in.readLong();
                long n_reads = in.readLong();
                FileOutputStream f = new FileOutputStream(file, true);
                int reads = 1;
                for(long i = 0; reads > 0; i++) {
                    reads = in.readInt();
                    in.readNBytes(fContent, 0, reads);
                    f.write(fContent);
                }
                f.flush();
                f.close();
                System.out.println("Music " + id + " finished downloading");
            }
        }
        catch(IOException ignored) {}
    }
}
