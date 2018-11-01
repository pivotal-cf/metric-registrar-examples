package io.pivotal.metric_registrar.examples.kotlin.controllers

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@ExtendWith(SpringExtension::class)
@WebMvcTest
class SimpleControllerTest(@Autowired private val mockMvc: MockMvc) {
    @Test
    fun ok() {
        mockMvc.perform(get("/simple")).andExpect(status().isOk)
    }
}

