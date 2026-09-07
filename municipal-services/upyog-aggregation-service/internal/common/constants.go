// Package common provides shared types, constants, and context utilities
// used across the aggregation service.
package common

// Header constants used throughout the service.
const (
	// HeaderRequestID is the HTTP header for the request ID.
	HeaderRequestID = "X-Request-Id"
	// HeaderCorrelationID is the HTTP header for the correlation ID.
	HeaderCorrelationID = "X-Correlation-Id"
	// HeaderTenantID is the HTTP header for the tenant ID.
	HeaderTenantID = "X-Tenant-Id"
	// HeaderAuthorization is the standard Authorization header.
	HeaderAuthorization = "Authorization"
	// HeaderContentType is the standard Content-Type header.
	HeaderContentType = "Content-Type"
	// HeaderAcceptLanguage is the standard Accept-Language header.
	HeaderAcceptLanguage = "Accept-Language"
	// HeaderUserInfo is the UPYOG user info header set by API gateway.
	HeaderUserInfo = "X-User-Info"
)

// ContentType constants.
const (
	ContentTypeJSON = "application/json"
)

// Provider status constants.
const (
	StatusSuccess = "SUCCESS"
	StatusFailed  = "FAILED"
	StatusTimeout = "TIMEOUT"
	StatusSkipped = "SKIPPED"
)

// Default configuration values.
const (
	DefaultServerPort        = 8080
	DefaultReadTimeout       = 30 // seconds
	DefaultWriteTimeout      = 30 // seconds
	DefaultProviderTimeout   = 10 // seconds
	DefaultMaxRetries        = 3
	DefaultCacheTTL          = 300 // seconds
	DefaultRateLimitRequests = 100
	DefaultRateLimitWindow   = 60 // seconds
	DefaultCircuitThreshold  = 5
	DefaultCircuitTimeout    = 30 // seconds
)
