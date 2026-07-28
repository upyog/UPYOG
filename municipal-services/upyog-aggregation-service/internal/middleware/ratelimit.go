package middleware

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"golang.org/x/time/rate"

	"github.com/upyog/upyog-aggregation-service/internal/dto"
	"github.com/upyog/upyog-aggregation-service/internal/errors"
	"github.com/upyog/upyog-aggregation-service/internal/tracing"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

// RateLimiter returns a middleware that enforces a global request rate limit
// using a token-bucket algorithm. rps defines the sustained requests per
// second, and burst defines the maximum burst size.
//
// When the limit is exceeded the middleware responds with 429 Too Many Requests
// and the standard ErrorResponseBody.
func RateLimiter(rps float64, burst int) gin.HandlerFunc {
	limiter := rate.NewLimiter(rate.Limit(rps), burst)

	return func(c *gin.Context) {
		if !limiter.Allow() {
			ctx := c.Request.Context()
			appErr := errors.NewRateLimited()

			c.AbortWithStatusJSON(http.StatusTooManyRequests, dto.ErrorResponseBody{
				Success:       false,
				Code:          string(appErr.Code),
				Message:       appErr.Message,
				TraceID:       tracing.TraceIDFromContext(ctx),
				CorrelationID: logger.CorrelationID(ctx),
			})
			return
		}

		c.Next()
	}
}
