package io.pivotal.metric_registrar.examples.spring_security;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {

    private MeterRegistry registry;

    public ExampleController(MeterRegistry registry) {
        this.registry = registry;
    }

    @GetMapping("/high_latency")
    public ResponseEntity<String> highLatency() throws InterruptedException {
        Thread.sleep(2000);
        return new ResponseEntity<>("OK", null, HttpStatus.OK);
    }

    @GetMapping("/custom_metric")
    public ResponseEntity<String> customMetric() {
        this.registry.counter("custom_metric").increment();
        return new ResponseEntity<>("OK", null, HttpStatus.OK);
    }

    @GetMapping("/simple")
    public ResponseEntity<String> simple() {
        return new ResponseEntity<>("OK", null, HttpStatus.OK);
    }
}
