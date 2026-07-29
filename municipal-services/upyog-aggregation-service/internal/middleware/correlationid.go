package middleware

import (
	"github.com/gin-gonic/gin"
	"github.com/google/uuid"

	"github.com/upyog/upyog-aggregation-service/internal/common"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

// CorrelationID returns a middleware that ensures every request carries a
// correlation ID for end-to-end request tracing across services. If the
// X-Correlation-Id header is present, its value is reused; otherwise a new
// UUIDv4 is generated. The ID is stored in the context via
// logger.WithCorrelationID and echoed back in the response header.
func CorrelationID() gin.HandlerFunc {
	return func(c *gin.Context) {
		correlationID := c.GetHeader(common.HeaderCorrelationID)
		if correlationID == "" {
			correlationID = uuid.New().String()
		}

		ctx := logger.WithCorrelationID(c.Request.Context(), correlationID)
		c.Request = c.Request.WithContext(ctx)

		// Echo the correlation ID back to the client.
		c.Header(common.HeaderCorrelationID, correlationID)

		c.Next()
	}
}
