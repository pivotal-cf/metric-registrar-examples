package io.pivotal.metric_registrar.examples.spring_security;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ExampleController {

    private MeterRegistry registry;
    private AtomicLong customGauge; 
    private Counter simpleCounter;

    public ExampleController(MeterRegistry registry) {
        this.registry = registry;
        this.customGauge = registry.gauge("custom", new AtomicLong(0));
        this.simpleCounter = registry.counter("simple");
    }

    @GetMapping("/high_latency")
    public ResponseEntity<String> highLatency() throws InterruptedException {
        Thread.sleep(2000);
        return new ResponseEntity<>("{}", null, HttpStatus.OK);
    }

    @GetMapping("/custom_metric")
    public ResponseEntity<String> customMetric(@RequestParam(value="inc", defaultValue="") String increment) {
        if (!"".equals(increment)) {
            this.customGauge.incrementAndGet();
        } else {
            this.customGauge.decrementAndGet();
        }
        
        return new ResponseEntity<>("{}", null, HttpStatus.OK);
    }

    @GetMapping("/simple")
    public ResponseEntity<String> simple() {
        this.simpleCounter.increment();
        return new ResponseEntity<>("{}", null, HttpStatus.OK);
    }
}
