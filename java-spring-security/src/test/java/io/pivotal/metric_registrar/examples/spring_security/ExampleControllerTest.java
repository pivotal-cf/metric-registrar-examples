package io.pivotal.metric_registrar.examples.spring_security;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest
public class ExampleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeterRegistry registry;

    @Mock
    private Counter counter;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @After
    public void tearDown() {
        validateMockitoUsage();
    }

    @Test
    public void contextLoads() {
        assertNotNull(mockMvc);
    }

    @Test
    public void simpleEndpoint() throws Exception {
        expectOkResponse("/simple");
    }

    @Test
    public void highLatency() throws Exception {
        long startTime = System.currentTimeMillis();
        expectOkResponse("/high_latency");
        assertTrue(System.currentTimeMillis() - startTime > 2000);
    }

    @Test
    public void customMetricEndpoint() throws Exception {
        when(registry.counter(anyString())).thenReturn(counter);
        expectOkResponse("/custom_metric");
        verify(counter).increment();
    }

    private void expectOkResponse(String requestURI) throws Exception {
        RequestBuilder get = servletContext -> new MockHttpServletRequest("GET", requestURI);
        assertNotNull(mockMvc);
        assertNotNull(get);
        mockMvc.perform(get)
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }
}