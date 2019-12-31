package Client;

import Utils.Defaults;
import Utils.Result;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class UpldQueue implements Runnable {
    private ArrayList<Upload> queue;
    private ReentrantLock lock;
    private Condition empty;
    private OutputStream stream;

    UpldQueue(OutputStream stream) {
        this.stream = stream;
        this.queue = new ArrayList<Upload>();
        this.lock = new ReentrantLock();
        this.empty = this.lock.newCondition();
    }

    void put(Upload a) {
        this.lock.lock();
        this.queue.add(a);
        this.empty.signal();
        this.lock.unlock();
    }

    Result<Upload, String> get() {
        this.lock.lock();
        try {
            while(this.queue.isEmpty())
                this.empty.await();
            Upload a = this.queue.remove(0);
            return Result.Ok(a);
        }
        catch(InterruptedException e) {
            return Result.Err(e.getMessage());
        }
        finally {
            this.lock.unlock();
        }
    }

    public void run() {
        PrintWriter pr = new PrintWriter(stream);
        while(true) {
            Upload a = this.get().unwrap();
            File f = new File(a.getPath());
            try {
                FileInputStream fr = new FileInputStream(f);
                pr.println(a.getId());
                long n_writes = f.length()/Defaults.MAXSIZE;
                pr.println(n_writes);
                pr.flush();
                byte[] buffer = new byte[Defaults.MAXSIZE];
                for(long i = 0; i < n_writes && fr.read(buffer) > 0; i++)
                    stream.write(buffer);
                stream.flush();
                fr.close();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
