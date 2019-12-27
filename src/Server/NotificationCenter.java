package Server;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class NotificationCenter {
    private Music music;
    private ReentrantLock lock;
    private Condition notify;

    NotificationCenter() {
        this.music = null;
        this.lock = new ReentrantLock();
        this.notify = lock.newCondition();
    }

    public void notify(Music music) {
        this.lock.lock();
        this.music = music;
        this.notify.signalAll();
        this.lock.unlock();
    }

    public Music get_music() {
        return this.music;
    }

    void get_notify() {
        try {
            this.lock.lock();
            this.notify.await();
            this.lock.unlock();
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
