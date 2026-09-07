package middleware

import (
	"time"

	"github.com/gin-gonic/gin"
	"go.uber.org/zap"

	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

// Logging returns a middleware that logs key request/response information
// after each request completes. It captures method, path, status code, latency,
// client IP, request ID, and user-agent.
func Logging(log *logger.Logger) gin.HandlerFunc {
	return func(c *gin.Context) {
		start := time.Now()
		path := c.Request.URL.Path
		rawQuery := c.Request.URL.RawQuery

		c.Next()

		if rawQuery != "" {
			path = path + "?" + rawQuery
		}

		ctx := c.Request.Context()
		latency := time.Since(start)
		status := c.Writer.Status()

		fields := []zap.Field{
			zap.String("method", c.Request.Method),
			zap.String("path", path),
			zap.Int("status", status),
			zap.Duration("latency", latency),
			zap.String("clientIP", c.ClientIP()),
			zap.String("requestId", logger.RequestID(ctx)),
			zap.String("userAgent", c.Request.UserAgent()),
		}

		switch {
		case status >= 500:
			log.WithContext(ctx).Error("request completed", fields...)
		case status >= 400:
			log.WithContext(ctx).Warn("request completed", fields...)
		default:
			log.WithContext(ctx).Info("request completed", fields...)
		}
	}
}
