package Server;

import Utils.Defaults;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UploadThread implements Runnable {
   private InputStream in;

    UploadThread(InputStream in) {
        this.in = in;
    }

   public void run() {
       byte[] fContent = new byte[Defaults.MAXSIZE];
        Path newer = Paths.get(".media");
        try {
            if(Files.notExists(newer))
                Files.createDirectory(newer);
            BufferedReader a = new BufferedReader(new InputStreamReader(in));
            String read;
            while((read = a.readLine()) != null) {
                File file = newer.resolve(read)
                        .toFile();
                int n_reads = Integer.parseInt(a.readLine());
                FileOutputStream f = new FileOutputStream(file, true);
                for(int i = 0; i < n_reads && in.read(fContent, 0, Defaults.MAXSIZE) > 0; i++)
                    f.write(fContent);
                f.flush();
                f.close();
            }
        }
        catch(IOException ignored) {}
    }
}
