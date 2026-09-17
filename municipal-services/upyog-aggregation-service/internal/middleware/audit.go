package middleware

import (
	"time"

	"github.com/gin-gonic/gin"
	"go.uber.org/zap"

	"github.com/upyog/upyog-aggregation-service/internal/common"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

// auditableMethods is the set of HTTP methods that warrant an audit log entry.
var auditableMethods = map[string]struct{}{
	"POST":   {},
	"PUT":    {},
	"PATCH":  {},
	"DELETE": {},
}

// Audit returns a middleware that logs an audit trail for state-mutating
// requests (POST, PUT, PATCH, DELETE). The log entry is written after the
// request completes and includes the user ID, tenant ID, action (method + path),
// HTTP status, and a timestamp.
func Audit(log *logger.Logger) gin.HandlerFunc {
	return func(c *gin.Context) {
		c.Next()

		method := c.Request.Method
		if _, ok := auditableMethods[method]; !ok {
			return
		}

		ctx := c.Request.Context()
		log.WithContext(ctx).Info("audit",
			zap.String("userId", common.UserID(ctx)),
			zap.String("tenantId", logger.TenantID(ctx)),
			zap.String("action", method+" "+c.Request.URL.Path),
			zap.Int("status", c.Writer.Status()),
			zap.String("timestamp", time.Now().UTC().Format(time.RFC3339)),
		)
	}
}
