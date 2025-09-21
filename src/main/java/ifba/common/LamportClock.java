package ifba.common;

import java.util.concurrent.atomic.AtomicInteger;


public class LamportClock {
    private AtomicInteger time = new AtomicInteger(0);

    public int increment() {
        return time.incrementAndGet();
    }

    public int update(int receivedTimestamp) {
        time.set(Math.max(time.get(), receivedTimestamp) + 1);
        return time.get();
    }

    public int getTime() {
        return time.get();
    }
}

