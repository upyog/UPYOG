//go:build integration

// Package integration contains end-to-end tests that assemble the complete
// service stack in-process — router, middleware chain, handler, engine,
// executor, registry, providers, and the resilient HTTP client — against
// mock backend services started with httptest.
//
// Run with:
//
//	make test-integration
//	# or: go test ./test/integration/... -tags=integration -v -count=1
package integration

import (
	"bytes"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"net/url"
	"sync"
	"testing"
	"time"

	"github.com/gin-gonic/gin"

	"github.com/upyog/upyog-aggregation-service/api"
	"github.com/upyog/upyog-aggregation-service/internal/aggregation/engine"
	"github.com/upyog/upyog-aggregation-service/internal/aggregation/executor"
	"github.com/upyog/upyog-aggregation-service/internal/aggregation/registry"
	"github.com/upyog/upyog-aggregation-service/internal/auth"
	"github.com/upyog/upyog-aggregation-service/internal/clients"
	"github.com/upyog/upyog-aggregation-service/internal/config"
	"github.com/upyog/upyog-aggregation-service/internal/metrics"
	"github.com/upyog/upyog-aggregation-service/internal/providers"
	"github.com/upyog/upyog-aggregation-service/internal/validator"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

const testRequestID = "f58c5b1d-2d53-4f8d-9cb3-1f5d6f0c2b45"
const testTenantID = "pb.amritsar"

// Prometheus collectors register into the process-global default registry,
// so the Metrics instance must be created exactly once per test binary.
var (
	metricsOnce   sync.Once
	sharedMetrics *metrics.Metrics
)

func testMetrics() *metrics.Metrics {
	metricsOnce.Do(func() {
		sharedMetrics = metrics.New("upyog_test", "aggregation_it")
	})
	return sharedMetrics
}

// sampleWorkflowResponse mimics egov-workflow-v2 /process/_search output.
const sampleWorkflowResponse = `{
	"ProcessInstances": [
		{
			"id": "pi-001",
			"tenantId": "pb.amritsar",
			"businessService": "NewTL",
			"businessId": "TL-2026-08-000123",
			"action": "APPLY",
			"moduleName": "TL",
			"state": {"state": "APPLIED", "applicationStatus": "APPLIED"},
			"auditDetails": {"createdTime": 1754000000000}
		},
		{
			"id": "pi-002",
			"tenantId": "pb.amritsar",
			"businessService": "PGR.CREATE",
			"businessId": "PGR-2026-08-000777",
			"action": "APPLY",
			"moduleName": "PGR",
			"state": {"state": "PENDINGFORASSIGNMENT", "applicationStatus": "PENDING"},
			"auditDetails": {"createdTime": 1754050000000}
		}
	],
	"totalCount": 2
}`

// sampleInboxResponse mimics the inbox /inbox/v2/_search output consumed by
// the recent-applications provider.
const sampleInboxResponse = `{
	"items": [
		{
			"id": "app-1",
			"businessService": "PT",
			"applicationNumber": "PT-2026-08-000001",
			"status": "PENDING",
			"lastModifiedTime": 1754000001000,
			"tenantId": "pb.amritsar"
		}
	]
}`

// stackOptions configures the in-process service stack under test.
type stackOptions struct {
	workflowURL      string
	inboxURL         string
	providerTimeouts map[string]time.Duration
}

// buildStack wires the full service exactly the way main.go does, but with
// backend base URLs pointing at test servers, auth/rate-limit/compression
// disabled, and no Redis (nil cache).
func buildStack(t *testing.T, opts stackOptions) http.Handler {
	t.Helper()
	gin.SetMode(gin.TestMode)

	log := logger.New("test")
	m := testMetrics()
	validator.Setup()

	newClient := func(name, baseURL string) *clients.Client {
		return clients.NewClient(clients.ClientConfig{
			ServiceName: name,
			BaseURL:     baseURL,
			Timeout:     5 * time.Second,
		}, log, m)
	}

	reg := registry.NewRegistry()
	reg.Register(providers.NewNewApplicationsProvider(
		newClient("egov-workflow-v2", opts.workflowURL), nil, log, m, time.Minute))
	reg.Register(providers.NewRecentApplicationsProvider(
		newClient("upyog-backend", opts.inboxURL), nil, log, m, time.Minute))

	exec := executor.NewExecutor(reg, log, m, 5*time.Second, opts.providerTimeouts)
	eng := engine.NewEngine(exec, reg, log, m)

	// Zero-value config: auth disabled, rate limiting disabled, compression off.
	cfg := &config.Config{}

	return api.SetupRouter(api.RouterConfig{
		Engine:       eng,
		Logger:       log,
		Metrics:      m,
		JWTValidator: auth.NewJWTValidator("", "", 60),
		Authorizer:   auth.NewAuthorizer(),
		Config:       cfg,
		Cache:        nil,
	})
}

// postAggregate sends a POST /api/v1/aggregate through the full router and
// returns the recorder plus the decoded body.
func postAggregate(t *testing.T, h http.Handler, body map[string]interface{}) (*httptest.ResponseRecorder, map[string]interface{}) {
	t.Helper()

	payload, err := json.Marshal(body)
	if err != nil {
		t.Fatalf("marshal request: %v", err)
	}

	req := httptest.NewRequest(http.MethodPost, "/api/v1/aggregate", bytes.NewReader(payload))
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("X-Tenant-Id", testTenantID)

	rec := httptest.NewRecorder()
	h.ServeHTTP(rec, req)

	var decoded map[string]interface{}
	if len(rec.Body.Bytes()) > 0 {
		if err := json.Unmarshal(rec.Body.Bytes(), &decoded); err != nil {
			t.Fatalf("decode response %q: %v", rec.Body.String(), err)
		}
	}
	return rec, decoded
}

// providerEntry digs responses.<name> out of the decoded aggregate response.
func providerEntry(t *testing.T, body map[string]interface{}, name string) map[string]interface{} {
	t.Helper()
	responses, ok := body["responses"].(map[string]interface{})
	if !ok {
		t.Fatalf("response has no 'responses' object: %v", body)
	}
	entry, ok := responses[name].(map[string]interface{})
	if !ok {
		t.Fatalf("no response entry for provider %q: %v", name, responses)
	}
	return entry
}

func aggregateRequestBody(requests ...map[string]interface{}) map[string]interface{} {
	return map[string]interface{}{
		"requestId": testRequestID,
		"page":      "citizen-home",
		"tenantId":  testTenantID,
		"requests":  requests,
	}
}

func TestAggregate_EndToEnd_Success(t *testing.T) {
	var workflowQuery url.Values

	workflowSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		workflowQuery = r.URL.Query()
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(sampleWorkflowResponse))
	}))
	defer workflowSrv.Close()

	inboxSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, _ *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(sampleInboxResponse))
	}))
	defer inboxSrv.Close()

	h := buildStack(t, stackOptions{workflowURL: workflowSrv.URL, inboxURL: inboxSrv.URL})

	rec, body := postAggregate(t, h, aggregateRequestBody(
		map[string]interface{}{
			"provider":   "new-applications",
			"pagination": map[string]interface{}{"page": 0, "size": 10},
			"filters":    map[string]interface{}{"sinceDays": 7, "moduleName": "TL"},
		},
		map[string]interface{}{"provider": "recent-applications"},
	))

	if rec.Code != http.StatusOK {
		t.Fatalf("expected 200, got %d: %s", rec.Code, rec.Body.String())
	}
	if body["success"] != true {
		t.Errorf("expected success=true, got %v", body["success"])
	}
	if body["requestId"] != testRequestID {
		t.Errorf("expected requestId echo, got %v", body["requestId"])
	}

	// new-applications assertions.
	newApps := providerEntry(t, body, "new-applications")
	if newApps["status"] != "SUCCESS" {
		t.Fatalf("new-applications: expected SUCCESS, got %v (%v)", newApps["status"], newApps)
	}
	data := newApps["data"].(map[string]interface{})
	if data["totalCount"].(float64) != 2 {
		t.Errorf("expected totalCount=2, got %v", data["totalCount"])
	}
	apps := data["applications"].([]interface{})
	if len(apps) != 2 {
		t.Fatalf("expected 2 applications, got %d", len(apps))
	}
	first := apps[0].(map[string]interface{})
	if first["applicationNumber"] != "TL-2026-08-000123" || first["status"] != "APPLIED" {
		t.Errorf("unexpected first application: %v", first)
	}

	// The workflow backend must have received the translated criteria.
	if workflowQuery.Get("tenantId") != testTenantID {
		t.Errorf("workflow backend saw tenantId=%q", workflowQuery.Get("tenantId"))
	}
	if workflowQuery.Get("moduleName") != "TL" {
		t.Errorf("workflow backend saw moduleName=%q", workflowQuery.Get("moduleName"))
	}
	if workflowQuery.Get("fromDate") == "" || workflowQuery.Get("limit") != "10" {
		t.Errorf("workflow backend saw fromDate=%q limit=%q",
			workflowQuery.Get("fromDate"), workflowQuery.Get("limit"))
	}

	// recent-applications assertions.
	recent := providerEntry(t, body, "recent-applications")
	if recent["status"] != "SUCCESS" {
		t.Fatalf("recent-applications: expected SUCCESS, got %v", recent["status"])
	}
	items := recent["data"].([]interface{})
	if len(items) != 1 {
		t.Errorf("expected 1 inbox item, got %d", len(items))
	}
}

