package config

import (
	"fmt"
	"os"
	"strings"
	"time"

	"github.com/spf13/viper"
)

// Load reads the configuration from the YAML file matching the current environment
// and merges environment variables. The environment is determined by the APP_ENV
// variable (defaults to "local").
func Load(configPath string) (*Config, error) {
	env := os.Getenv("APP_ENV")
	if env == "" {
		env = "local"
	}

	v := viper.New()
	v.SetConfigType("yaml")
	v.AddConfigPath(configPath)
	v.AddConfigPath(".")
	v.AddConfigPath("./configs")

	// Load base configuration first.
	v.SetConfigName("application-" + env)

	// Allow environment variable overrides with the UPYOG_ prefix.
	v.SetEnvPrefix("UPYOG")
	v.SetEnvKeyReplacer(strings.NewReplacer(".", "_", "-", "_"))
	v.AutomaticEnv()

	// Set defaults.
	setDefaults(v)

	if err := v.ReadInConfig(); err != nil {
		return nil, fmt.Errorf("failed to read config file application-%s.yaml: %w", env, err)
	}

	cfg := &Config{}
	if err := v.Unmarshal(cfg); err != nil {
		return nil, fmt.Errorf("failed to unmarshal config: %w", err)
	}

	return cfg, nil
}

// setDefaults configures sensible default values for all configuration keys.
func setDefaults(v *viper.Viper) {
	// Server defaults.
	v.SetDefault("server.port", 8080)
	v.SetDefault("server.contextPath", "")
	v.SetDefault("server.readTimeout", 30*time.Second)
	v.SetDefault("server.writeTimeout", 30*time.Second)
	v.SetDefault("server.idleTimeout", 120*time.Second)
	v.SetDefault("server.shutdownTimeout", 15*time.Second)
	v.SetDefault("server.maxRequestBodyMB", 5)
	v.SetDefault("server.enableCompression", true)

	// Redis defaults.
	v.SetDefault("redis.host", "localhost")
	v.SetDefault("redis.port", 6379)
	v.SetDefault("redis.db", 0)
	v.SetDefault("redis.poolSize", 20)
	v.SetDefault("redis.minIdleConns", 5)
	v.SetDefault("redis.dialTimeout", 5*time.Second)
	v.SetDefault("redis.readTimeout", 3*time.Second)
	v.SetDefault("redis.writeTimeout", 3*time.Second)
	v.SetDefault("redis.enabled", true)

	// Auth defaults.
	v.SetDefault("auth.enabled", false)
	v.SetDefault("auth.tokenExpLeeway", 60)
	v.SetDefault("auth.publicPaths", []string{"/health", "/readiness", "/liveness", "/metrics"})

	// Observability defaults.
	v.SetDefault("observability.metrics.enabled", true)
	v.SetDefault("observability.metrics.path", "/metrics")
	v.SetDefault("observability.metrics.namespace", "upyog")
	v.SetDefault("observability.metrics.subsystem", "aggregation")
	v.SetDefault("observability.tracing.enabled", false)
	v.SetDefault("observability.tracing.serviceName", "upyog-aggregation-service")
	v.SetDefault("observability.tracing.samplingRate", 0.1)

	// Provider defaults.
	v.SetDefault("providers.defaultTimeout", 10*time.Second)
	v.SetDefault("providers.maxRetries", 2)
	v.SetDefault("providers.cacheTTL", 5*time.Minute)
	v.SetDefault("providers.recentApplicationsSinceDays", 7)
	v.SetDefault("providers.completedServiceStatuses", []string{
		"APPROVE",
		"APPROVED",
		"AUTO_APPROVED",
		"CANCELED",
		"CANCELLED",
		"CLOSEDAFTERREJECTION",
		"CLOSEDAFTERRESOLUTION",
		"CLOSURE",
		"COMPLETED",
		"CONNECTION_ACTIVATED",
		"DELIVERED",
		"DISCONNECTION_EXECUTED",
		"DISPOSED",
		"EXPIRED",
		"MANUALEXPIRED",
		"REFUNDAPPROVED",
		"REGISTRATIONCOMPLETED",
		"REJECTED",
		"REQUESTCOMPLETED",
		"REQUESTREJECTED",
		"RESOLVED",
		"REVOCATED",
		"TREE_PRUNING_SERVICE_COMPLETED",
		"VOIDED",
		"COMPLETE_REQUEST",
		"COMPLETEREQUEST",
		"SETTLED",
	})

	// Rate limiting defaults.
	v.SetDefault("rateLimit.enabled", false)
	v.SetDefault("rateLimit.requestsPerSecond", 100.0)
	v.SetDefault("rateLimit.burst", 200)
}
