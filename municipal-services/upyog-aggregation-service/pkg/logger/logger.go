// Package logger provides a structured logging factory built on top of Zap.
// It supports context-aware logging with request ID, trace ID, tenant, and
// other correlation fields automatically extracted from the context.
package logger

import (
	"context"
	"os"

	"go.uber.org/zap"
	"go.uber.org/zap/zapcore"
)

// contextKey is an unexported type used for context keys to avoid collisions.
type contextKey string

const (
	// CtxKeyRequestID is the context key for the request ID.
	CtxKeyRequestID contextKey = "requestId"
	// CtxKeyTraceID is the context key for the trace ID.
	CtxKeyTraceID contextKey = "traceId"
	// CtxKeyCorrelationID is the context key for the correlation ID.
	CtxKeyCorrelationID contextKey = "correlationId"
	// CtxKeyTenantID is the context key for the tenant ID.
	CtxKeyTenantID contextKey = "tenantId"
	// CtxKeyProvider is the context key for the provider name.
	CtxKeyProvider contextKey = "provider"
)

// Logger wraps zap.Logger with context-aware logging capabilities.
type Logger struct {
	*zap.Logger
}

// New creates a new Logger configured for the given environment.
// In production, it outputs JSON; in development, it uses a human-readable console format.
func New(env string) *Logger {
	var cfg zap.Config

	switch env {
	case "prod", "production", "uat":
		cfg = zap.NewProductionConfig()
		cfg.EncoderConfig.TimeKey = "timestamp"
		cfg.EncoderConfig.EncodeTime = zapcore.ISO8601TimeEncoder
		cfg.EncoderConfig.StacktraceKey = "stacktrace"
	default:
		cfg = zap.NewDevelopmentConfig()
		cfg.EncoderConfig.EncodeLevel = zapcore.CapitalColorLevelEncoder
	}

	cfg.EncoderConfig.CallerKey = "caller"
	cfg.EncoderConfig.MessageKey = "message"
	cfg.EncoderConfig.LevelKey = "level"

	l, err := cfg.Build(
		zap.AddCaller(),
		zap.AddCallerSkip(0),
		zap.AddStacktrace(zapcore.ErrorLevel),
	)
	if err != nil {
		// Fall back to a no-op logger if configuration fails.
		l = zap.NewNop()
		_, _ = os.Stderr.WriteString("failed to initialize logger: " + err.Error() + "\n")
	}

	return &Logger{Logger: l}
}

// WithContext returns a new Logger enriched with fields extracted from the context.
// This ensures every log line carries the request ID, trace ID, tenant, and other
// correlation data automatically.
func (l *Logger) WithContext(ctx context.Context) *zap.Logger {
	if ctx == nil {
		return l.Logger
	}

	fields := make([]zap.Field, 0, 5)

	if v, ok := ctx.Value(CtxKeyRequestID).(string); ok && v != "" {
		fields = append(fields, zap.String("requestId", v))
	}
	if v, ok := ctx.Value(CtxKeyTraceID).(string); ok && v != "" {
		fields = append(fields, zap.String("traceId", v))
	}
	if v, ok := ctx.Value(CtxKeyCorrelationID).(string); ok && v != "" {
		fields = append(fields, zap.String("correlationId", v))
	}
	if v, ok := ctx.Value(CtxKeyTenantID).(string); ok && v != "" {
		fields = append(fields, zap.String("tenantId", v))
	}
	if v, ok := ctx.Value(CtxKeyProvider).(string); ok && v != "" {
		fields = append(fields, zap.String("provider", v))
	}

	return l.Logger.With(fields...)
}

// WithProvider returns a context with the provider name set.
func WithProvider(ctx context.Context, provider string) context.Context {
	return context.WithValue(ctx, CtxKeyProvider, provider)
}

// WithRequestID returns a context with the request ID set.
func WithRequestID(ctx context.Context, id string) context.Context {
	return context.WithValue(ctx, CtxKeyRequestID, id)
}

// WithTraceID returns a context with the trace ID set.
func WithTraceID(ctx context.Context, id string) context.Context {
	return context.WithValue(ctx, CtxKeyTraceID, id)
}

// WithCorrelationID returns a context with the correlation ID set.
func WithCorrelationID(ctx context.Context, id string) context.Context {
	return context.WithValue(ctx, CtxKeyCorrelationID, id)
}

// WithTenantID returns a context with the tenant ID set.
func WithTenantID(ctx context.Context, id string) context.Context {
	return context.WithValue(ctx, CtxKeyTenantID, id)
}

// RequestID extracts the request ID from the context.
func RequestID(ctx context.Context) string {
	if v, ok := ctx.Value(CtxKeyRequestID).(string); ok {
		return v
	}
	return ""
}

// TraceID extracts the trace ID from the context.
func TraceID(ctx context.Context) string {
	if v, ok := ctx.Value(CtxKeyTraceID).(string); ok {
		return v
	}
	return ""
}

// CorrelationID extracts the correlation ID from the context.
func CorrelationID(ctx context.Context) string {
	if v, ok := ctx.Value(CtxKeyCorrelationID).(string); ok {
		return v
	}
	return ""
}

// TenantID extracts the tenant ID from the context.
func TenantID(ctx context.Context) string {
	if v, ok := ctx.Value(CtxKeyTenantID).(string); ok {
		return v
	}
	return ""
}