func TestAggregate_EndToEnd_PartialFailure(t *testing.T) {
	// Workflow is broken (HTTP 500); inbox is healthy. The aggregate call must
	// still return 200 with a FAILED entry for new-applications only.
	workflowSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, _ *http.Request) {
		w.WriteHeader(http.StatusInternalServerError)
	}))
	defer workflowSrv.Close()

	inboxSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, _ *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(sampleInboxResponse))
	}))
	defer inboxSrv.Close()

	h := buildStack(t, stackOptions{workflowURL: workflowSrv.URL, inboxURL: inboxSrv.URL})

	rec, body := postAggregate(t, h, aggregateRequestBody(
		map[string]interface{}{"provider": "new-applications"},
		map[string]interface{}{"provider": "recent-applications"},
	))

	if rec.Code != http.StatusOK {
		t.Fatalf("expected 200 despite provider failure, got %d", rec.Code)
	}
	if body["success"] != true {
		t.Errorf("expected success=true (partial results), got %v", body["success"])
	}

	newApps := providerEntry(t, body, "new-applications")
	if newApps["status"] != "FAILED" {
		t.Errorf("expected FAILED for new-applications, got %v", newApps["status"])
	}
	if newApps["errorCode"] == "" || newApps["errorCode"] == nil {
		t.Errorf("expected an errorCode on the failed provider entry, got %v", newApps)
	}

	recent := providerEntry(t, body, "recent-applications")
	if recent["status"] != "SUCCESS" {
		t.Errorf("healthy provider must still succeed, got %v", recent["status"])
	}
}

