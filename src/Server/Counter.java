package Server;

public class Counter {
    private int c;

    Counter() {
        this.c = 0;
    }

    public synchronized int increment() {
        c++;
        return c;
    }
}
