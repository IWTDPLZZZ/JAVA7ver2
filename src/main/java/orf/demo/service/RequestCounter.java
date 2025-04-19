package orf.demo.service;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class RequestCounter {
    private final AtomicLong counter = new AtomicLong(0);

    public long incrementAndGet() {
        return counter.incrementAndGet();
    }

    public long getCount() {
        return counter.get();
    }

    public void reset() {
        counter.set(0);
    }
}