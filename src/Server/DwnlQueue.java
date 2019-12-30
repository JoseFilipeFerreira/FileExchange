package Server;

import Utils.Defaults;
import Utils.Result;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DwnlQueue {
    private ArrayList<Download> queue;
    private ReentrantLock lock;
    private Condition full;
    private Condition empty;

    DwnlQueue() {
        this.queue = new ArrayList<Download>();
        this.lock = new ReentrantLock();
        this.full = this.lock.newCondition();
        this.empty = this.lock.newCondition();
    }

    void put(Download a) {
        this.lock.lock();
        try {
            while(this.queue.size() >= Defaults.MAXDOWN)
                this.full.await();
            this.queue.add(a);
            this.empty.signal();
        }
        catch(InterruptedException ignored) {}
        finally {
            this.lock.unlock();
        }
    }

    Result<Download, String> get() {
        this.lock.lock();
        try {
            while(this.queue.isEmpty())
                this.empty.await();
            Download a = this.queue.remove(0);
            this.full.signal();
            return Result.Ok(a);
        }
        catch(InterruptedException e) {
            return Result.Err(e.getMessage());
        }
        finally {
            this.lock.unlock();
        }
    }
}
