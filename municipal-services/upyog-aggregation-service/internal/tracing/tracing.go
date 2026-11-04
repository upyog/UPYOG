// Package tracing provides OpenTelemetry tracing setup and span helpers
// for the aggregation service. It configures the tracer provider with
// a Jaeger/OTLP exporter and exposes utility functions for creating spans.
package tracing

import (
	"context"
	"fmt"

	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/attribute"
	"go.opentelemetry.io/otel/exporters/otlp/otlptrace/otlptracegrpc"
	"go.opentelemetry.io/otel/sdk/resource"
	sdktrace "go.opentelemetry.io/otel/sdk/trace"
	semconv "go.opentelemetry.io/otel/semconv/v1.24.0"
	"go.opentelemetry.io/otel/trace"
)

// TracerProvider wraps the OpenTelemetry tracer provider and exposes
// a Shutdown method for graceful cleanup.
type TracerProvider struct {
	provider *sdktrace.TracerProvider
}

// Init initializes the OpenTelemetry tracer provider with an OTLP/gRPC exporter.
// Returns a TracerProvider that must be shut down on application exit.
func Init(ctx context.Context, serviceName, endpoint string, samplingRate float64) (*TracerProvider, error) {
	if endpoint == "" {
		// No endpoint configured — return a no-op provider.
		noop := sdktrace.NewTracerProvider()
		otel.SetTracerProvider(noop)
		return &TracerProvider{provider: noop}, nil
	}

	exporter, err := otlptracegrpc.New(ctx,
		otlptracegrpc.WithEndpoint(endpoint),
		otlptracegrpc.WithInsecure(),
	)
	if err != nil {
		return nil, fmt.Errorf("failed to create OTLP trace exporter: %w", err)
	}

	res, err := resource.New(ctx,
		resource.WithAttributes(
			semconv.ServiceNameKey.String(serviceName),
			semconv.ServiceVersionKey.String("1.0.0"),
		),
	)
	if err != nil {
		return nil, fmt.Errorf("failed to create tracing resource: %w", err)
	}

	sampler := sdktrace.ParentBased(sdktrace.TraceIDRatioBased(samplingRate))

	tp := sdktrace.NewTracerProvider(
		sdktrace.WithBatcher(exporter),
		sdktrace.WithResource(res),
		sdktrace.WithSampler(sampler),
	)

	otel.SetTracerProvider(tp)

	return &TracerProvider{provider: tp}, nil
}

// Shutdown flushes any remaining spans and shuts down the tracer provider.
func (tp *TracerProvider) Shutdown(ctx context.Context) error {
	if tp.provider != nil {
		return tp.provider.Shutdown(ctx)
	}
	return nil
}

// Tracer returns the named tracer from the global tracer provider.
func Tracer(name string) trace.Tracer {
	return otel.Tracer(name)
}

// StartSpan starts a new span with the given name and attributes.
func StartSpan(ctx context.Context, tracerName, spanName string, attrs ...attribute.KeyValue) (context.Context, trace.Span) {
	tr := Tracer(tracerName)
	ctx, span := tr.Start(ctx, spanName, trace.WithAttributes(attrs...))
	return ctx, span
}

// SpanFromContext returns the current span from the context.
func SpanFromContext(ctx context.Context) trace.Span {
	return trace.SpanFromContext(ctx)
}

// TraceIDFromContext extracts the trace ID string from the current span context.
func TraceIDFromContext(ctx context.Context) string {
	sc := trace.SpanFromContext(ctx).SpanContext()
	if sc.HasTraceID() {
		return sc.TraceID().String()
	}
	return ""
}
