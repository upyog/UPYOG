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

	// ── Authentication (conditional) ──────────────────────────────────
	if cfg.Config.Auth.Enabled {
		r.Use(middleware.Authentication(
			cfg.JWTValidator,
			cfg.Config.Auth.PublicPaths,
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

	// ── Health & observability endpoints ───────────────────────────────
	healthHandler := NewHealthHandler(cfg.Cache, "1.0.0")
	r.GET("/health", healthHandler.Health)
	r.GET("/readiness", healthHandler.Readiness)
	r.GET("/liveness", healthHandler.Liveness)
	r.GET("/metrics", gin.WrapH(promhttp.Handler()))

	// ── API v1 routes ─────────────────────────────────────────────────
	v1 := r.Group("/api/v1")
	{
		aggregateHandler := NewAggregateHandler(cfg.Engine, cfg.Logger)
		v1.POST("/aggregate", aggregateHandler.Handle)
	}

	return r
}
