# Kotlin Metric Registrar Example

### Running
1. `cd metric-registrar-examples/kotlin`
1. `./gradlew bootRun`

### Endpoints
- `/` - frontend to trigger endpoints
- `/simple` - Returns OK.
- `/high_latency` - Delays 2 seconds before responding.
- `/custom_metric` - Increments the custom counter.
- `/actuator/prometheus` - Displays Prometheus metrics

### Running Tests
1. `cd metric-registrar-examples/kotlin`
1. `./gradlew test`