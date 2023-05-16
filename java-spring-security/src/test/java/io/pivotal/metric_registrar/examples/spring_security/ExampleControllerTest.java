package io.pivotal.metric_registrar.examples.spring_security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.atomic.AtomicLong;

@WebMvcTest
@WebAppConfiguration
public class ExampleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @MockBean
    private MeterRegistry registry;

    @Mock
    private Counter simpleCounter;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @AfterEach
    public void tearDown() {
        validateMockitoUsage();
    }

    @Test
    public void simpleEndpoint() throws Exception {
        when(registry.counter(anyString())).thenReturn(simpleCounter);
        expectOkResponse("/simple");
        verify(simpleCounter).increment();
    }

    @Test
    public void highLatency() throws Exception {
        long startTime = System.currentTimeMillis();
        expectOkResponse("/high_latency");
        assertTrue(System.currentTimeMillis() - startTime > 2000);
    }

    @Test
    public void customMetricEndpoint() throws Exception {
        AtomicLong customGauge = new AtomicLong(0L);
        when(registry.gauge(anyString(), any())).thenReturn(customGauge);
        expectOkResponse("/custom_metric", "inc", "1");
        assertEquals(1L, customGauge.get());
        expectOkResponse("/custom_metric");
        assertEquals(0L, customGauge.get());
    }

    @Test
    public void htmlHarness() throws Exception {
        mockMvc.perform(servletContext -> new MockHttpServletRequest("GET", "/"))
                .andExpect(status().isOk());
    }

    private void expectOkResponse(String requestURI) throws Exception {
        RequestBuilder get = servletContext -> new MockHttpServletRequest("GET", requestURI);

        mockMvc.perform(get)
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));
    }

    private void expectOkResponse(String requestURI, String name, String value) throws Exception {
        RequestBuilder get = servletContext -> {
            MockHttpServletRequest req = new MockHttpServletRequest("GET", requestURI);
            req.addParameter(name, value);
            return req;
        };

        mockMvc.perform(get)
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));
    }
}