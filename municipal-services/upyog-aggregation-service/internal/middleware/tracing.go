package middleware

import (
	"strconv"

	"github.com/gin-gonic/gin"
	"go.opentelemetry.io/otel/attribute"

	"github.com/upyog/upyog-aggregation-service/internal/tracing"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

// Tracing returns a middleware that creates an OpenTelemetry span for each
// incoming HTTP request. It sets the trace ID in the logger context so that
// downstream log lines are automatically correlated, and records the response
// status code as a span attribute.
func Tracing() gin.HandlerFunc {
	return func(c *gin.Context) {
		method := c.Request.Method
		path := c.FullPath()
		if path == "" {
			path = c.Request.URL.Path
		}

		spanName := method + " " + path
		ctx, span := tracing.StartSpan(c.Request.Context(), "http", spanName)
		defer span.End()

		// Inject the trace ID into the context for structured logging.
		traceID := tracing.TraceIDFromContext(ctx)
		if traceID != "" {
			ctx = logger.WithTraceID(ctx, traceID)
		}

		c.Request = c.Request.WithContext(ctx)

		c.Next()

		// Record the HTTP status code on the span for observability.
		span.SetAttributes(attribute.Int("http.status_code", c.Writer.Status()))
		span.SetAttributes(attribute.String("http.method", method))
		span.SetAttributes(attribute.String("http.path", path))
		span.SetAttributes(attribute.String("http.status", strconv.Itoa(c.Writer.Status())))
	}
}
