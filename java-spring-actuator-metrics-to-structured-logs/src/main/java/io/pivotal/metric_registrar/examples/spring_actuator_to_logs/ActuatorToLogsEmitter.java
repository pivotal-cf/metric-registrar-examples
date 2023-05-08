package io.pivotal.metric_registrar.examples.spring_actuator_to_logs;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Service
public class ActuatorToLogsEmitter {
    // This is the metric registry used by /actuator/prometheus.
    // Source code in org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint.java
    private CollectorRegistry collectorRegistry;
    private Logger logger;

    public ActuatorToLogsEmitter(CollectorRegistry collectorRegistry, Logger logger) {
        this.collectorRegistry = collectorRegistry;
        this.logger = logger;
    }

    //The rate below (15000) could be refactored into an environment variable if you wanted to update this without redeploying the app
    @Scheduled(fixedRate = 15000)
    public void emit() {
        Enumeration<Collector.MetricFamilySamples> metrics = collectorRegistry.metricFamilySamples();

        while (metrics.hasMoreElements()) {
            // A metric family contains information such as metric name and type
            Collector.MetricFamilySamples metricFamily = metrics.nextElement();

            // A metric family contains 1..n samples, where a sample has a value and labels
            metricFamily.samples.forEach(sample -> {
                String metricAsJson = convertToStructuredJson(metricFamily, sample);
                if (metricAsJson != null) {
                    logger.info(metricAsJson);
                }
            });
        }
    }

    private String convertToStructuredJson(Collector.MetricFamilySamples metricFamily, Collector.MetricFamilySamples.Sample sample) {
        String nameAndTags = String.format("\"name\": \"%s\", \"tags\": { %s } }", metricFamily.name, buildTags(sample));

        switch (metricFamily.type) {
            case GAUGE:
                return String.format("{ \"type\": \"gauge\", \"value\": %s, %s", sample.value, nameAndTags);
            case COUNTER:
                return String.format("{ \"type\": \"counter\", \"delta\": %s, %s", (int)(sample.value), nameAndTags);
            default:
                return null;
        }
    }

    private String buildTags(Collector.MetricFamilySamples.Sample sample) {
        List<String> tags = new ArrayList<>();
        List<String> labelNames = sample.labelNames;
        List<String> labelValues = sample.labelValues;

        for (int i = 0; i < labelNames.size(); i++) {
            tags.add(String.format("\"%s\": \"%s\"", labelNames.get(i), labelValues.get(i)));
        }

        return String.join(", ", tags);
    }
}
