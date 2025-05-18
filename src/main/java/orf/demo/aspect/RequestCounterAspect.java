package orf.demo.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import orf.demo.service.RequestCounter;

@Aspect
@Component
public class RequestCounterAspect {
    private final RequestCounter requestCounter;

    public RequestCounterAspect(RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
    }

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {}

    @Before("serviceMethods()")
    public void countAllRequests() {
        requestCounter.incrementTotal();
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void countSuccessfulRequests(Object result) {
        requestCounter.incrementSuccessful();
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void countFailedRequests(Exception ex) {
        requestCounter.incrementFailed();
    }
}