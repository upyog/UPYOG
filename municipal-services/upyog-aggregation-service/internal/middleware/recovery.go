// Package middleware provides HTTP middleware components for the aggregation
// service's Gin-based API layer. Each middleware is a self-contained handler
// factory that returns a gin.HandlerFunc.
package middleware

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"go.uber.org/zap"

	"github.com/upyog/upyog-aggregation-service/internal/dto"
	"github.com/upyog/upyog-aggregation-service/internal/tracing"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

// Recovery returns a middleware that catches panics, logs the stack trace at
// Error level, and responds with a 500 Internal Server Error in the standard
// ErrorResponseBody format. This must be the outermost middleware in the chain.
func Recovery(log *logger.Logger) gin.HandlerFunc {
	return func(c *gin.Context) {
		defer func() {
			if err := recover(); err != nil {
				ctx := c.Request.Context()
				log.WithContext(ctx).Error(
					"panic recovered",
					zap.Any("error", err),
					zap.Stack("stacktrace"),
				)

				traceID := tracing.TraceIDFromContext(ctx)
				correlationID := logger.CorrelationID(ctx)

				c.AbortWithStatusJSON(http.StatusInternalServerError, dto.ErrorResponseBody{
					Success:       false,
					Code:          "INTERNAL_ERROR",
					Message:       "an unexpected error occurred",
					TraceID:       traceID,
					CorrelationID: correlationID,
				})
			}
		}()

		c.Next()
	}
}
