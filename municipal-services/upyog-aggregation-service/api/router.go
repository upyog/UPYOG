// Package api provides the HTTP router, handler setup, and request routing
// for the aggregation service's REST API.
package api

import (
	"github.com/gin-gonic/gin"
	"github.com/prometheus/client_golang/prometheus/promhttp"

	"github.com/upyog/upyog-aggregation-service/internal/aggregation/engine"
	"github.com/upyog/upyog-aggregation-service/internal/auth"
	"github.com/upyog/upyog-aggregation-service/internal/cache"
	"github.com/upyog/upyog-aggregation-service/internal/config"
	"github.com/upyog/upyog-aggregation-service/internal/metrics"
	"github.com/upyog/upyog-aggregation-service/internal/middleware"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

// RouterConfig holds all dependencies required to set up the HTTP router.
type RouterConfig struct {
	// Engine is the aggregation engine that orchestrates provider execution.
	Engine *engine.Engine
	// Logger is the structured logger instance.
	Logger *logger.Logger
	// Metrics holds the registered Prometheus metrics.
	Metrics *metrics.Metrics
	// JWTValidator validates and parses JWT tokens.
	JWTValidator *auth.JWTValidator
	// Authorizer performs role-based access control.
	Authorizer *auth.Authorizer
	// Config is the application configuration.
	Config *config.Config
	// Cache is the shared Redis cache client.
	Cache *cache.Cache
}

// SetupRouter creates a new Gin engine, registers all middleware in the correct
// order, and mounts the API routes. The returned engine is ready to be passed
// to http.Server.Handler.
func SetupRouter(cfg RouterConfig) *gin.Engine {
	r := gin.New()

	// ── Core middleware (always active) ────────────────────────────────
	r.Use(middleware.Recovery(cfg.Logger))
	r.Use(middleware.RequestID())
	r.Use(middleware.CorrelationID())
	r.Use(middleware.Tracing())
	r.Use(middleware.Logging(cfg.Logger))
	r.Use(middleware.HTTPMetrics(cfg.Metrics))

	// ── Optional compression ──────────────────────────────────────────
	if cfg.Config.Server.EnableCompression {
		r.Use(middleware.Compression())
	}

	// ── Context enrichment ────────────────────────────────────────────
	r.Use(middleware.LocaleResolver())
	r.Use(middleware.TenantResolver(cfg.Logger))
	// Always capture the bearer token for downstream forwarding, even when
	// in-service authentication is disabled (gateway-fronted deployments).
	r.Use(middleware.TokenPassthrough())

	// ── Authentication (conditional) ──────────────────────────────────
	if cfg.Config.Auth.Enabled {
		r.Use(middleware.Authentication(
			cfg.JWTValidator,
			cfg.Config.Auth.PublicPaths,
			cfg.Config.Server.ContextPath,
			cfg.Logger,
		))
	}

	// ── Rate limiting (conditional) ───────────────────────────────────
	if cfg.Config.RateLimit.Enabled {
		r.Use(middleware.RateLimiter(
			cfg.Config.RateLimit.RequestsPS,
			cfg.Config.RateLimit.Burst,
		))
	}

	// ── Audit trail ───────────────────────────────────────────────────
	r.Use(middleware.Audit(cfg.Logger))

	// All routes are mounted under the optional server.contextPath so the
	// service can run behind the UPYOG API gateway, which routes
	// /<context>/** to the pod without stripping the prefix. An empty
	// context path (the default) serves everything at the root.
	base := r.Group(cfg.Config.Server.ContextPath)

	// ── Health & observability endpoints ───────────────────────────────
	healthHandler := NewHealthHandler(cfg.Cache, "1.0.0")
	base.GET("/health", healthHandler.Health)
	base.GET("/readiness", healthHandler.Readiness)
	base.GET("/liveness", healthHandler.Liveness)
	base.GET("/metrics", gin.WrapH(promhttp.Handler()))

	// ── API v1 routes ─────────────────────────────────────────────────
	v1 := base.Group("/api/v1")
	{
		aggregateHandler := NewAggregateHandler(cfg.Engine, cfg.Logger)
		v1.POST("/aggregate", aggregateHandler.Handle)
	}

	return r
}
