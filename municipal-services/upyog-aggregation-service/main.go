package main

import (
	"context"
	"fmt"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"go.uber.org/zap"

	"github.com/upyog/upyog-aggregation-service/api"
	"github.com/upyog/upyog-aggregation-service/internal/aggregation/engine"
	"github.com/upyog/upyog-aggregation-service/internal/aggregation/executor"
	"github.com/upyog/upyog-aggregation-service/internal/aggregation/registry"
	"github.com/upyog/upyog-aggregation-service/internal/auth"
	"github.com/upyog/upyog-aggregation-service/internal/cache"
	"github.com/upyog/upyog-aggregation-service/internal/clients"
	"github.com/upyog/upyog-aggregation-service/internal/config"
	"github.com/upyog/upyog-aggregation-service/internal/metrics"
	"github.com/upyog/upyog-aggregation-service/internal/providers"
	"github.com/upyog/upyog-aggregation-service/internal/tracing"
	"github.com/upyog/upyog-aggregation-service/internal/validator"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

func main() {
	log := logger.New(os.Getenv("APP_ENV"))
	defer func() { _ = log.Sync() }()

	cfg, err := config.Load("./configs")
	if err != nil {
		log.Fatal("failed to load config", zap.Error(err))
	}

	// Tracing.
	tp, err := tracing.Init(
		context.Background(),
		cfg.Observability.Tracing.ServiceName,
		cfg.Observability.Tracing.Endpoint,
		cfg.Observability.Tracing.SamplingRate,
	)
	if err != nil {
		log.Fatal("failed to init tracing", zap.Error(err))
	}
	defer func() {
		ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
		defer cancel()
		_ = tp.Shutdown(ctx)
	}()

	// Metrics.
	m := metrics.New(
		cfg.Observability.Metrics.Namespace,
		cfg.Observability.Metrics.Subsystem,
	)

	// Cache.
	var c *cache.Cache
	if cfg.Redis.Enabled {
		c, err = cache.NewCache(cfg.Redis, cache.DefaultCacheConfig(), log, m)
		if err != nil {
			log.Warn("redis unavailable, cache disabled", zap.Error(err))
		}
	}

	// Validator.
	validator.Setup()

	// Registry + providers.
	reg := registry.NewRegistry()

	// newServiceClient is a helper that builds a Client from a named entry in
	// backend.services. If the entry is missing from config it logs a warning
	// and returns a client with an empty BaseURL (requests will fail fast).
	newServiceClient := func(key, serviceName string) *clients.Client {
		ep, ok := cfg.Backend.Services[key]
		if !ok {
			log.Warn("backend service not configured, provider calls will fail",
				zap.String("key", key))
		}
		return clients.NewClient(clients.ClientConfig{
			ServiceName:      serviceName,
			BaseURL:          ep.BaseURL,
			Timeout:          ep.Timeout,
			MaxConns:         ep.MaxConns,
			CircuitThreshold: ep.CircuitThreshold,
			CircuitTimeout:   ep.CircuitTimeout,
		}, log, m)
	}

	// One client per backend service, matching the entries in
	// backend.services in the application config.
	inboxClient       := newServiceClient("inbox", "inbox")
	billingClient     := newServiceClient("billing", "billing")
	userEventClient   := newServiceClient("user-event", "egov-user-event")
	advertisementClient := newServiceClient("advertisement", "advertisement-service")
	draftClient       := newServiceClient("draft", "upyog-draft-service")
	workflowClient    := newServiceClient("workflow", "egov-workflow-v2")
	chbClient         := newServiceClient("chb", "chb-services")

	cacheTTL := cfg.Providers.CacheTTL
	reg.Register(providers.NewQuickSummaryProvider(inboxClient, billingClient, draftClient, workflowClient, advertisementClient, chbClient, c, log, m, cacheTTL, cfg.Providers.CompletedServiceStatuses))
	reg.Register(providers.NewRecentApplicationsProvider(workflowClient, advertisementClient, chbClient, c, log, m, cacheTTL, cfg.Providers.RecentApplicationsSinceDays))
	reg.Register(providers.NewNotificationsProvider(userEventClient, c, log, m, cacheTTL))
	reg.Register(providers.NewDraftApplicationsProvider(draftClient, c, log, m, cacheTTL))
	// Replaced tlServicesClient with billingClient to fetch due renewals from billing-service
	reg.Register(providers.NewDueRenewalsProvider(billingClient, c, log, m, cacheTTL))
	reg.Register(providers.NewUpcomingEventsProvider(userEventClient, c, log, m, cacheTTL))
	reg.Register(providers.NewAdvertisementBannersProvider(advertisementClient, c, log, m, cacheTTL))
	reg.Register(providers.NewNewApplicationsProvider(workflowClient, c, log, m, cacheTTL))

	// Build per-provider timeout map.
	providerTimeouts := make(map[string]time.Duration, len(cfg.Providers.Custom))
	for name, custom := range cfg.Providers.Custom {
		if custom.Timeout > 0 {
			providerTimeouts[name] = custom.Timeout
		}
	}

	exec := executor.NewExecutor(reg, log, m, cfg.Providers.DefaultTimeout, providerTimeouts)
	eng := engine.NewEngine(exec, reg, log, m)

	// Auth.
	jwtValidator := auth.NewJWTValidator(cfg.Auth.Issuer, cfg.Auth.Audience, cfg.Auth.TokenExpLeeway)
	authorizer := auth.NewAuthorizer()

	// Router.
	router := api.SetupRouter(api.RouterConfig{
		Engine:       eng,
		Logger:       log,
		Metrics:      m,
		JWTValidator: jwtValidator,
		Authorizer:   authorizer,
		Config:       cfg,
		Cache:        c,
	})

	srv := &http.Server{
		Addr:         fmt.Sprintf(":%d", cfg.Server.Port),
		Handler:      router,
		ReadTimeout:  cfg.Server.ReadTimeout,
		WriteTimeout: cfg.Server.WriteTimeout,
		IdleTimeout:  cfg.Server.IdleTimeout,
	}

	// Start server.
	go func() {
		log.Info("starting server", zap.Int("port", cfg.Server.Port))
		if err := srv.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			log.Fatal("server error", zap.Error(err))
		}
	}()

	// Graceful shutdown.
	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit

	log.Info("shutting down server")
	ctx, cancel := context.WithTimeout(context.Background(), cfg.Server.ShutdownTimeout)
	defer cancel()

	if err := srv.Shutdown(ctx); err != nil {
		log.Error("server forced to shutdown", zap.Error(err))
	}

	if c != nil {
		_ = c.Close()
	}

	log.Info("server stopped")
}
