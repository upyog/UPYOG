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
//
// contextPath is the optional base prefix under which all routes are mounted
// (e.g. "/upyog-aggregation-service"). When non-empty, the skip-set is built
// from both the bare path ("/liveness") and the prefixed path
// ("/upyog-aggregation-service/liveness") so that health/metrics probes are
// exempt regardless of whether callers include the prefix in publicPaths.
func Authentication(jwtValidator *auth.JWTValidator, publicPaths []string, contextPath string, log *logger.Logger) gin.HandlerFunc {
	// Normalise contextPath: strip trailing slash, keep leading slash.
	contextPath = strings.TrimRight(contextPath, "/")

	publicSet := make(map[string]struct{}, len(publicPaths)*2)
	for _, p := range publicPaths {
		// Always register the bare path as supplied.
		publicSet[p] = struct{}{}
		// Also register the context-path-prefixed variant so the check works
		// whether the caller has already included the prefix in publicPaths or
		// not, and whether the gateway strips the prefix or not.
		if contextPath != "" && !strings.HasPrefix(p, contextPath) {
			publicSet[contextPath+p] = struct{}{}
		}
	}

	return func(c *gin.Context) {
		// Skip authentication for declared public paths (bare or prefixed).
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
