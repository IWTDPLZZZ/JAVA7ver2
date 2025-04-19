package orf.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestCounterTest {

    private RequestCounter requestCounter;

    @BeforeEach
    void setUp() {
        requestCounter = new RequestCounter();
    }

    @Test
    void testIncrementAndGet_SingleThread() {
        assertEquals(1, requestCounter.incrementAndGet());
        assertEquals(2, requestCounter.incrementAndGet());
        assertEquals(2, requestCounter.getCount());
    }

    @Test
    void testIncrementAndGet_MultiThread() throws InterruptedException {
        int threadCount = 100;
        int incrementsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    requestCounter.incrementAndGet();
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(threadCount * incrementsPerThread, requestCounter.getCount());
    }

    @Test
    void testReset() {
        requestCounter.incrementAndGet();
        requestCounter.reset();
        assertEquals(0, requestCounter.getCount());
    }
}