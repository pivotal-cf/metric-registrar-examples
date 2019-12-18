package io.pivotal.metric_registrar.examples.spring_actuator_to_logs;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.Iterator;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
public class ActuatorToLogsEmitterTest {
    private ActuatorToLogsEmitter emitter;
    private Collector.MetricFamilySamples.Sample sample = new Collector.MetricFamilySamples.Sample(
            "example",
            asList("label1", "label2", "label3"),
            asList("value1", "value2", "value3"),
            1.0
    );

    @Mock
    CollectorRegistry collectorRegistry;

    @Mock
    Logger logger;

    @Before
    public void setUp() {
        initMocks(this);
        emitter = new ActuatorToLogsEmitter(collectorRegistry, logger);
    }

    @After
    public void tearDown() {
        validateMockitoUsage();
    }

    @Test
    public void emitsGauge() {
        when(collectorRegistry.metricFamilySamples()).thenReturn(Collections.enumeration(
                singletonList(
                        new Collector.MetricFamilySamples("example", Collector.Type.GAUGE, "", singletonList(sample))
                )
        ));

        emitter.emit();

        verify(logger).info(matches(".* \"type\": \"gauge\".*\"value\": 1.0.*"));
    }

    @Test
    public void emitsCounter() {
        when(collectorRegistry.metricFamilySamples()).thenReturn(Collections.enumeration(
                singletonList(
                        new Collector.MetricFamilySamples("example", Collector.Type.COUNTER, "", singletonList(sample))
                )
        ));

        emitter.emit();

        verify(logger).info(matches(".*\"type\": \"counter\".*\"delta\": 1.0.*"));

    }

    @Test
    public void parsesLabelsIntoTags() {
        when(collectorRegistry.metricFamilySamples()).thenReturn(Collections.enumeration(
                singletonList(
                        new Collector.MetricFamilySamples("example", Collector.Type.COUNTER, "", singletonList(sample))
                )
        ));

        emitter.emit();

        verify(logger).info(contains("\"tags\": { \"label1\": \"value1\", \"label2\": \"value2\", \"label3\": \"value3\" }"));
    }

    @Test
    public void doesNotEmitIfTypeIsUnknown() {
        Iterator<Collector.MetricFamilySamples> objectIterator = Collections.emptyIterator();
        when(collectorRegistry.metricFamilySamples()).thenReturn(Collections.enumeration(
                singletonList(
                        new Collector.MetricFamilySamples("example", Collector.Type.HISTOGRAM, "", singletonList(sample))
                )
        ));

        emitter.emit();

        verifyZeroInteractions(logger);
    }

    @Test
    public void doesNotEmitIfNoSamplesPresent() {
        when(collectorRegistry.metricFamilySamples()).thenReturn(Collections.enumeration(emptyList()));

        emitter.emit();

        verifyZeroInteractions(logger);
    }

    @Test
    public void handlesMultipleMetricFamilies() {
        when(collectorRegistry.metricFamilySamples()).thenReturn(Collections.enumeration(
                asList(
                        new Collector.MetricFamilySamples("example", Collector.Type.COUNTER, "", singletonList(sample)),
                        new Collector.MetricFamilySamples("example2", Collector.Type.GAUGE, "", singletonList(sample))
                )
        ));

        emitter.emit();

        verify(logger).info(matches(".*\"type\": \"counter\".*\"name\": \"example\".*"));
        verify(logger).info(matches(".*\"type\": \"gauge\".*\"name\": \"example2\".*"));
        verifyNoMoreInteractions(logger);
    }

    @Test
    public void handlesMultipleSamples() {
        Iterator<Collector.MetricFamilySamples> objectIterator = Collections.emptyIterator();
        when(collectorRegistry.metricFamilySamples()).thenReturn(Collections.enumeration(
                singletonList(
                        new Collector.MetricFamilySamples("example", Collector.Type.COUNTER, "",
                                asList(
                                        new Collector.MetricFamilySamples.Sample("example", singletonList("id"), singletonList("sample1"), 1.0),
                                        new Collector.MetricFamilySamples.Sample("example", singletonList("id"), singletonList("sample2"), 1.0)
                                )
                        )
                )
        ));

        emitter.emit();

        verify(logger).info(contains("\"tags\": { \"id\": \"sample1\" }"));
        verify(logger).info(contains("\"tags\": { \"id\": \"sample2\" }"));
    }
}
