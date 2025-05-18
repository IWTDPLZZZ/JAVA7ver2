package orf.demo.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceRequestCounterAspectTest {

    private ServiceRequestCounterAspect counterAspect;
    private ProceedingJoinPoint joinPoint;

    @BeforeEach
    void setUp() throws Throwable {
        counterAspect = mock(ServiceRequestCounterAspect.class);
        joinPoint = mock(ProceedingJoinPoint.class);

        final int[] counter = {0};

        lenient().doAnswer(invocation -> {
            counter[0]++;
            ProceedingJoinPoint jp = invocation.getArgument(0);
            return jp.proceed();
        }).when(counterAspect).countRequests(any(ProceedingJoinPoint.class));

        lenient().when(counterAspect.getCounter()).thenAnswer(invocation -> counter[0]);

        lenient().doAnswer(invocation -> {
            counter[0] = 0;
            return null;
        }).when(counterAspect).resetCounter();

        lenient().when(joinPoint.proceed()).thenReturn(mock(Object.class));
    }

    @Test
    void shouldIncrementCounterWhenCountingRequests() throws Throwable {
        counterAspect.countRequests(joinPoint);
        int countAfterFirstCall = (int) counterAspect.getCounter();
        counterAspect.countRequests(joinPoint);
        int countAfterSecondCall = (int) counterAspect.getCounter();

        assertEquals(1, countAfterFirstCall);
        assertEquals(2, countAfterSecondCall);
        verify(counterAspect, times(2)).countRequests(joinPoint);
        verify(joinPoint, times(2)).proceed();
        verify(counterAspect, times(2)).getCounter();
    }

    @Test
    void shouldResetCounterSuccessfully() throws Throwable {
        counterAspect.countRequests(joinPoint);

        counterAspect.resetCounter();
        int countAfterReset = (int) counterAspect.getCounter();

        assertEquals(0, countAfterReset);
        verify(counterAspect, times(1)).countRequests(joinPoint);
        verify(joinPoint, times(1)).proceed();
        verify(counterAspect, times(1)).resetCounter();
        verify(counterAspect, times(1)).getCounter();
    }
}