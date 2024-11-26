@file:Suppress("DEPRECATION")

package io.pivotal.metric_registrar.examples.kotlin.controllers

import com.nhaarman.mockitokotlin2.*
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.management.timer.Timer


@ExtendWith(SpringExtension::class)
@WebMvcTest
class ExampleControllerTest(@Autowired private val mockMvc: MockMvc) {

    @MockBean
    private lateinit var registry: MeterRegistry

    private val mockCounter: Counter = mock()

    @BeforeEach
    fun setup() {
        whenever(registry.counter(any())).doReturn(mockCounter)
    }

    @Test
    fun ok() {
        mockMvc.perform(get("/simple")).andExpect(status().isOk)
    }

    @Test
    fun highLatency() {
        val startTime = System.currentTimeMillis();
        mockMvc.perform(get("/high_latency")).andExpect(status().isOk)
        assertTrue(System.currentTimeMillis() - startTime > 2 * Timer.ONE_SECOND, "Too Fast")
    }

    @Test
    fun customMetric() {
        mockMvc.perform(get("/custom_metric")).andExpect(status().isOk)

        verify(mockCounter).increment()
    }
}

