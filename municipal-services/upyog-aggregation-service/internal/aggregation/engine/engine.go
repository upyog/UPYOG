// Package engine implements the core aggregation orchestrator.
// It fans out provider requests concurrently using errgroup and
// collects responses into a single AggregateResponse. A failing
// provider never causes the entire request to fail.
package engine

import (
	"context"
	"sync"

	"go.uber.org/zap"
	"golang.org/x/sync/errgroup"

	"github.com/upyog/upyog-aggregation-service/internal/aggregation/executor"
	"github.com/upyog/upyog-aggregation-service/internal/aggregation/registry"
	"github.com/upyog/upyog-aggregation-service/internal/dto"
	"github.com/upyog/upyog-aggregation-service/internal/metrics"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

// Engine is the top-level aggregation orchestrator.
type Engine struct {
	executor *executor.Executor
	registry *registry.Registry
	log      *logger.Logger
	m        *metrics.Metrics
}

// NewEngine creates an Engine with the supplied dependencies.
func NewEngine(
	exec *executor.Executor,
	reg *registry.Registry,
	log *logger.Logger,
	m *metrics.Metrics,
) *Engine {
	return &Engine{
		executor: exec,
		registry: reg,
		log:      log,
		m:        m,
	}
}

// Aggregate concurrently executes every provider request in req,
// waits for completion, and returns a composite response.
//
// Individual provider failures are captured in their respective
// ProviderResponse entries — the aggregate request as a whole always
// succeeds.
func (e *Engine) Aggregate(ctx context.Context, req dto.AggregateRequest) *dto.AggregateResponse {
	providerCount := len(req.Requests)
	e.m.ProvidersPerRequest.Observe(float64(providerCount))

	e.log.WithContext(ctx).Info("starting aggregation",
		zap.String("requestId", req.RequestID),
		zap.Int("providers", providerCount),
	)

	// Thread-safe map to collect results.
	var results sync.Map

	g, gCtx := errgroup.WithContext(ctx)

	for _, provReq := range req.Requests {
		// Capture loop variable.
		pr := provReq

		g.Go(func() error {
			resp := e.executor.Execute(gCtx, pr, req)
			results.Store(pr.Provider, resp)
			// Never return an error — individual failures are handled per-provider.
			return nil
		})
	}

	// All goroutines return nil, so Wait never returns an error.
	_ = g.Wait()

	// Copy sync.Map into a plain map for serialisation.
	responses := make(map[string]*dto.ProviderResponse, providerCount)
	results.Range(func(key, value any) bool {
		responses[key.(string)] = value.(*dto.ProviderResponse)
		return true
	})

	e.log.WithContext(ctx).Info("aggregation complete",
		zap.String("requestId", req.RequestID),
		zap.Int("responseCount", len(responses)),
	)

	return &dto.AggregateResponse{
		Success:   true,
		RequestID: req.RequestID,
		Responses: responses,
	}
}
