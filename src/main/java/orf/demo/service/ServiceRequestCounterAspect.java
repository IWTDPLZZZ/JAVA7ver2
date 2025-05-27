package orf.demo.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicLong;

@Aspect
@Component
public class ServiceRequestCounterAspect {
    private final AtomicLong counter = new AtomicLong(0);

    @Around("execution(* orf.demo.service.Interface.InterfaceSpellCheckCategoryService.*(..))")
    public Object countRequests(ProceedingJoinPoint joinPoint) throws Throwable {
        counter.incrementAndGet();
        return joinPoint.proceed();
    }

    public long getCounter() {
        return counter.get();
    }

    public void resetCounter() {
        counter.set(0);
    }
}