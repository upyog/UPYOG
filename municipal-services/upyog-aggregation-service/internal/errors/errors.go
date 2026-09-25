// Package errors provides standardized error types and the error response contract
// for the aggregation service. All errors flowing through the service should use
// these types to ensure consistent API responses and structured logging.
package errors

import (
	"fmt"
	"net/http"
)

// Code represents a machine-readable error code.
type Code string

const (
	// CodeInternal indicates an unexpected internal server error.
	CodeInternal Code = "INTERNAL_ERROR"
	// CodeValidation indicates a request validation failure.
	CodeValidation Code = "VALIDATION_ERROR"
	// CodeAuthentication indicates an authentication failure.
	CodeAuthentication Code = "AUTHENTICATION_ERROR"
	// CodeAuthorization indicates an authorization failure.
	CodeAuthorization Code = "AUTHORIZATION_ERROR"
	// CodeProviderFailed indicates a data provider execution failure.
	CodeProviderFailed Code = "PROVIDER_FAILED"
	// CodeProviderNotFound indicates a requested provider is not registered.
	CodeProviderNotFound Code = "PROVIDER_NOT_FOUND"
	// CodeProviderTimeout indicates a provider execution timeout.
	CodeProviderTimeout Code = "PROVIDER_TIMEOUT"
	// CodeBackendUnavailable indicates a backend service is unavailable.
	CodeBackendUnavailable Code = "BACKEND_UNAVAILABLE"
	// CodeCircuitOpen indicates the circuit breaker for a backend is open.
	CodeCircuitOpen Code = "CIRCUIT_OPEN"
	// CodeRateLimited indicates the client has been rate limited.
	CodeRateLimited Code = "RATE_LIMITED"
	// CodeCacheError indicates a cache operation failure.
	CodeCacheError Code = "CACHE_ERROR"
	// CodeBadRequest indicates a malformed request.
	CodeBadRequest Code = "BAD_REQUEST"
)

// ErrorResponse is the standard error response contract returned by the API.
type ErrorResponse struct {
	Success       bool   `json:"success"`
	Code          Code   `json:"code"`
	Message       string `json:"message"`
	TraceID       string `json:"traceId,omitempty"`
	CorrelationID string `json:"correlationId,omitempty"`
}

// AppError is the base error type for all application errors.
type AppError struct {
	Code       Code
	Message    string
	HTTPStatus int
	Cause      error
}

// Error implements the error interface.
func (e *AppError) Error() string {
	if e.Cause != nil {
		return fmt.Sprintf("[%s] %s: %v", e.Code, e.Message, e.Cause)
	}
	return fmt.Sprintf("[%s] %s", e.Code, e.Message)
}

// Unwrap returns the underlying cause for errors.Is/As support.
func (e *AppError) Unwrap() error {
	return e.Cause
}

// NewInternal creates an internal server error.
func NewInternal(message string, cause error) *AppError {
	return &AppError{
		Code:       CodeInternal,
		Message:    message,
		HTTPStatus: http.StatusInternalServerError,
		Cause:      cause,
	}
}

// NewValidation creates a validation error.
func NewValidation(message string) *AppError {
	return &AppError{
		Code:       CodeValidation,
		Message:    message,
		HTTPStatus: http.StatusBadRequest,
	}
}

// NewAuthentication creates an authentication error.
func NewAuthentication(message string) *AppError {
	return &AppError{
		Code:       CodeAuthentication,
		Message:    message,
		HTTPStatus: http.StatusUnauthorized,
	}
}

// NewAuthorization creates an authorization error.
func NewAuthorization(message string) *AppError {
	return &AppError{
		Code:       CodeAuthorization,
		Message:    message,
		HTTPStatus: http.StatusForbidden,
	}
}

// NewProviderFailed creates a provider failure error.
func NewProviderFailed(provider string, cause error) *AppError {
	return &AppError{
		Code:       CodeProviderFailed,
		Message:    fmt.Sprintf("provider '%s' execution failed", provider),
		HTTPStatus: http.StatusOK, // Partial response — overall request succeeds.
		Cause:      cause,
	}
}

// NewProviderNotFound creates a provider-not-found error.
func NewProviderNotFound(provider string) *AppError {
	return &AppError{
		Code:       CodeProviderNotFound,
		Message:    fmt.Sprintf("provider '%s' is not registered", provider),
		HTTPStatus: http.StatusBadRequest,
	}
}

// NewProviderTimeout creates a provider timeout error.
func NewProviderTimeout(provider string) *AppError {
	return &AppError{
		Code:       CodeProviderTimeout,
		Message:    fmt.Sprintf("provider '%s' timed out", provider),
		HTTPStatus: http.StatusOK, // Partial response — overall request succeeds.
	}
}

// NewBackendUnavailable creates a backend-unavailable error.
func NewBackendUnavailable(service string, cause error) *AppError {
	return &AppError{
		Code:       CodeBackendUnavailable,
		Message:    fmt.Sprintf("%s service unavailable", service),
		HTTPStatus: http.StatusServiceUnavailable,
		Cause:      cause,
	}
}

// NewRateLimited creates a rate-limited error.
func NewRateLimited() *AppError {
	return &AppError{
		Code:       CodeRateLimited,
		Message:    "rate limit exceeded, please retry later",
		HTTPStatus: http.StatusTooManyRequests,
	}
}

// NewBadRequest creates a bad-request error.
func NewBadRequest(message string) *AppError {
	return &AppError{
		Code:       CodeBadRequest,
		Message:    message,
		HTTPStatus: http.StatusBadRequest,
	}
}

// ToResponse converts an AppError to the standard ErrorResponse.
func (e *AppError) ToResponse(traceID, correlationID string) ErrorResponse {
	return ErrorResponse{
		Success:       false,
		Code:          e.Code,
		Message:       e.Message,
		TraceID:       traceID,
		CorrelationID: correlationID,
	}
}
