// Package clients provides resilient HTTP client primitives including retry
// with exponential backoff, circuit breaking, and connection pooling.
package clients

import (
	"context"
	"errors"
	"math/rand"
	"net"
	"time"

	apperr "github.com/upyog/upyog-aggregation-service/internal/errors"
)

// RetryConfig controls the retry behaviour for backend requests.
type RetryConfig struct {
	// MaxRetries is the maximum number of retry attempts (excluding the initial call).
	MaxRetries int
	// InitialDelay is the base delay before the first retry.
	InitialDelay time.Duration
	// MaxDelay caps the backoff so it never exceeds this value.
	MaxDelay time.Duration
	// Multiplier is applied to the delay after each retry.
	Multiplier float64
}

// DefaultRetryConfig returns sensible production defaults.
func DefaultRetryConfig() RetryConfig {
	return RetryConfig{
		MaxRetries:   3,
		InitialDelay: 100 * time.Millisecond,
		MaxDelay:     5 * time.Second,
		Multiplier:   2.0,
	}
}

// Retry executes operation, retrying with exponential backoff and jitter on
// retryable errors. It respects context cancellation between attempts.
func Retry(ctx context.Context, cfg RetryConfig, operation func() error) error {
	var lastErr error
	delay := cfg.InitialDelay

	for attempt := 0; attempt <= cfg.MaxRetries; attempt++ {
		lastErr = operation()
		if lastErr == nil {
			return nil
		}

		// Do not retry non-retryable errors.
		if !isRetryable(lastErr) {
			return lastErr
		}

		// Do not retry if we've exhausted all attempts.
		if attempt == cfg.MaxRetries {
			break
		}

		// Apply jitter: random 0–50 % of current delay.
		jitter := time.Duration(rand.Int63n(int64(delay) / 2)) //nolint:gosec // non-crypto jitter is fine
		sleep := delay + jitter

		select {
		case <-ctx.Done():
			return ctx.Err()
		case <-time.After(sleep):
		}

		// Exponential backoff, capped at MaxDelay.
		delay = time.Duration(float64(delay) * cfg.Multiplier)
		if delay > cfg.MaxDelay {
			delay = cfg.MaxDelay
		}
	}

	return lastErr
}

// isRetryable determines whether an error should be retried.
//
// Retryable conditions:
//   - Network-level errors (dial, DNS, timeout).
//   - Server-side errors (5xx) surfaced via AppError with specific codes.
func isRetryable(err error) bool {
	if err == nil {
		return false
	}

	// Network-level errors are always worth retrying.
	var netErr net.Error
	if errors.As(err, &netErr) {
		return true
	}

	// Application-level retryable codes.
	var appErr *apperr.AppError
	if errors.As(err, &appErr) {
		switch appErr.Code {
		case apperr.CodeBackendUnavailable, apperr.CodeProviderTimeout, apperr.CodeInternal:
			return true
		default:
			return false
		}
	}

	return false
}
