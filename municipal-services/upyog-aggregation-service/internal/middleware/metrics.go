package middleware

import (
	"strconv"
	"time"

	"github.com/gin-gonic/gin"

	"github.com/upyog/upyog-aggregation-service/internal/metrics"
)

// HTTPMetrics returns a middleware that records Prometheus metrics for each
// HTTP request: total count, duration (seconds), and response size (bytes).
// Metrics are labelled by method, path, and (for counters) status code.
func HTTPMetrics(m *metrics.Metrics) gin.HandlerFunc {
	return func(c *gin.Context) {
		start := time.Now()
		path := c.FullPath()
		if path == "" {
			path = c.Request.URL.Path
		}
		method := c.Request.Method

		c.Next()

		status := strconv.Itoa(c.Writer.Status())
		duration := time.Since(start).Seconds()
		responseSize := float64(c.Writer.Size())

		m.HTTPRequestsTotal.WithLabelValues(method, path, status).Inc()
		m.HTTPRequestDuration.WithLabelValues(method, path).Observe(duration)
		m.HTTPResponseSize.WithLabelValues(method, path).Observe(responseSize)
	}
}
