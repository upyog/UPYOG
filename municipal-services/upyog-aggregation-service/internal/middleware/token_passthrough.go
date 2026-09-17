package middleware

import (
	"strings"

	"github.com/gin-gonic/gin"

	"github.com/upyog/upyog-aggregation-service/internal/common"
)

// TokenPassthrough extracts the bearer token from the Authorization header
// and stores it in the request context WITHOUT validating it.
//
// This keeps downstream auth-token forwarding (Authorization header and
// DIGIT RequestInfo.authToken) working in deployments where authentication
// is enforced upstream by the API gateway — e.g. UPYOG/DIGIT clusters, where
// access tokens are opaque UUIDs that cannot be validated as JWTs by this
// service — and the in-service Authentication middleware is therefore
// disabled. When Authentication is enabled it runs after this middleware and
// re-stores the token along with the validated identity claims.
func TokenPassthrough() gin.HandlerFunc {
	const bearerPrefix = "Bearer "

	return func(c *gin.Context) {
		authHeader := c.GetHeader(common.HeaderAuthorization)
		if strings.HasPrefix(authHeader, bearerPrefix) {
			if token := strings.TrimPrefix(authHeader, bearerPrefix); token != "" {
				ctx := common.WithAuthToken(c.Request.Context(), token)
				c.Request = c.Request.WithContext(ctx)
			}
		}
		c.Next()
	}
}
