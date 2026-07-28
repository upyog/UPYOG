package middleware

import (
	"github.com/gin-gonic/gin"
	"github.com/google/uuid"

	"github.com/upyog/upyog-aggregation-service/internal/common"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

// RequestID returns a middleware that ensures every request carries a unique
// request ID. If the X-Request-Id header is present, its value is reused;
// otherwise a new UUIDv4 is generated. The ID is stored in the context via
// logger.WithRequestID and echoed back in the response header.
func RequestID() gin.HandlerFunc {
	return func(c *gin.Context) {
		requestID := c.GetHeader(common.HeaderRequestID)
		if requestID == "" {
			requestID = uuid.New().String()
		}

		ctx := logger.WithRequestID(c.Request.Context(), requestID)
		c.Request = c.Request.WithContext(ctx)

		// Echo the request ID back to the client.
		c.Header(common.HeaderRequestID, requestID)

		c.Next()
	}
}
