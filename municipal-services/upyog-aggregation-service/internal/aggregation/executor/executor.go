// Package executor provides the single-provider execution layer.
// It resolves a provider from the registry, enforces per-provider
// timeouts, records metrics, and translates errors into the
// appropriate ProviderResponse status.
package executor

import (
	"context"
	"errors"
	"fmt"
	"strconv"
	"time"

	"go.opentelemetry.io/otel/attribute"
	"go.uber.org/zap"

	"github.com/upyog/upyog-aggregation-service/internal/aggregation/registry"
	"github.com/upyog/upyog-aggregation-service/internal/common"
	"github.com/upyog/upyog-aggregation-service/internal/dto"
	apperrors "github.com/upyog/upyog-aggregation-service/internal/errors"
	"github.com/upyog/upyog-aggregation-service/internal/metrics"
	"github.com/upyog/upyog-aggregation-service/internal/tracing"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

const tracerName = "upyog.aggregation.executor"

// Executor wraps individual provider invocations with timeout enforcement,
// distributed tracing, and Prometheus metrics.
type Executor struct {
	registry         *registry.Registry
	log              *logger.Logger
	m                *metrics.Metrics
	defaultTimeout   time.Duration
	providerTimeouts map[string]time.Duration
}

// NewExecutor creates an Executor.
//
// providerTimeouts overrides the default timeout for specific providers.
// A nil map is safe — the default timeout is used for every provider.
func NewExecutor(
	reg *registry.Registry,
	log *logger.Logger,
	m *metrics.Metrics,
	defaultTimeout time.Duration,
	providerTimeouts map[string]time.Duration,
) *Executor {
	if providerTimeouts == nil {
		providerTimeouts = make(map[string]time.Duration)
	}
	return &Executor{
		registry:         reg,
		log:              log,
		m:                m,
		defaultTimeout:   defaultTimeout,
		providerTimeouts: providerTimeouts,
	}
}

// Execute resolves the named provider, runs it within a scoped timeout,
// and returns a fully-populated ProviderResponse regardless of outcome.
func (e *Executor) Execute(
	ctx context.Context,
	provReq dto.ProviderRequest,
	aggReq dto.AggregateRequest,
) *dto.ProviderResponse {
	providerName := provReq.Provider

	// Resolve provider.
	provider, err := e.registry.Resolve(providerName)
	if err != nil {
		e.m.ProviderRequestsTotal.WithLabelValues(providerName, common.StatusFailed).Inc()
		var appErr *apperrors.AppError
		if errors.As(err, &appErr) {
			return &dto.ProviderResponse{
				Status:    common.StatusFailed,
				ErrorCode: string(appErr.Code),
				Message:   appErr.Message,
			}
		}
		return &dto.ProviderResponse{
			Status:    common.StatusFailed,
			ErrorCode: string(apperrors.CodeProviderNotFound),
			Message:   fmt.Sprintf("provider '%s' is not registered", providerName),
		}
	}

	// Determine per-provider timeout.
	timeout := e.defaultTimeout
	if t, ok := e.providerTimeouts[providerName]; ok {
		timeout = t
	}

	childCtx, cancel := context.WithTimeout(ctx, timeout)
	defer cancel()

	// Start a tracing span.
	childCtx, span := tracing.StartSpan(childCtx, tracerName, "provider."+providerName,
		attribute.String("provider.name", providerName),
		attribute.String("tenant.id", aggReq.TenantID),
	)
	defer span.End()

	// Execute with timing.
	start := time.Now()
	resp, execErr := provider.Execute(childCtx, provReq, aggReq)
	executionMs := time.Since(start).Milliseconds()

	cachedLabel := "false"
	if resp != nil && resp.Cached {
		cachedLabel = "true"
	}

	if execErr != nil {
		status := common.StatusFailed
		errorCode := string(apperrors.CodeProviderFailed)
		message := execErr.Error()

		if errors.Is(execErr, context.DeadlineExceeded) || errors.Is(childCtx.Err(), context.DeadlineExceeded) {
			status = common.StatusTimeout
			errorCode = string(apperrors.CodeProviderTimeout)
			message = fmt.Sprintf("provider '%s' timed out after %s", providerName, timeout)
		}

		e.log.WithContext(ctx).Warn("provider execution failed",
			zap.String("provider", providerName),
			zap.String("status", status),
			zap.Int64("executionTimeMs", executionMs),
			zap.Error(execErr),
		)

		e.m.ProviderExecutionDuration.
			WithLabelValues(providerName, status, cachedLabel).
			Observe(float64(executionMs) / 1000.0)
		e.m.ProviderRequestsTotal.WithLabelValues(providerName, status).Inc()

		return &dto.ProviderResponse{
			Status:        status,
			ExecutionTime: executionMs,
			ErrorCode:     errorCode,
			Message:       message,
		}
	}

	// Success path.
	if resp == nil {
		resp = &dto.ProviderResponse{}
	}
	resp.Status = common.StatusSuccess
	resp.ExecutionTime = executionMs

	e.log.WithContext(ctx).Debug("provider executed successfully",
		zap.String("provider", providerName),
		zap.Int64("executionTimeMs", executionMs),
		zap.String("cached", strconv.FormatBool(resp.Cached)),
	)

	e.m.ProviderExecutionDuration.
		WithLabelValues(providerName, common.StatusSuccess, cachedLabel).
		Observe(float64(executionMs) / 1000.0)
	e.m.ProviderRequestsTotal.WithLabelValues(providerName, common.StatusSuccess).Inc()

	return resp
}
