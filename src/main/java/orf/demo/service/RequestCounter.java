package orf.demo.service;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class RequestCounter {
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong successfulRequests = new AtomicLong(0);
    private final AtomicLong failedRequests = new AtomicLong(0);

    public void incrementTotal() {
        totalRequests.incrementAndGet();
    }

    public void incrementSuccessful() {
        successfulRequests.incrementAndGet();
    }

    public void incrementFailed() {
        failedRequests.incrementAndGet();
    }

    public long getTotalRequests() {
        return totalRequests.get();
    }

    public long getSuccessfulRequests() {
        return successfulRequests.get();
    }

    public long getFailedRequests() {
        return failedRequests.get();
    }

    public long getCount() {
        return totalRequests.get() + successfulRequests.get() + failedRequests.get();
    }

    public void reset() {
        totalRequests.set(0);
        successfulRequests.set(0);
        failedRequests.set(0);
    }
}