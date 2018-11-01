package io.pivotal.metric_registrar.examples.kotlin.controllers

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.management.timer.Timer

@RestController
class ExampleController(@Autowired private val registry: MeterRegistry) {
    @RequestMapping("/simple")
    fun simple(): String {
        return "{}"
    }

    @RequestMapping("/high_latency")
    fun highLatency(): String {
        Thread.sleep(2 * Timer.ONE_SECOND)
        return "{}"
    }

    @GetMapping("/custom_metric")
    fun customMetric(): String {
        registry.counter("custom_metric").increment()
        return "{}"
    }
}
