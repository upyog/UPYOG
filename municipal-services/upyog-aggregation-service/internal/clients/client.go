package clients

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"io"
	"net"
	"net/http"
	"strconv"
	"strings"
	"time"

	"go.opentelemetry.io/otel/attribute"
	"go.opentelemetry.io/otel/codes"
	"go.uber.org/zap"

	"github.com/upyog/upyog-aggregation-service/internal/common"
	apperr "github.com/upyog/upyog-aggregation-service/internal/errors"
	"github.com/upyog/upyog-aggregation-service/internal/metrics"
	"github.com/upyog/upyog-aggregation-service/internal/tracing"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

const (
	tracerName = "clients"

	// headerRequestID propagates the request identifier across services.
	headerRequestID = "X-Request-Id"
	// headerCorrelationID propagates the correlation identifier across services.
	headerCorrelationID = "X-Correlation-Id"
	// headerAuthorization carries the bearer token.
	headerAuthorization = "Authorization"
)

// Response represents the raw HTTP response received from a backend service.
type Response struct {
	// StatusCode is the HTTP status code returned by the server.
	StatusCode int
	// Body is the raw response body bytes.
	Body []byte
	// Headers contains the response headers.
	Headers http.Header
}

// ClientConfig holds configuration for a backend HTTP client.
type ClientConfig struct {
	// ServiceName identifies the backend service (used in logs and metrics).
	ServiceName string
	// BaseURL is the scheme + host (e.g., "http://property-service:8080").
	BaseURL string
	// Timeout is the per-request timeout.
	Timeout time.Duration
	// MaxConns is the maximum total connections to the backend.
	MaxConns int
	// MaxIdleConns is the maximum number of idle keep-alive connections.
	MaxIdleConns int
	// RetryConfig controls retry behaviour.
	RetryConfig RetryConfig
	// CircuitThreshold is the failure count before the circuit opens.
	CircuitThreshold int
	// CircuitTimeout is how long the circuit stays open before probing.
	CircuitTimeout time.Duration
}

// Client is a resilient HTTP client for calling backend micro-services.
// It integrates retry, circuit breaking, tracing, metrics, and structured logging.
type Client struct {
	config     ClientConfig
	httpClient *http.Client
	cb         *CircuitBreaker
	log        *logger.Logger
	m          *metrics.Metrics
}

// NewClient creates a Client with connection pooling, keep-alive, and TLS
// handshake timeout pre-configured.
func NewClient(cfg ClientConfig, log *logger.Logger, m *metrics.Metrics) *Client {
	if cfg.Timeout == 0 {
		cfg.Timeout = 10 * time.Second
	}
	if cfg.MaxConns == 0 {
		cfg.MaxConns = 100
	}
	if cfg.MaxIdleConns == 0 {
		cfg.MaxIdleConns = 20
	}
	if cfg.CircuitThreshold == 0 {
		cfg.CircuitThreshold = 5
	}
	if cfg.CircuitTimeout == 0 {
		cfg.CircuitTimeout = 30 * time.Second
	}

	// Normalise BaseURL: strip trailing slash so that path joining
	// (BaseURL + "/some/path") never produces a double slash.
	cfg.BaseURL = strings.TrimRight(cfg.BaseURL, "/")

	transport := &http.Transport{
		DialContext: (&net.Dialer{
			Timeout:   5 * time.Second,
			KeepAlive: 30 * time.Second,
		}).DialContext,
		MaxConnsPerHost:     cfg.MaxConns,
		MaxIdleConnsPerHost: cfg.MaxIdleConns,
		MaxIdleConns:        cfg.MaxIdleConns * 2,
		IdleConnTimeout:     90 * time.Second,
		TLSHandshakeTimeout: 5 * time.Second,
		ForceAttemptHTTP2:   true,
	}

	return &Client{
		config: cfg,
		httpClient: &http.Client{
			Timeout:   cfg.Timeout,
			Transport: transport,
		},
		cb:  NewCircuitBreaker(cfg.CircuitThreshold, cfg.CircuitTimeout),
		log: log,
		m:   m,
	}
}

// Get performs an HTTP GET request.
func (c *Client) Get(ctx context.Context, path string, headers map[string]string) (*Response, error) {
	return c.Do(ctx, http.MethodGet, path, nil, headers)
}

// Post performs an HTTP POST request with a JSON body.
func (c *Client) Post(ctx context.Context, path string, body interface{}, headers map[string]string) (*Response, error) {
	return c.Do(ctx, http.MethodPost, path, body, headers)
}

