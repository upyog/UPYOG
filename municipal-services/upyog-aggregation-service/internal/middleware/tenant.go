package middleware

import (
	"github.com/gin-gonic/gin"

	"github.com/upyog/upyog-aggregation-service/internal/common"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

// TenantResolver returns a middleware that resolves the tenant ID for the
// current request. It first checks the X-Tenant-Id header; if absent, it
// falls back to the tenant ID stored in the context (set by the authentication
// middleware from JWT claims). The middleware does not abort if no tenant is
// found, since some endpoints (e.g., health checks) are tenant-agnostic.
func TenantResolver(log *logger.Logger) gin.HandlerFunc {
	return func(c *gin.Context) {
		tenantID := c.GetHeader(common.HeaderTenantID)

		// Fall back to the tenant ID from JWT claims if the header is empty.
		if tenantID == "" {
			tenantID = logger.TenantID(c.Request.Context())
		}

		if tenantID != "" {
			ctx := logger.WithTenantID(c.Request.Context(), tenantID)
			c.Request = c.Request.WithContext(ctx)
		}

		c.Next()
	}
}
