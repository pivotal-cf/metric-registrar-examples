# Examples for the Metric Registrar

The examples are organized in the following folders 

* [Java using Spring Security](java-spring-security/)
* [Kotlin](kotlin/)
* [Go](golang/)


## Prerequisites
These plugins will allow you to register & test apps with the Metric Registrar

```
cf install-plugin -r CF-Community "metric-registrar"
cf install-plugin -r CF-Community "log-cache"
```

### Register the endpoint
To register an app's metric endpoint with the Metric Registrar, run:

```
cf register-metrics-endpoint <APP_NAME> /actuator/metrics
```

### Make sure the app is emitting metrics
To look at all the counters emitted by an app, run:

```
cf tail -f --envelope-type=counter <APP_NAME>
```

To look at all the guages emitted by an app, run:

```
cf tail -f --envelope-type=guage <APP_NAME>
```

If you don't see anything contact your platform support to enable the Metric Registrar.

### Want to make a change?

Make sure you run all the tests before submitting a PR
```
./gradlew tests
```