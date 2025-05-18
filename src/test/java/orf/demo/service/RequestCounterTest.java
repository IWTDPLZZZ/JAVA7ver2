package orf.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestCounterTest {

    private RequestCounter requestCounter;

    @BeforeEach
    void setUp() {
        requestCounter = mock(RequestCounter.class);
    }

    @Test
    void shouldIncrementAndGetInSingleThread() {
        doAnswer(invocation -> {
            long current = requestCounter.getCount();
            return current + 1;
        }).when(requestCounter).incrementAndGet();

        when(requestCounter.getCount()).thenReturn(0L, 1L, 2L);

        long firstCall = requestCounter.incrementAndGet();
        long secondCall = requestCounter.incrementAndGet();
        long finalCount = requestCounter.getCount();

        assertEquals(1, firstCall);
        assertEquals(2, secondCall);
        assertEquals(2, finalCount);
        verify(requestCounter, times(2)).incrementAndGet();
        verify(requestCounter, times(3)).getCount();
    }

    @Test
    void shouldIncrementAndGetInMultipleThreads() throws InterruptedException {
        int threadCount = 100;
        int incrementsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        long expectedCount = (long) threadCount * incrementsPerThread;

        doAnswer(invocation -> {
            long current = requestCounter.getCount();
            return current + 1;
        }).when(requestCounter).incrementAndGet();

        when(requestCounter.getCount()).thenReturn(0L, expectedCount);

        Runnable task = () -> {
            for (int j = 0; j < incrementsPerThread; j++) {
                requestCounter.incrementAndGet();
            }
            latch.countDown();
        };

        for (int i = 0; i < threadCount; i++) {
            executor.submit(task);
        }
        latch.await();
        executor.shutdown();

        assertEquals(expectedCount, requestCounter.getCount());
        verify(requestCounter, times(threadCount * incrementsPerThread)).incrementAndGet();
        verify(requestCounter, times(1)).getCount();
    }

    @Test
    void shouldResetCount() {
        doAnswer(invocation -> {
            long current = requestCounter.getCount();
            return current + 1;
        }).when(requestCounter).incrementAndGet();

        when(requestCounter.getCount()).thenReturn(0L, 1L, 0L);

        requestCounter.incrementAndGet();
        requestCounter.reset();
        long finalCount = requestCounter.getCount();

        assertEquals(0, finalCount);
        verify(requestCounter, times(1)).incrementAndGet();
        verify(requestCounter, times(1)).reset();
        verify(requestCounter, times(3)).getCount();
    }
}