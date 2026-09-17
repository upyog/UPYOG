// Package providers defines the DataProvider interface and base provider
// implementation for the UPYOG aggregation service. Every data source
// (quick-summary, recent-applications, etc.) implements DataProvider and
// embeds BaseProvider for shared caching and HTTP-client access.
package providers

import (
	"context"
	"time"

	"github.com/upyog/upyog-aggregation-service/internal/cache"
	"github.com/upyog/upyog-aggregation-service/internal/clients"
	"github.com/upyog/upyog-aggregation-service/internal/dto"
	"github.com/upyog/upyog-aggregation-service/internal/metrics"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

// DataProvider is the contract every aggregation data source must implement.
// The engine resolves providers by Name() and invokes Execute() concurrently.
type DataProvider interface {
	// Name returns the unique, kebab-case provider identifier
	// (e.g. "quick-summary"). This name is used for registry lookup.
	Name() string

	// Execute fetches data for the given provider request within the scope of
	// the parent aggregation request. Implementations MUST be safe for
	// concurrent invocation.
	Execute(ctx context.Context, request dto.ProviderRequest, aggReq dto.AggregateRequest) (*dto.ProviderResponse, error)
}

// BaseProvider provides common infrastructure shared by all concrete
// providers: an HTTP client, Redis cache handle, structured logger,
// Prometheus metrics, and a default cache TTL.
type BaseProvider struct {
	providerName string
	Client       *clients.Client
	Cache        *cache.Cache
	Log          *logger.Logger
	Metrics      *metrics.Metrics
	CacheTTL     time.Duration
}

// NewBaseProvider constructs a BaseProvider with the supplied dependencies.
func NewBaseProvider(
	name string,
	client *clients.Client,
	c *cache.Cache,
	log *logger.Logger,
	m *metrics.Metrics,
	ttl time.Duration,
) BaseProvider {
	return BaseProvider{
		providerName: name,
		Client:       client,
		Cache:        c,
		Log:          log,
		Metrics:      m,
		CacheTTL:     ttl,
	}
}

// Name returns the provider's registered name.
func (b *BaseProvider) Name() string {
	return b.providerName
}

// BuildCacheKey constructs a deterministic cache key scoped to the provider's
// name and the supplied tenant ID. Additional parts are appended for further
// scoping (e.g. pagination offsets).
func (b *BaseProvider) BuildCacheKey(tenantID string, parts ...string) string {
	return cache.BuildKey(tenantID, b.providerName, parts...)
}

// GetCached attempts to load a previously-cached value into dest.
// It returns (true, nil) on a cache hit, (false, nil) on a miss,
// and (false, err) when the cache backend is unreachable.
func (b *BaseProvider) GetCached(ctx context.Context, key string, dest interface{}) (bool, error) {
	return b.Cache.Get(ctx, key, dest)
}

// SetCached stores value in the cache under key with the given TTL.
// A zero TTL falls back to the cache's configured default.
func (b *BaseProvider) SetCached(ctx context.Context, key string, value interface{}, ttl time.Duration) error {
	return b.Cache.Set(ctx, key, value, ttl)
}
