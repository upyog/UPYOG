// Package config manages application configuration using Viper with support
// for multiple environment profiles (local, dev, qa, uat, prod).
package config

import (
	"time"
)

// Config holds the complete application configuration.
type Config struct {
	Server        ServerConfig        `mapstructure:"server"`
	Redis         RedisConfig         `mapstructure:"redis"`
	Auth          AuthConfig          `mapstructure:"auth"`
	Observability ObservabilityConfig `mapstructure:"observability"`
	Providers     ProvidersConfig     `mapstructure:"providers"`
	Backend       BackendConfig       `mapstructure:"backend"`
	RateLimit     RateLimitConfig     `mapstructure:"rateLimit"`
}

// ServerConfig holds HTTP server settings.
type ServerConfig struct {
	Port int `mapstructure:"port"`
	// ContextPath is an optional base path all routes are served under
	// (e.g. "/upyog-aggregation-service"). Required when running behind the
	// UPYOG API gateway, which routes /<context>/** without stripping the
	// prefix. Empty means routes are served at the root.
	ContextPath       string        `mapstructure:"contextPath"`
	ReadTimeout       time.Duration `mapstructure:"readTimeout"`
	WriteTimeout      time.Duration `mapstructure:"writeTimeout"`
	IdleTimeout       time.Duration `mapstructure:"idleTimeout"`
	ShutdownTimeout   time.Duration `mapstructure:"shutdownTimeout"`
	MaxRequestBodyMB  int           `mapstructure:"maxRequestBodyMB"`
	EnableCompression bool          `mapstructure:"enableCompression"`
}

// RedisConfig holds Redis connection settings.
type RedisConfig struct {
	Host         string        `mapstructure:"host"`
	Port         int           `mapstructure:"port"`
	Password     string        `mapstructure:"password"`
	DB           int           `mapstructure:"db"`
	PoolSize     int           `mapstructure:"poolSize"`
	MinIdleConns int           `mapstructure:"minIdleConns"`
	DialTimeout  time.Duration `mapstructure:"dialTimeout"`
	ReadTimeout  time.Duration `mapstructure:"readTimeout"`
	WriteTimeout time.Duration `mapstructure:"writeTimeout"`
	Enabled      bool          `mapstructure:"enabled"`
}

// AuthConfig holds authentication and authorization settings.
type AuthConfig struct {
	Enabled        bool     `mapstructure:"enabled"`
	JWKSUrl        string   `mapstructure:"jwksUrl"`
	Issuer         string   `mapstructure:"issuer"`
	Audience       string   `mapstructure:"audience"`
	PublicPaths    []string `mapstructure:"publicPaths"`
	TokenExpLeeway int      `mapstructure:"tokenExpLeeway"`
}

// ObservabilityConfig holds observability settings.
type ObservabilityConfig struct {
	Metrics MetricsConfig `mapstructure:"metrics"`
	Tracing TracingConfig `mapstructure:"tracing"`
}

// MetricsConfig holds Prometheus metrics settings.
type MetricsConfig struct {
	Enabled   bool   `mapstructure:"enabled"`
	Path      string `mapstructure:"path"`
	Namespace string `mapstructure:"namespace"`
	Subsystem string `mapstructure:"subsystem"`
}

// TracingConfig holds OpenTelemetry tracing settings.
type TracingConfig struct {
	Enabled      bool    `mapstructure:"enabled"`
	Endpoint     string  `mapstructure:"endpoint"`
	ServiceName  string  `mapstructure:"serviceName"`
	SamplingRate float64 `mapstructure:"samplingRate"`
}

// ProvidersConfig holds global provider configuration.
type ProvidersConfig struct {
	DefaultTimeout   time.Duration                   `mapstructure:"defaultTimeout"`
	MaxRetries       int                             `mapstructure:"maxRetries"`
	CacheTTL         time.Duration                   `mapstructure:"cacheTTL"`
	CompletedServiceStatuses   []string                        `mapstructure:"completedServiceStatuses"`
	RecentApplicationsSinceDays int                             `mapstructure:"recentApplicationsSinceDays"`
	Custom                     map[string]ProviderCustomConfig `mapstructure:"custom"`
}

// ProviderCustomConfig holds per-provider overrides.
type ProviderCustomConfig struct {
	Timeout  time.Duration `mapstructure:"timeout"`
	CacheTTL time.Duration `mapstructure:"cacheTTL"`
	Retries  int           `mapstructure:"retries"`
}

// BackendConfig holds backend service base URLs.
type BackendConfig struct {
	Services map[string]ServiceEndpoint `mapstructure:"services"`
}

// ServiceEndpoint holds a single backend service's connection details.
type ServiceEndpoint struct {
	BaseURL          string        `mapstructure:"baseUrl"`
	Timeout          time.Duration `mapstructure:"timeout"`
	MaxConns         int           `mapstructure:"maxConns"`
	CircuitThreshold int           `mapstructure:"circuitThreshold"`
	CircuitTimeout   time.Duration `mapstructure:"circuitTimeout"`
}

// RateLimitConfig holds rate limiting settings.
type RateLimitConfig struct {
	Enabled    bool    `mapstructure:"enabled"`
	RequestsPS float64 `mapstructure:"requestsPerSecond"`
	Burst      int     `mapstructure:"burst"`
}
