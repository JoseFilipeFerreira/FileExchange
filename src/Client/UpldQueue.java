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
    private DataOutputStream stream;

    UpldQueue(OutputStream stream) {
        this.stream = new DataOutputStream(stream);
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
        while(true) {
            Upload a = this.get().unwrap();
            File f = new File(a.getPath());
            try {
                FileInputStream fr = new FileInputStream(f);
                stream.writeLong(Long.parseLong(a.getId()));
                long remaning = f.length();
                long n_writes = (f.length()/Defaults.MAXSIZE) - 1;
                stream.writeLong(remaning);
                stream.writeLong(n_writes);
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
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
