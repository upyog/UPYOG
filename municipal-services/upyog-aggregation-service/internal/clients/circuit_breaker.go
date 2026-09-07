package clients

import (
	"sync"
	"time"

	apperr "github.com/upyog/upyog-aggregation-service/internal/errors"
)

// CircuitState represents the current state of the circuit breaker.
type CircuitState int

const (
	// StateClosed allows all requests through. Failures are counted.
	StateClosed CircuitState = iota
	// StateHalfOpen allows a single probe request to test recovery.
	StateHalfOpen
	// StateOpen rejects all requests immediately.
	StateOpen
)

// String returns a human-readable representation of the circuit state.
func (s CircuitState) String() string {
	switch s {
	case StateClosed:
		return "closed"
	case StateHalfOpen:
		return "half-open"
	case StateOpen:
		return "open"
	default:
		return "unknown"
	}
}

// CircuitBreaker implements the circuit breaker pattern to prevent cascading
// failures when a downstream service is unhealthy.
type CircuitBreaker struct {
	mu           sync.RWMutex
	state        CircuitState
	failureCount int
	successCount int
	threshold    int
	timeout      time.Duration
	lastFailure  time.Time
}

// NewCircuitBreaker creates a CircuitBreaker that opens after threshold
// consecutive failures and remains open for timeout before allowing a probe.
func NewCircuitBreaker(threshold int, timeout time.Duration) *CircuitBreaker {
	return &CircuitBreaker{
		state:     StateClosed,
		threshold: threshold,
		timeout:   timeout,
	}
}

// Allow checks whether a request is permitted. It returns nil if the request
// may proceed, or an AppError with CodeCircuitOpen if the circuit is open.
func (cb *CircuitBreaker) Allow() error {
	cb.mu.Lock()
	defer cb.mu.Unlock()

	switch cb.state {
	case StateClosed:
		return nil

	case StateOpen:
		if cb.isTimeoutElapsed() {
			cb.state = StateHalfOpen
			cb.successCount = 0
			return nil
		}
		return &apperr.AppError{
			Code:       apperr.CodeCircuitOpen,
			Message:    "circuit breaker is open, request rejected",
			HTTPStatus: 503,
		}

	case StateHalfOpen:
		// Allow one probe request while half-open.
		return nil
	}

	return nil
}

// RecordSuccess records a successful request. In half-open state this resets
// the breaker back to closed.
func (cb *CircuitBreaker) RecordSuccess() {
	cb.mu.Lock()
	defer cb.mu.Unlock()

	cb.failureCount = 0

	if cb.state == StateHalfOpen {
		cb.successCount++
		cb.state = StateClosed
	}
}

// RecordFailure records a failed request. When consecutive failures reach the
// threshold the circuit trips to open.
func (cb *CircuitBreaker) RecordFailure() {
	cb.mu.Lock()
	defer cb.mu.Unlock()

	cb.failureCount++
	cb.lastFailure = time.Now()

	switch cb.state {
	case StateClosed:
		if cb.failureCount >= cb.threshold {
			cb.state = StateOpen
		}
	case StateHalfOpen:
		// Probe failed — reopen.
		cb.state = StateOpen
		cb.failureCount = 0
	}
}

// State returns the current circuit state.
func (cb *CircuitBreaker) State() CircuitState {
	cb.mu.RLock()
	defer cb.mu.RUnlock()

	// Transparently transition from open → half-open when the timeout elapses.
	if cb.state == StateOpen && cb.isTimeoutElapsedUnsafe() {
		return StateHalfOpen
	}
	return cb.state
}

// Reset manually resets the circuit breaker to its initial closed state.
func (cb *CircuitBreaker) Reset() {
	cb.mu.Lock()
	defer cb.mu.Unlock()

	cb.state = StateClosed
	cb.failureCount = 0
	cb.successCount = 0
	cb.lastFailure = time.Time{}
}

// isTimeoutElapsed checks whether enough time has passed since the last failure
// to transition from open to half-open. Caller must hold cb.mu.
func (cb *CircuitBreaker) isTimeoutElapsed() bool {
	return time.Since(cb.lastFailure) >= cb.timeout
}

// isTimeoutElapsedUnsafe is the same check without requiring a write lock.
// Used only from State() which holds a read lock — safe because lastFailure is
// only read here and the caller handles the conceptual transition.
func (cb *CircuitBreaker) isTimeoutElapsedUnsafe() bool {
	return time.Since(cb.lastFailure) >= cb.timeout
}
