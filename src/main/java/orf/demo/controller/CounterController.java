package orf.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import orf.demo.service.ServiceRequestCounterAspect;

@RestController
public class CounterController {
    private final ServiceRequestCounterAspect counterAspect;

    public CounterController(ServiceRequestCounterAspect counterAspect) {
        this.counterAspect = counterAspect;
    }

    @GetMapping("/api/counter")
    public long getRequestCounter() {
        return counterAspect.getCounter();
    }
}