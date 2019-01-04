# Kotlin Metric Registrar Example

## Sample Kotlin App

### Endpoints in the sample app
- `/` - frontend to trigger endpoints
- `/simple` - Returns OK.
- `/high_latency` - Delays 2 seconds before responding.
- `/custom_metric` - Increments the custom counter.
- `/actuator/prometheus` - Displays Prometheus metrics

### Running the sample application
1. `cd metric-registrar-examples/kotlin`
1. `./gradlew bootRun`

#### Running tests for sample app
1. `cd metric-registrar-examples/kotlin`
1. `./gradlew test`

## Register app with Metric Registrar

### Install Plugins
These plugins will allow you to register & test apps with the Metric Registrar

1. `cf install-plugin -r CF-Community "metric-registrar"`
1. `cf install-plugin -r CF-Community "log-cache"`


### Register
1. `cf register-metrics-endpoint <APP_NAME> /actuator/prometheus`

### Test
To look at all the counters emitted by an app, run:

`cf tail -f --envelope-type=counter <APP_NAME>`

To look at all the guages emitted by an app, run:

`cf tail -f --envelope-type=guage <APP_NAME>`

