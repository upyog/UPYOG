package middleware

import (
	"net/http"

	"github.com/gin-gonic/gin"

	"github.com/upyog/upyog-aggregation-service/internal/auth"
	"github.com/upyog/upyog-aggregation-service/internal/dto"
	"github.com/upyog/upyog-aggregation-service/internal/tracing"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

// Authorization returns a middleware that enforces role-based access control.
// The user must hold at least one of the specified roles; otherwise the request
// is aborted with a 403 Forbidden response.
func Authorization(authorizer *auth.Authorizer, roles ...string) gin.HandlerFunc {
	return func(c *gin.Context) {
		ctx := c.Request.Context()

		if err := authorizer.Authorize(ctx, roles...); err != nil {
			c.AbortWithStatusJSON(http.StatusForbidden, dto.ErrorResponseBody{
				Success:       false,
				Code:          "AUTHORIZATION_ERROR",
				Message:       err.Error(),
				TraceID:       tracing.TraceIDFromContext(ctx),
				CorrelationID: logger.CorrelationID(ctx),
			})
			return
		}

		c.Next()
	}
}
