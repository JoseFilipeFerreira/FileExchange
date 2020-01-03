package Server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class NotificationCenter {
    private List<Music> music;
    private int pos;
    private ReentrantLock lock;
    private Condition notify;

    NotificationCenter() {
        this.music = new ArrayList<Music>();
        this.pos = 0;
        this.lock = new ReentrantLock();
        this.notify = lock.newCondition();
    }

    public void notify(Music music) {
        this.lock.lock();
        this.music.add(music);
        this.pos++;
        this.notify.signalAll();
        this.lock.unlock();
    }

    public Music get_music(int pos) {
        try {
            this.lock.lock();
            if(pos >= this.pos) {
                this.notify.await();
            }
            return this.music.get(pos);
        }
        catch(InterruptedException ignored) {}
        finally {
            this.lock.unlock();
        }
        return null;
    }

    public int get_pos() {
        this.lock.lock();
        int i = this.pos;
        this.lock.unlock();
        return i;
    }
}
