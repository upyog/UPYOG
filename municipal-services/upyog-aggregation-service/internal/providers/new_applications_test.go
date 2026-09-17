package providers

import (
	"context"
	"encoding/json"
	"io"
	"net/http"
	"net/http/httptest"
	"net/url"
	"testing"
	"time"

	"github.com/upyog/upyog-aggregation-service/internal/clients"
	"github.com/upyog/upyog-aggregation-service/internal/common"
	"github.com/upyog/upyog-aggregation-service/internal/dto"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

// sampleWorkflowResponse mimics the egov-workflow-v2 /process/_search payload.
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
			"businessService": "PT.CREATE",
			"businessId": "PT-2026-08-000456",
			"action": "APPLY",
			"moduleName": "PT",
			"state": null,
			"auditDetails": null
		}
	],
	"totalCount": 42
}`

func newTestProvider(t *testing.T, backendURL string) *NewApplicationsProvider {
	t.Helper()
	client := clients.NewClient(clients.ClientConfig{
		ServiceName: "egov-workflow-v2-test",
		BaseURL:     backendURL,
		Timeout:     2 * time.Second,
	}, logger.New("test"), nil)
	return NewNewApplicationsProvider(client, nil, logger.New("test"), nil, time.Minute)
}

func TestNewApplicationsProvider_Execute(t *testing.T) {
	var gotMethod, gotPath, gotRawQuery, gotAuthHeader string
	var gotBody []byte

	srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		gotMethod = r.Method
		gotPath = r.URL.Path
		gotRawQuery = r.URL.RawQuery
		gotAuthHeader = r.Header.Get("Authorization")
		gotBody, _ = io.ReadAll(r.Body)
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(sampleWorkflowResponse))
	}))
	defer srv.Close()

	p := newTestProvider(t, srv.URL)

	ctx := common.WithAuthToken(context.Background(), "test-token")
	req := dto.ProviderRequest{
		Provider:   newApplicationsProviderName,
		Pagination: &dto.Pagination{Page: 1, Size: 5},
		Filters: map[string]interface{}{
			"businessService": "NewTL",
			"sinceDays":       float64(3),
		},
	}
	aggReq := dto.AggregateRequest{
		RequestID:   "f58c5b1d-2d53-4f8d-9cb3-1f5d6f0c2b45",
		Page:        "citizen-home",
		TenantID:    "pb.amritsar",
		Requests:    []dto.ProviderRequest{req},
		RequestInfo: []byte(`{"authToken":"test-token","msgId":"f58c5b1d-2d53-4f8d-9cb3-1f5d6f0c2b45"}`),
	}

	resp, err := p.Execute(ctx, req, aggReq)
	if err != nil {
		t.Fatalf("Execute returned error: %v", err)
	}

	// --- Assert the outbound HTTP request. ---
	if gotMethod != http.MethodPost {
		t.Errorf("expected POST, got %s", gotMethod)
	}
	if gotPath != "/egov-workflow-v2/egov-wf/process/_search" {
		t.Errorf("unexpected path: %s", gotPath)
	}
	if gotAuthHeader != "Bearer test-token" {
		t.Errorf("expected bearer token to be forwarded, got %q", gotAuthHeader)
	}

	query := mustParseQuery(t, gotRawQuery)
	if query.Get("tenantId") != "pb.amritsar" {
		t.Errorf("expected tenantId=pb.amritsar, got %q", query.Get("tenantId"))
	}
	if query.Get("businessService") != "NewTL" {
		t.Errorf("expected businessService=NewTL, got %q", query.Get("businessService"))
	}
	if query.Get("offset") != "5" || query.Get("limit") != "5" {
		t.Errorf("expected offset=5&limit=5, got offset=%q limit=%q",
			query.Get("offset"), query.Get("limit"))
	}
	if query.Get("fromDate") == "" {
		t.Error("expected fromDate to be set")
	}

	var body workflowSearchBody
	if err := json.Unmarshal(gotBody, &body); err != nil {
		t.Fatalf("failed to parse request body: %v", err)
	}

	var reqInfo struct {
		AuthToken string `json:"authToken"`
		MsgID     string `json:"msgId"`
	}
	if len(body.RequestInfo) > 0 && string(body.RequestInfo) != "null" {
		if err := json.Unmarshal(body.RequestInfo, &reqInfo); err != nil {
			t.Fatalf("failed to parse RequestInfo: %v", err)
		}
	}

	if reqInfo.AuthToken != "test-token" {
		t.Errorf("expected authToken in RequestInfo, got %q", reqInfo.AuthToken)
	}
	if reqInfo.MsgID != aggReq.RequestID {
		t.Errorf("expected msgId=%q, got %q", aggReq.RequestID, reqInfo.MsgID)
	}

	// --- Assert the mapped provider response. ---
	if resp.Status != common.StatusSuccess {
		t.Errorf("expected SUCCESS status, got %s", resp.Status)
	}
	data, ok := resp.Data.(NewApplicationsData)
	if !ok {
		t.Fatalf("expected NewApplicationsData payload, got %T", resp.Data)
	}
	if data.TotalCount != 42 {
		t.Errorf("expected totalCount=42, got %d", data.TotalCount)
	}
	if len(data.Applications) != 2 {
		t.Fatalf("expected 2 applications, got %d", len(data.Applications))
	}

	first := data.Applications[0]
	if first.ApplicationNumber != "TL-2026-08-000123" {
		t.Errorf("unexpected applicationNumber: %s", first.ApplicationNumber)
	}
	if first.Status != "APPLIED" || first.State != "APPLIED" {
		t.Errorf("unexpected status/state: %s/%s", first.Status, first.State)
	}
	if first.CreatedTime != 1754000000000 {
		t.Errorf("unexpected createdTime: %d", first.CreatedTime)
	}

	// Second instance has null state/auditDetails — must not panic and
	// must default cleanly.
	second := data.Applications[1]
	if second.Status != "" || second.CreatedTime != 0 {
		t.Errorf("expected empty status and zero createdTime, got %q/%d",
			second.Status, second.CreatedTime)
	}
}

func TestNewApplicationsProvider_BackendError(t *testing.T) {
	srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, _ *http.Request) {
		w.WriteHeader(http.StatusBadRequest)
		_, _ = w.Write([]byte(`{"Errors":[{"code":"INVALID_TENANT"}]}`))
	}))
	defer srv.Close()

	p := newTestProvider(t, srv.URL)

	req := dto.ProviderRequest{Provider: newApplicationsProviderName}
	aggReq := dto.AggregateRequest{
		RequestID: "f58c5b1d-2d53-4f8d-9cb3-1f5d6f0c2b45",
		Page:      "citizen-home",
		TenantID:  "pb.amritsar",
		Requests:  []dto.ProviderRequest{req},
	}

	if _, err := p.Execute(context.Background(), req, aggReq); err == nil {
		t.Fatal("expected error for non-200 backend response")
	}
}

func TestNewApplicationsProvider_DefaultQuery(t *testing.T) {
	var gotRawQuery string
	srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		gotRawQuery = r.URL.RawQuery
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(`{"ProcessInstances": [], "totalCount": 0}`))
	}))
	defer srv.Close()

	p := newTestProvider(t, srv.URL)

	req := dto.ProviderRequest{Provider: newApplicationsProviderName}
	aggReq := dto.AggregateRequest{
		RequestID: "f58c5b1d-2d53-4f8d-9cb3-1f5d6f0c2b45",
		Page:      "citizen-home",
		TenantID:  "pb.amritsar",
		Requests:  []dto.ProviderRequest{req},
	}

	resp, err := p.Execute(context.Background(), req, aggReq)
	if err != nil {
		t.Fatalf("Execute returned error: %v", err)
	}

	query := mustParseQuery(t, gotRawQuery)
	if query.Get("offset") != "0" || query.Get("limit") != "10" {
		t.Errorf("expected default offset=0&limit=10, got offset=%q limit=%q",
			query.Get("offset"), query.Get("limit"))
	}
	if query.Get("history") != "false" {
		t.Errorf("expected history=false, got %q", query.Get("history"))
	}

	data := resp.Data.(NewApplicationsData)
	if len(data.Applications) != 0 || data.TotalCount != 0 {
		t.Errorf("expected empty result, got %+v", data)
	}
}

func mustParseQuery(t *testing.T, raw string) url.Values {
	t.Helper()
	values, err := url.ParseQuery(raw)
	if err != nil {
		t.Fatalf("failed to parse query %q: %v", raw, err)
	}
	return values
}
