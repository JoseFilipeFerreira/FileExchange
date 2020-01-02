package Server;

import Utils.Defaults;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DwnlMgr {

    private int current_downloads;
    private ReentrantLock lock;
    private Condition full;

    DwnlMgr() {
        this.current_downloads = 0;
        this.lock = new ReentrantLock();
        this.full = this.lock.newCondition();
    }

    void start_download() {
        try {
            this.lock.lock();
            while(this.current_downloads >= Defaults.MAXDOWN) {
                this.full.await();
            }
            this.current_downloads++;
        }
        catch(InterruptedException ignored) {}
        finally { this.lock.unlock(); }
    }

    void finish_download() {
        this.lock.lock();
        this.current_downloads--;
        this.full.signal();
        this.lock.unlock();
    }
}
