package middleware

import (
	"net/http"
	"strings"

	"github.com/gin-gonic/gin"

	"github.com/upyog/upyog-aggregation-service/internal/auth"
	"github.com/upyog/upyog-aggregation-service/internal/common"
	"github.com/upyog/upyog-aggregation-service/internal/dto"
	"github.com/upyog/upyog-aggregation-service/internal/tracing"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

// Authentication returns a middleware that validates JWT Bearer tokens on every
// request except those matching publicPaths. On success it populates the
// request context with the user's identity (ID, roles, auth token, and type).
func Authentication(jwtValidator *auth.JWTValidator, publicPaths []string, log *logger.Logger) gin.HandlerFunc {
	publicSet := make(map[string]struct{}, len(publicPaths))
	for _, p := range publicPaths {
		publicSet[p] = struct{}{}
	}

	return func(c *gin.Context) {
		// Skip authentication for declared public paths.
		if _, ok := publicSet[c.Request.URL.Path]; ok {
			c.Next()
			return
		}

		authHeader := c.GetHeader(common.HeaderAuthorization)
		if authHeader == "" {
			abortUnauthorized(c, "missing authorization header")
			return
		}

		const bearerPrefix = "Bearer "
		if !strings.HasPrefix(authHeader, bearerPrefix) {
			abortUnauthorized(c, "authorization header must use Bearer scheme")
			return
		}

		tokenString := strings.TrimPrefix(authHeader, bearerPrefix)
		if tokenString == "" {
			abortUnauthorized(c, "empty bearer token")
			return
		}

		claims, err := jwtValidator.ParseToken(tokenString)
		if err != nil {
			log.WithContext(c.Request.Context()).Warn("JWT parse failed: " + err.Error())
			abortUnauthorized(c, "invalid token")
			return
		}

		if err := jwtValidator.ValidateClaims(claims); err != nil {
			log.WithContext(c.Request.Context()).Warn("JWT validation failed: " + err.Error())
			abortUnauthorized(c, "token validation failed")
			return
		}

		// Enrich the request context with identity fields.
		ctx := c.Request.Context()
		ctx = common.WithAuthToken(ctx, tokenString)
		ctx = common.WithUserID(ctx, claims.Sub)
		ctx = common.WithUserRoles(ctx, claims.RealmAccess.Roles)
		ctx = common.WithUserType(ctx, claims.PreferredUsername)
		c.Request = c.Request.WithContext(ctx)

		c.Next()
	}
}

// abortUnauthorized aborts the request with a 401 Unauthorized response.
func abortUnauthorized(c *gin.Context, message string) {
	ctx := c.Request.Context()
	c.AbortWithStatusJSON(http.StatusUnauthorized, dto.ErrorResponseBody{
		Success:       false,
		Code:          "AUTHENTICATION_ERROR",
		Message:       message,
		TraceID:       tracing.TraceIDFromContext(ctx),
		CorrelationID: logger.CorrelationID(ctx),
	})
}
