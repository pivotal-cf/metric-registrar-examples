package main

import (
    "github.com/gorilla/mux"
    "github.com/prometheus/client_golang/prometheus"
    "github.com/prometheus/client_golang/prometheus/promauto"
    "github.com/prometheus/client_golang/prometheus/promhttp"
    "log"
    "net/http"
    "os"
    "time"
)

var customCounter = promauto.NewCounter(prometheus.CounterOpts{
    Name: "custom",
    Help: "Count number of endpoint calls",
})

var requestDuration = promauto.NewHistogramVec(
    prometheus.HistogramOpts{
        Name: "request_duration_seconds",
        Help: "A histogram of latencies for requests.",
    },
    []string{"code", "method", "handler"},
)

func main() {
    router := mux.NewRouter()
    staticPath := os.Getenv("GOPATH") + "/src/metric_registrar_examples/static"

    router.Handle("/", http.FileServer(http.Dir(staticPath)))
    router.Handle("/metrics", promhttp.Handler())

    router.HandleFunc("/simple", instrument(simple, "simple")).Methods(http.MethodGet)
    router.HandleFunc("/high_latency", instrument(highLatency, "high_latency")).Methods(http.MethodGet)
    router.HandleFunc("/custom_metric", instrument(customMetric, "custom_metric"))

    port := os.Getenv("PORT")
    if port == "" {
        port = "8080"
    }

    log.Fatal(http.ListenAndServe(":"+port, router))
}

func instrument(handlerFunc http.HandlerFunc, name string) http.HandlerFunc {
    handlerDuration, err := requestDuration.CurryWith(prometheus.Labels{
        "handler": name,
    })
    if err != nil {
        panic(err)
    }

    return promhttp.InstrumentHandlerDuration(handlerDuration, handlerFunc)
}

func simple(w http.ResponseWriter, _ *http.Request) {
    w.Write([]byte("{}"))
}

func highLatency(w http.ResponseWriter, _ *http.Request) {
    time.Sleep(2 * time.Second)
    w.Write([]byte("{}"))
}

func customMetric(w http.ResponseWriter, _ *http.Request) {
    customCounter.Inc()
    w.Write([]byte("{}"))
}
