# Java Example with Spring Security
Demonstrates how to expose endpoints with Spring Security so they can be reached by `metric-registrar`.

## Running
```bash
./gradlew bootRun
```

## Endpoints
- `/simple` - Returns OK; exercises built-in Micrometer metrics
- `/high_latency` - a slow endpoint to simulate long-running requests
- `custom_metric` - increments the `custom_metric` counter
- `/actuator/metrics` - Micrometer endpoint for metrics
- `/actuator/prometheus` - Prometheus endpoint for metrics

## Running Tests
```bash
./gradlew test
```
