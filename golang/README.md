# Go Metric Registrar Example

### Running
1. `cd metric-registrar-examples/golang`
1. `go get ./...`
1. `go run src metric_registrar_examples/main.go`

### Endpoints
- `/` - frontend to trigger endpoints
- `/simple` - Returns OK.
- `/high_latency` - Delays 2 seconds before responding.
- `/custom_metric` - Increments the custom counter.
- `/metrics` - Displays Prometheus metrics

### Running Tests
1. `cd metric-registrar-examples/golang/src/metric_registrar_examples`
1. `go get -t ./...`
1. `go test ./...` or `ginkgo -r`