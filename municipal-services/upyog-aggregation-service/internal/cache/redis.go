// Package cache provides a Redis-backed caching layer with tenant-aware keys,
// JSON serialization, and integrated metrics for hit/miss tracking.
package cache

import (
	"context"
	"encoding/json"
	"fmt"
	"strings"
	"time"

	"github.com/redis/go-redis/v9"
	"go.uber.org/zap"

	"github.com/upyog/upyog-aggregation-service/internal/config"
	"github.com/upyog/upyog-aggregation-service/internal/metrics"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

const (
	// keyPrefix is prepended to all cache keys for namespace isolation.
	keyPrefix = "agg"
	// scanBatchSize controls how many keys SCAN returns per iteration.
	scanBatchSize = 100
)

// CacheConfig holds cache-layer configuration.
type CacheConfig struct {
	// DefaultTTL is the default time-to-live for cached entries.
	DefaultTTL time.Duration
	// RefreshThreshold is the fraction of TTL remaining at which a background
	// refresh should be triggered (0.3 means refresh when 30 % of TTL remains).
	RefreshThreshold float64
}

// DefaultCacheConfig returns production-ready defaults.
func DefaultCacheConfig() CacheConfig {
	return CacheConfig{
		DefaultTTL:       5 * time.Minute,
		RefreshThreshold: 0.3,
	}
}

// Cache wraps a Redis client with convenience methods for JSON caching.
type Cache struct {
	client *redis.Client
	config CacheConfig
	log    *logger.Logger
	m      *metrics.Metrics
}

// NewCache creates a Cache, connects to Redis, and verifies the connection
// with a PING command.
func NewCache(redisCfg config.RedisConfig, cacheCfg CacheConfig, log *logger.Logger, m *metrics.Metrics) (*Cache, error) {
	if cacheCfg.RefreshThreshold == 0 {
		cacheCfg.RefreshThreshold = 0.3
	}

	client := redis.NewClient(&redis.Options{
		Addr:         fmt.Sprintf("%s:%d", redisCfg.Host, redisCfg.Port),
		Password:     redisCfg.Password,
		DB:           redisCfg.DB,
		PoolSize:     redisCfg.PoolSize,
		MinIdleConns: redisCfg.MinIdleConns,
		DialTimeout:  redisCfg.DialTimeout,
		ReadTimeout:  redisCfg.ReadTimeout,
		WriteTimeout: redisCfg.WriteTimeout,
	})

	ctx, cancel := context.WithTimeout(context.Background(), redisCfg.DialTimeout)
	defer cancel()

	if err := client.Ping(ctx).Err(); err != nil {
		return nil, fmt.Errorf("redis ping failed: %w", err)
	}

	log.WithContext(context.Background()).Info("redis cache connected",
		zap.String("addr", fmt.Sprintf("%s:%d", redisCfg.Host, redisCfg.Port)),
		zap.Int("poolSize", redisCfg.PoolSize),
	)

	return &Cache{
		client: client,
		config: cacheCfg,
		log:    log,
		m:      m,
	}, nil
}

// Get retrieves a cached value by key and JSON-unmarshals it into dest.
// Returns (true, nil) on a cache hit, (false, nil) on a miss, and (false, err)
// on failure.
func (c *Cache) Get(ctx context.Context, key string, dest interface{}) (bool, error) {
	data, err := c.client.Get(ctx, key).Bytes()
	if err != nil {
		if err == redis.Nil {
			c.recordMiss(key)
			return false, nil
		}
		c.recordError(key, err)
		return false, fmt.Errorf("cache get %q: %w", key, err)
	}

	if err := json.Unmarshal(data, dest); err != nil {
		c.recordError(key, err)
		return false, fmt.Errorf("cache unmarshal %q: %w", key, err)
	}

	c.recordHit(key)
	return true, nil
}

// Set JSON-marshals value and stores it in Redis with the given TTL.
// If ttl is 0 the DefaultTTL is used.
func (c *Cache) Set(ctx context.Context, key string, value interface{}, ttl time.Duration) error {
	if ttl == 0 {
		ttl = c.config.DefaultTTL
	}

	data, err := json.Marshal(value)
	if err != nil {
		return fmt.Errorf("cache marshal %q: %w", key, err)
	}

	if err := c.client.Set(ctx, key, data, ttl).Err(); err != nil {
		c.recordError(key, err)
		return fmt.Errorf("cache set %q: %w", key, err)
	}

	return nil
}

// Delete removes one or more keys from the cache.
func (c *Cache) Delete(ctx context.Context, keys ...string) error {
	if len(keys) == 0 {
		return nil
	}

	if err := c.client.Del(ctx, keys...).Err(); err != nil {
		return fmt.Errorf("cache delete: %w", err)
	}

	return nil
}

// DeletePattern removes all keys matching the given glob pattern using
// incremental SCAN to avoid blocking the Redis instance.
func (c *Cache) DeletePattern(ctx context.Context, pattern string) error {
	var cursor uint64
	var totalDeleted int

	for {
		keys, nextCursor, err := c.client.Scan(ctx, cursor, pattern, scanBatchSize).Result()
		if err != nil {
			return fmt.Errorf("cache scan %q: %w", pattern, err)
		}

		if len(keys) > 0 {
			if err := c.client.Del(ctx, keys...).Err(); err != nil {
				return fmt.Errorf("cache batch delete: %w", err)
			}
			totalDeleted += len(keys)
		}

		cursor = nextCursor
		if cursor == 0 {
			break
		}
	}

	c.log.WithContext(ctx).Debug("cache pattern delete completed",
		zap.String("pattern", pattern),
		zap.Int("deleted", totalDeleted),
	)

	return nil
}

// BuildKey constructs a deterministic, tenant-aware cache key.
// Format: agg:{tenantID}:{provider}:{part1}:{part2}:…
func BuildKey(tenantID, provider string, parts ...string) string {
	base := keyPrefix + ":" + tenantID + ":" + provider
	if len(parts) > 0 {
		base += ":" + strings.Join(parts, ":")
	}
	return base
}

// Health verifies the Redis connection is alive.
func (c *Cache) Health(ctx context.Context) error {
	if err := c.client.Ping(ctx).Err(); err != nil {
		return fmt.Errorf("redis health check failed: %w", err)
	}
	return nil
}

// Close gracefully shuts down the Redis client.
func (c *Cache) Close() error {
	return c.client.Close()
}

// recordHit increments the cache-hit counter for the provider derived from key.
func (c *Cache) recordHit(key string) {
	if c.m != nil {
		c.m.CacheHitsTotal.WithLabelValues(providerFromKey(key)).Inc()
	}
}

// recordMiss increments the cache-miss counter for the provider derived from key.
func (c *Cache) recordMiss(key string) {
	if c.m != nil {
		c.m.CacheMissesTotal.WithLabelValues(providerFromKey(key)).Inc()
	}
}

// recordError increments the cache-error counter and logs the failure.
func (c *Cache) recordError(key string, err error) {
	if c.m != nil {
		c.m.CacheErrorsTotal.WithLabelValues(providerFromKey(key)).Inc()
	}
	c.log.WithContext(context.Background()).Warn("cache error",
		zap.String("key", key),
		zap.Error(err),
	)
}

// providerFromKey extracts the provider segment from a cache key.
// Key format: agg:{tenantID}:{provider}:…
func providerFromKey(key string) string {
	parts := strings.SplitN(key, ":", 4)
	if len(parts) >= 3 {
		return parts[2]
	}
	return "unknown"
}
