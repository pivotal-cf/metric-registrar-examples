package main

import (
	"log"
	"net/http"
	"os"
	"time"

	"github.com/gorilla/mux"
	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promauto"
	"github.com/prometheus/client_golang/prometheus/promhttp"
)

var simpleCounter = promauto.NewCounter(prometheus.CounterOpts{
	Name: "simple",
	Help: "Increments when /simple handler is called",
})

var customCounter = promauto.NewGauge(prometheus.GaugeOpts{
	Name: "custom",
	Help: "Custom gauge used to scale app instances",
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
	staticPath := "./static"

	router.Handle("/", http.FileServer(http.Dir(staticPath)))
	router.Handle("/metrics", promhttp.Handler())

	router.HandleFunc("/simple", instrument(simple, "simple")).Methods(http.MethodGet)
	router.HandleFunc("/high_latency", instrument(highLatency, "high_latency")).Methods(http.MethodGet)
	router.HandleFunc("/custom_metric", instrument(customMetric, "custom_metric"))

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}
	log.Printf("Starting server on port http://localhost:%s\n", port)
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
	simpleCounter.Inc()
	w.Write([]byte("{}"))
}

func highLatency(w http.ResponseWriter, _ *http.Request) {
	time.Sleep(2 * time.Second)
	w.Write([]byte("{}"))
}

func customMetric(w http.ResponseWriter, r *http.Request) {
	if r.FormValue("inc") != "" {
		customCounter.Inc()
	} else {
		customCounter.Dec()
	}

	w.Write([]byte("{}"))
}
