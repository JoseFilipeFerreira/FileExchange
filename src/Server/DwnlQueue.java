package Server;

import Utils.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DwnlQueue implements Runnable {
    private List<Download> queue;
    private ReentrantLock lock;
    private Condition empty;
    private DwnlMgr man;

    DwnlQueue(DwnlMgr man) {
        this.queue = new ArrayList<Download>();
        this.lock = new ReentrantLock();
        this.empty = this.lock.newCondition();
        this.man = man;
    }

    void put(Download a) {
        this.lock.lock();
        this.queue.add(a);
        this.empty.signal();
        this.lock.unlock();
    }

    Result<Download, String> get() {
        this.lock.lock();
        try {
            while(this.queue.isEmpty())
                this.empty.await();
            Download a = this.queue.remove(0);
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
            Result<Download, String> dwnld = this.get();
            if(dwnld.is_ok()) {
                man.start_download();
                dwnld.unwrap().download();
                man.finish_download();
            }
        }
    }
}
