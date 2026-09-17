package dto

// AggregateResponse is the top-level response body for the POST /api/v1/aggregate endpoint.
type AggregateResponse struct {
	// Success indicates whether the aggregation request was processed (even if individual providers failed).
	Success bool `json:"success"`
	// RequestID echoes the client-provided request ID.
	RequestID string `json:"requestId"`
	// Responses maps provider names to their individual execution results.
	Responses map[string]*ProviderResponse `json:"responses"`
}

// ProviderResponse represents the execution result of a single data provider.
type ProviderResponse struct {
	// Status is one of: SUCCESS, FAILED, TIMEOUT, SKIPPED.
	Status string `json:"status"`
	// ExecutionTime is the provider execution duration in milliseconds.
	ExecutionTime int64 `json:"executionTime,omitempty"`
	// Cached indicates whether the response was served from cache.
	Cached bool `json:"cached,omitempty"`
	// Data is the provider-specific response payload. Present only on SUCCESS.
	Data interface{} `json:"data,omitempty"`
	// ErrorCode is a machine-readable error code. Present only on FAILED/TIMEOUT.
	ErrorCode string `json:"errorCode,omitempty"`
	// Message is a human-readable error message. Present only on FAILED/TIMEOUT.
	Message string `json:"message,omitempty"`
}

// ErrorResponseBody is the standard error response returned for non-aggregate errors
// (e.g., validation failures, authentication errors).
type ErrorResponseBody struct {
	Success       bool   `json:"success"`
	Code          string `json:"code"`
	Message       string `json:"message"`
	TraceID       string `json:"traceId,omitempty"`
	CorrelationID string `json:"correlationId,omitempty"`
}

// HealthResponse is returned by the health check endpoints.
type HealthResponse struct {
	Status  string            `json:"status"`
	Service string            `json:"service"`
	Version string            `json:"version"`
	Details map[string]string `json:"details,omitempty"`
}