// Do is the main request execution method. It:
//  1. Builds the full URL from BaseURL + path.
//  2. Creates an *http.Request with the provided context.
//  3. Forwards JWT, X-Request-Id, and X-Correlation-Id from the context.
//  4. Applies the circuit breaker.
//  5. Retries on retryable failures with exponential backoff.
//  6. Records metrics and creates a tracing span.
//  7. Logs the request/response lifecycle.
func (c *Client) Do(ctx context.Context, method, path string, body interface{}, headers map[string]string) (*Response, error) {
	url := c.config.BaseURL + path

	// Start tracing span.
	ctx, span := tracing.StartSpan(ctx, tracerName, fmt.Sprintf("HTTP %s %s", method, c.config.ServiceName),
		attribute.String("http.method", method),
		attribute.String("http.url", url),
		attribute.String("service", c.config.ServiceName),
	)
	defer span.End()

	log := c.log.WithContext(ctx)
	start := time.Now()

	var resp *Response
	var retryCount int

	err := Retry(ctx, c.config.RetryConfig, func() error {
		// Circuit breaker gate.
		if cbErr := c.cb.Allow(); cbErr != nil {
			return cbErr
		}

		// Marshal body if present.
		var bodyReader io.Reader
		if body != nil {
			data, marshalErr := json.Marshal(body)
			if marshalErr != nil {
				return apperr.NewInternal("failed to marshal request body", marshalErr)
			}
			bodyReader = bytes.NewReader(data)
		}

		req, reqErr := http.NewRequestWithContext(ctx, method, url, bodyReader)
		if reqErr != nil {
			return apperr.NewInternal("failed to create HTTP request", reqErr)
		}

		// Standard headers.
		req.Header.Set("Content-Type", "application/json")
		req.Header.Set("Accept", "application/json")

		// Forward JWT from context.
		if token := common.AuthToken(ctx); token != "" {
			req.Header.Set(headerAuthorization, "Bearer "+token)
		}

		// Propagate correlation headers.
		if rid := logger.RequestID(ctx); rid != "" {
			req.Header.Set(headerRequestID, rid)
		}
		if cid := logger.CorrelationID(ctx); cid != "" {
			req.Header.Set(headerCorrelationID, cid)
		}

		// Apply caller-supplied headers (may override defaults).
		for k, v := range headers {
			req.Header.Set(k, v)
		}

		// Execute request.
		httpResp, doErr := c.httpClient.Do(req)
		if doErr != nil {
			c.cb.RecordFailure()
			retryCount++
			return apperr.NewBackendUnavailable(c.config.ServiceName, doErr)
		}
		defer httpResp.Body.Close() //nolint:errcheck

		respBody, readErr := io.ReadAll(httpResp.Body)
		if readErr != nil {
			c.cb.RecordFailure()
			retryCount++
			return apperr.NewInternal("failed to read response body", readErr)
		}

		resp = &Response{
			StatusCode: httpResp.StatusCode,
			Body:       respBody,
			Headers:    httpResp.Header,
		}

		// Treat 5xx as retryable failures.
		if httpResp.StatusCode >= http.StatusInternalServerError {
			c.cb.RecordFailure()
			retryCount++
			return apperr.NewBackendUnavailable(c.config.ServiceName,
				fmt.Errorf("backend returned HTTP %d", httpResp.StatusCode),
			)
		}

		// Request succeeded from a transport perspective.
		c.cb.RecordSuccess()
		return nil
	})

	duration := time.Since(start).Seconds()
	statusStr := "error"
	if resp != nil {
		statusStr = strconv.Itoa(resp.StatusCode)
	}

	// Record metrics.
	if c.m != nil {
		c.m.BackendRequestDuration.WithLabelValues(c.config.ServiceName, method, statusStr).Observe(duration)
		c.m.BackendRequestsTotal.WithLabelValues(c.config.ServiceName, method, statusStr).Inc()
		if retryCount > 0 {
			c.m.BackendRetriesTotal.WithLabelValues(c.config.ServiceName).Add(float64(retryCount))
		}
	}

	if err != nil {
		span.SetStatus(codes.Error, err.Error())
		span.RecordError(err)
		log.Error("backend request failed",
			zap.String("service", c.config.ServiceName),
			zap.String("method", method),
			zap.String("url", url),
			zap.Int("retries", retryCount),
			zap.Duration("duration", time.Since(start)),
			zap.Error(err),
		)
		return nil, err
	}

	log.Debug("backend request completed",
		zap.String("service", c.config.ServiceName),
		zap.String("method", method),
		zap.String("url", url),
		zap.Int("status", resp.StatusCode),
		zap.Int("retries", retryCount),
		zap.Duration("duration", time.Since(start)),
	)

	return resp, nil
}
