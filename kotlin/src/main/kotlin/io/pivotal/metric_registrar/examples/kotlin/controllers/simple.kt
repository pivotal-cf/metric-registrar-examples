package io.pivotal.metric_registrar.examples.kotlin.controllers

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SimpleController {
    @RequestMapping("/simple")
    fun simple(): String {
        return "{}"
    }
}
