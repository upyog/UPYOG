// Package metrics provides Prometheus metrics registration and helpers
// for the aggregation service. All custom metrics are registered here
// and reused across the codebase.
package metrics

import (
	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promauto"
)

// Metrics holds all registered Prometheus metrics for the service.
type Metrics struct {
	// HTTP metrics.
	HTTPRequestsTotal   *prometheus.CounterVec
	HTTPRequestDuration *prometheus.HistogramVec
	HTTPResponseSize    *prometheus.HistogramVec

	// Provider metrics.
	ProviderExecutionDuration *prometheus.HistogramVec
	ProviderRequestsTotal     *prometheus.CounterVec
	ProviderErrorsTotal       *prometheus.CounterVec
	ProvidersPerRequest       prometheus.Histogram

	// Cache metrics.
	CacheHitsTotal   *prometheus.CounterVec
	CacheMissesTotal *prometheus.CounterVec
	CacheErrorsTotal *prometheus.CounterVec

	// Circuit breaker metrics.
	CircuitBreakerState   *prometheus.GaugeVec
	CircuitBreakerTrips   *prometheus.CounterVec

	// Backend client metrics.
	BackendRequestDuration *prometheus.HistogramVec
	BackendRequestsTotal   *prometheus.CounterVec
	BackendRetriesTotal    *prometheus.CounterVec
}

// New creates and registers all Prometheus metrics.
func New(namespace, subsystem string) *Metrics {
	return &Metrics{
		HTTPRequestsTotal: promauto.NewCounterVec(
			prometheus.CounterOpts{
				Namespace: namespace,
				Subsystem: subsystem,
				Name:      "http_requests_total",
				Help:      "Total number of HTTP requests by method, path, and status code.",
			},
			[]string{"method", "path", "status"},
		),
		HTTPRequestDuration: promauto.NewHistogramVec(
			prometheus.HistogramOpts{
				Namespace: namespace,
				Subsystem: subsystem,
				Name:      "http_request_duration_seconds",
				Help:      "HTTP request duration in seconds.",
				Buckets:   []float64{0.01, 0.025, 0.05, 0.1, 0.25, 0.5, 1, 2.5, 5, 10},
			},
			[]string{"method", "path"},
		),
		HTTPResponseSize: promauto.NewHistogramVec(
			prometheus.HistogramOpts{
				Namespace: namespace,
				Subsystem: subsystem,
				Name:      "http_response_size_bytes",
				Help:      "HTTP response size in bytes.",
				Buckets:   prometheus.ExponentialBuckets(100, 10, 7),
			},
			[]string{"method", "path"},
		),
		ProviderExecutionDuration: promauto.NewHistogramVec(
			prometheus.HistogramOpts{
				Namespace: namespace,
				Subsystem: subsystem,
				Name:      "provider_execution_duration_seconds",
				Help:      "Provider execution duration in seconds.",
				Buckets:   []float64{0.005, 0.01, 0.025, 0.05, 0.1, 0.25, 0.5, 1, 2, 5},
			},
			[]string{"provider", "status", "cached"},
		),
		ProviderRequestsTotal: promauto.NewCounterVec(
			prometheus.CounterOpts{
				Namespace: namespace,
				Subsystem: subsystem,
				Name:      "provider_requests_total",
				Help:      "Total number of provider executions by provider and status.",
			},
			[]string{"provider", "status"},
		),
		ProviderErrorsTotal: promauto.NewCounterVec(
			prometheus.CounterOpts{
				Namespace: namespace,
				Subsystem: subsystem,
				Name:      "provider_errors_total",
				Help:      "Total number of provider errors by provider and error code.",
			},
			[]string{"provider", "error_code"},
		),
		ProvidersPerRequest: promauto.NewHistogram(
			prometheus.HistogramOpts{
				Namespace: namespace,
				Subsystem: subsystem,
				Name:      "providers_per_request",
				Help:      "Number of providers requested per aggregation call.",
				Buckets:   []float64{1, 2, 3, 5, 7, 10, 15, 20},
			},
		),
		CacheHitsTotal: promauto.NewCounterVec(
			prometheus.CounterOpts{
				Namespace: namespace,
				Subsystem: subsystem,
				Name:      "cache_hits_total",
				Help:      "Total number of cache hits by provider.",
			},
			[]string{"provider"},
		),
		CacheMissesTotal: promauto.NewCounterVec(
			prometheus.CounterOpts{
				Namespace: namespace,
				Subsystem: subsystem,
				Name:      "cache_misses_total",
				Help:      "Total number of cache misses by provider.",
			},
			[]string{"provider"},
		),
		CacheErrorsTotal: promauto.NewCounterVec(
			prometheus.CounterOpts{
				Namespace: namespace,
				Subsystem: subsystem,
				Name:      "cache_errors_total",
				Help:      "Total number of cache errors by provider.",
			},
			[]string{"provider"},
		),
		CircuitBreakerState: promauto.NewGaugeVec(
			prometheus.GaugeOpts{
				Namespace: namespace,
				Subsystem: subsystem,
				Name:      "circuit_breaker_state",
				Help:      "Current state of the circuit breaker (0=closed, 1=half-open, 2=open).",
			},
			[]string{"service"},
		),
		CircuitBreakerTrips: promauto.NewCounterVec(
			prometheus.CounterOpts{
				Namespace: namespace,
				Subsystem: subsystem,
				Name:      "circuit_breaker_trips_total",
				Help:      "Total number of circuit breaker trips by service.",
			},
			[]string{"service"},
		),
		BackendRequestDuration: promauto.NewHistogramVec(
			prometheus.HistogramOpts{
				Namespace: namespace,
				Subsystem: subsystem,
				Name:      "backend_request_duration_seconds",
				Help:      "Backend HTTP request duration in seconds.",
				Buckets:   []float64{0.01, 0.025, 0.05, 0.1, 0.25, 0.5, 1, 2.5, 5, 10},
			},
			[]string{"service", "method", "status"},
		),
		BackendRequestsTotal: promauto.NewCounterVec(
			prometheus.CounterOpts{
				Namespace: namespace,
				Subsystem: subsystem,
				Name:      "backend_requests_total",
				Help:      "Total backend HTTP requests by service, method, and status.",
			},
			[]string{"service", "method", "status"},
		),
		BackendRetriesTotal: promauto.NewCounterVec(
			prometheus.CounterOpts{
				Namespace: namespace,
				Subsystem: subsystem,
				Name:      "backend_retries_total",
				Help:      "Total number of backend request retries by service.",
			},
			[]string{"service"},
		),
	}
}