func TestAggregate_EndToEnd_ProviderTimeout(t *testing.T) {
	// Workflow responds slower than the provider's timeout budget.
	workflowSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, _ *http.Request) {
		time.Sleep(1 * time.Second)
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(sampleWorkflowResponse))
	}))
	defer workflowSrv.Close()

	inboxSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, _ *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(sampleInboxResponse))
	}))
	defer inboxSrv.Close()

	h := buildStack(t, stackOptions{
		workflowURL: workflowSrv.URL,
		inboxURL:    inboxSrv.URL,
		providerTimeouts: map[string]time.Duration{
			"new-applications": 200 * time.Millisecond,
		},
	})

	rec, body := postAggregate(t, h, aggregateRequestBody(
		map[string]interface{}{"provider": "new-applications"},
		map[string]interface{}{"provider": "recent-applications"},
	))

	if rec.Code != http.StatusOK {
		t.Fatalf("expected 200, got %d", rec.Code)
	}

	newApps := providerEntry(t, body, "new-applications")
	if newApps["status"] != "TIMEOUT" {
		t.Errorf("expected TIMEOUT, got %v (%v)", newApps["status"], newApps)
	}
	if newApps["errorCode"] != "PROVIDER_TIMEOUT" {
		t.Errorf("expected PROVIDER_TIMEOUT errorCode, got %v", newApps["errorCode"])
	}

	recent := providerEntry(t, body, "recent-applications")
	if recent["status"] != "SUCCESS" {
		t.Errorf("fast provider must not be affected by the slow one, got %v", recent["status"])
	}
}

func TestAggregate_EndToEnd_UnknownProvider(t *testing.T) {
	inboxSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, _ *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(sampleInboxResponse))
	}))
	defer inboxSrv.Close()

	h := buildStack(t, stackOptions{workflowURL: inboxSrv.URL, inboxURL: inboxSrv.URL})

	rec, body := postAggregate(t, h, aggregateRequestBody(
		map[string]interface{}{"provider": "does-not-exist"},
	))

	if rec.Code != http.StatusOK {
		t.Fatalf("expected 200, got %d", rec.Code)
	}
	entry := providerEntry(t, body, "does-not-exist")
	if entry["status"] != "FAILED" {
		t.Errorf("expected FAILED, got %v", entry["status"])
	}
	if entry["errorCode"] != "PROVIDER_NOT_FOUND" {
		t.Errorf("expected PROVIDER_NOT_FOUND, got %v", entry["errorCode"])
	}
}

func TestAggregate_EndToEnd_ValidationError(t *testing.T) {
	h := buildStack(t, stackOptions{workflowURL: "http://127.0.0.1:0", inboxURL: "http://127.0.0.1:0"})

	// requestId is not a UUID → binding validation must reject with 400
	// before any provider executes.
	rec, body := postAggregate(t, h, map[string]interface{}{
		"requestId": "not-a-uuid",
		"page":      "citizen-home",
		"tenantId":  testTenantID,
		"requests":  []map[string]interface{}{{"provider": "new-applications"}},
	})

	if rec.Code != http.StatusBadRequest {
		t.Fatalf("expected 400, got %d: %s", rec.Code, rec.Body.String())
	}
	if body["success"] != false {
		t.Errorf("expected success=false, got %v", body["success"])
	}
}
