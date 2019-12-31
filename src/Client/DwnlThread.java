package Client;

import Utils.Defaults;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DwnlThread implements Runnable {
   private InputStream in;

    DwnlThread(InputStream in) {
        this.in = in;
    }

   public void run() {
       byte[] fContent = new byte[Defaults.MAXSIZE];
        try {
            BufferedReader a = new BufferedReader(new InputStreamReader(in));
            String read;
            while((read = a.readLine()) != null) {
                File file = new File(read);
                int n_reads = Integer.parseInt(a.readLine());
                System.out.println("Music" + read + " donwnload is starting");
                FileOutputStream f = new FileOutputStream(file, true);
                for(int i = 0; i < n_reads && in.read(fContent, 0, Defaults.MAXSIZE) > 0; i++)
                    f.write(fContent);
                f.flush();
                f.close();
                System.out.println("Music " + read + " finished downloading");
            }
        }
        catch(IOException ignored) {}
    }
}
