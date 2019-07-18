# Java Example with Spring Security
Demonstrates how to expose endpoints with Spring Security so they can be reached by `metric-registrar`.

## Running the app locally
```bash
cd metric-registrar-examples/java-spring-security
./gradlew bootRun
```

### Push the app
```
cd metric-registrar-examples/java-spring-security
./gradlew build
cf push
```

## Endpoints
- `/simple` - Returns OK; exercises built-in Micrometer metrics
- `/high_latency` - a slow endpoint to simulate long-running requests
- `custom_metric` - increments the `custom_metric` counter
- `/actuator/prometheus` - Prometheus endpoint for metrics

## Running Tests
```bash
./gradlew test
```
