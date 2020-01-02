package Server;

import Utils.Defaults;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UploadThread implements Runnable {
    private DataInputStream in;

    UploadThread(InputStream in) {
        this.in = new DataInputStream(in);
    }

    public void run() {
        byte[] fContent = new byte[Defaults.MAXSIZE];
        Path newer = Paths.get(".media");
        try {
            if(Files.notExists(newer))
                Files.createDirectory(newer);
            while(true) {
                long id = in.readLong();
                File file = newer.resolve(String.valueOf(id)).toFile();
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
            }
        }
        catch(IOException e) {}
    }
}
