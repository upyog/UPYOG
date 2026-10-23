package engine

import (
	"context"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"sync"
	"testing"
	"time"

	"github.com/upyog/upyog-aggregation-service/internal/clients"
	"github.com/upyog/upyog-aggregation-service/internal/dto"
	"github.com/upyog/upyog-aggregation-service/internal/metrics"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

var (
	sharedMetrics *metrics.Metrics
	metricsOnce   sync.Once
)

func getTestMetrics() *metrics.Metrics {
	metricsOnce.Do(func() {
		sharedMetrics = metrics.New("upyog_test", "aggregation_engine_test")
	})
	return sharedMetrics
}

func TestEmployeeEngine_AggregateEmployee_WorkflowProcessSearch(t *testing.T) {
	var capturedURL string
	var capturedPayload map[string]interface{}

	// Mock Workflow Server
	workflowSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		capturedURL = r.URL.String()
		_ = json.NewDecoder(r.Body).Decode(&capturedPayload)
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(`{"ProcessInstances":[{"id":"PI-101"}]}`))
	}))
	defer workflowSrv.Close()

	// Mock Employee Dashboard Server
	dashSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(`{"dashboards":[]}`))
	}))
	defer dashSrv.Close()

	// Mock Inbox Server
	inboxSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(`{"items":[],"totalCount":0}`))
	}))
	defer inboxSrv.Close()

	log := logger.New("test")
	m := getTestMetrics()

	inboxClient := clients.NewClient(clients.ClientConfig{ServiceName: "inbox", BaseURL: inboxSrv.URL, Timeout: 5 * time.Second}, log, m)
	dashClient := clients.NewClient(clients.ClientConfig{ServiceName: "employee-dashboard", BaseURL: dashSrv.URL, Timeout: 5 * time.Second}, log, m)
	workflowClient := clients.NewClient(clients.ClientConfig{ServiceName: "workflow", BaseURL: workflowSrv.URL, Timeout: 5 * time.Second}, log, m)

	eng := NewEmployeeEngine(inboxClient, dashClient, workflowClient, log, m)

	req := dto.EmployeeAggregateRequest{
		TenantID:  "pb.amritsar",
		RequestID: "req-101",
		RequestInfo: json.RawMessage(`{
			"apiId": "asset-services",
			"userInfo": {
				"uuid": "20bf4ea7-90ff-49db-ad8b-626e95aa12a7",
				"tenantId": "pb.amritsar"
			}
		}`),
		Services: []string{"PT"},
	}

	resp := eng.AggregateEmployee(context.Background(), req)

	if !resp.Success {
		t.Fatalf("expected success to be true, got false. Errors: %v", resp.Errors)
	}

	expectedPath := "/egov-workflow-v2/egov-wf/process/_search?tenantId=pb.amritsar&limit=10&offset=0"
	if capturedURL != expectedPath {
		t.Errorf("expected URL %q, got %q", expectedPath, capturedURL)
	}

	if len(resp.Workflow) == 0 {
		t.Fatalf("expected workflow object to be populated, but it was empty")
	}
}

func TestEmployeeEngine_AggregateEmployee_WorkflowFromInboxSortedBySLA(t *testing.T) {
	workflowCalled := false

	workflowSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		workflowCalled = true
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(`{"ProcessInstances":[]}`))
	}))
	defer workflowSrv.Close()

	dashSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(`{"dashboards":[]}`))
	}))
	defer dashSrv.Close()

	inboxSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		respPayload := `{
			"items": [
				{
					"ProcessInstance": {
						"id": "PI-PT-LARGE-SLA",
						"businesssServiceSla": 86400000,
						"businessId": "PT-001"
					}
				},
				{
					"ProcessInstance": {
						"id": "PI-PT-SMALL-SLA",
						"businesssServiceSla": 1000,
						"businessId": "PT-002"
					}
				}
			],
			"totalCount": 2
		}`
		_, _ = w.Write([]byte(respPayload))
	}))
	defer inboxSrv.Close()

	log := logger.New("test")
	m := getTestMetrics()

	inboxClient := clients.NewClient(clients.ClientConfig{ServiceName: "inbox", BaseURL: inboxSrv.URL, Timeout: 5 * time.Second}, log, m)
	dashClient := clients.NewClient(clients.ClientConfig{ServiceName: "employee-dashboard", BaseURL: dashSrv.URL, Timeout: 5 * time.Second}, log, m)
	workflowClient := clients.NewClient(clients.ClientConfig{ServiceName: "workflow", BaseURL: workflowSrv.URL, Timeout: 5 * time.Second}, log, m)

	eng := NewEmployeeEngine(inboxClient, dashClient, workflowClient, log, m)

	req := dto.EmployeeAggregateRequest{
		TenantID:  "pb.amritsar",
		RequestID: "req-101",
		Services:  []string{"PT"},
	}

	resp := eng.AggregateEmployee(context.Background(), req)

	if !resp.Success {
		t.Fatalf("expected success to be true, got false. Errors: %v", resp.Errors)
	}

	if !workflowCalled {
		t.Errorf("expected workflow API to be called, but it was not")
	}

	if len(resp.Inbox) == 0 {
		t.Fatalf("expected inbox object to be populated, but it was empty")
	}
}

func TestEmployeeEngine_AggregateEmployee_ViewAllWorkflow(t *testing.T) {
	var capturedWorkflowURL string

	workflowSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		capturedWorkflowURL = r.URL.String()
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(`{"ProcessInstances":[{"id":"PI-101"}]}`))
	}))
	defer workflowSrv.Close()

	dashSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(`{}`))
	}))
	defer dashSrv.Close()

	inboxSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(`{"items": [], "totalCount": 0}`))
	}))
	defer inboxSrv.Close()

	log := logger.New("test")
	m := getTestMetrics()

	inboxClient := clients.NewClient(clients.ClientConfig{ServiceName: "inbox", BaseURL: inboxSrv.URL, Timeout: 5 * time.Second}, log, m)
	dashClient := clients.NewClient(clients.ClientConfig{ServiceName: "dash", BaseURL: dashSrv.URL, Timeout: 5 * time.Second}, log, m)
	workflowClient := clients.NewClient(clients.ClientConfig{ServiceName: "wf", BaseURL: workflowSrv.URL, Timeout: 5 * time.Second}, log, m)

	eng := NewEmployeeEngine(inboxClient, dashClient, workflowClient, log, m)

	req := dto.EmployeeAggregateRequest{
		TenantID:  "pb.amritsar",
		RequestID: "req-view-all",
		Workflow:  json.RawMessage(`{"moduleName":"PT","isViewAll":true}`),
	}

	resp := eng.AggregateEmployee(context.Background(), req)

	if !resp.Success {
		t.Fatalf("expected success, got errors: %v", resp.Errors)
	}

	expectedURL := "/egov-workflow-v2/egov-wf/process/_search?tenantId=pb.amritsar&limit=300&offset=0"
	if capturedWorkflowURL != expectedURL {
		t.Errorf("expected workflow URL %q, got %q", expectedURL, capturedWorkflowURL)
	}

	if len(resp.Workflow) == 0 {
		t.Errorf("expected workflow response to be populated")
	}
	if len(resp.Inbox) != 0 {
		t.Errorf("expected inbox response to be nil when only workflow was requested, got %s", string(resp.Inbox))
	}
	if len(resp.EmployeeDashboard) != 0 {
		t.Errorf("expected dashboard response to be nil when only workflow was requested, got %s", string(resp.EmployeeDashboard))
	}
}

func TestEmployeeEngine_AggregateEmployee_OnlyWorkflowInRequest(t *testing.T) {
	workflowCalled := false
	inboxCalled := false
	dashCalled := false

	workflowSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		workflowCalled = true
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(`{"ProcessInstances":[{"id":"PI-WF-ONLY"}]}`))
	}))
	defer workflowSrv.Close()

	dashSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		dashCalled = true
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(`{}`))
	}))
	defer dashSrv.Close()

	inboxSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		inboxCalled = true
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(`{}`))
	}))
	defer inboxSrv.Close()

	log := logger.New("test")
	m := getTestMetrics()

	inboxClient := clients.NewClient(clients.ClientConfig{ServiceName: "inbox", BaseURL: inboxSrv.URL, Timeout: 5 * time.Second}, log, m)
	dashClient := clients.NewClient(clients.ClientConfig{ServiceName: "dash", BaseURL: dashSrv.URL, Timeout: 5 * time.Second}, log, m)
	workflowClient := clients.NewClient(clients.ClientConfig{ServiceName: "wf", BaseURL: workflowSrv.URL, Timeout: 5 * time.Second}, log, m)

	eng := NewEmployeeEngine(inboxClient, dashClient, workflowClient, log, m)

	req := dto.EmployeeAggregateRequest{
		TenantID:  "pb.amritsar",
		RequestID: "req-wf-only",
		Workflow:  json.RawMessage(`{"limit": 10, "offset": 0}`),
	}

	resp := eng.AggregateEmployee(context.Background(), req)

	if !resp.Success {
		t.Fatalf("expected success, got errors: %v", resp.Errors)
	}
	if !workflowCalled {
		t.Errorf("expected workflow service to be called")
	}
	if inboxCalled {
		t.Errorf("expected inbox service NOT to be called when only workflow is requested")
	}
	if dashCalled {
		t.Errorf("expected dashboard service NOT to be called when only workflow is requested")
	}
	if len(resp.Workflow) == 0 {
		t.Errorf("expected workflow response to be populated")
	}
	if len(resp.Inbox) != 0 {
		t.Errorf("expected inbox response to be nil/omitted")
	}
	if len(resp.EmployeeDashboard) != 0 {
		t.Errorf("expected employeeDashboard response to be nil/omitted")
	}
}

func TestEmployeeEngine_AggregateEmployee_PreservesExplicitLimit(t *testing.T) {
	var receivedInboxPayload map[string]interface{}

	workflowSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(`{"ProcessInstances":[]}`))
	}))
	defer workflowSrv.Close()

	dashSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(`{}`))
	}))
	defer dashSrv.Close()

	inboxSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		_ = json.NewDecoder(r.Body).Decode(&receivedInboxPayload)
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(`{"items": [], "totalCount": 0}`))
	}))
	defer inboxSrv.Close()

	log := logger.New("test")
	m := getTestMetrics()

	inboxClient := clients.NewClient(clients.ClientConfig{ServiceName: "inbox", BaseURL: inboxSrv.URL, Timeout: 5 * time.Second}, log, m)
	dashClient := clients.NewClient(clients.ClientConfig{ServiceName: "dash", BaseURL: dashSrv.URL, Timeout: 5 * time.Second}, log, m)
	workflowClient := clients.NewClient(clients.ClientConfig{ServiceName: "wf", BaseURL: workflowSrv.URL, Timeout: 5 * time.Second}, log, m)

	eng := NewEmployeeEngine(inboxClient, dashClient, workflowClient, log, m)

	req := dto.EmployeeAggregateRequest{
		TenantID:  "pb.amritsar",
		RequestID: "req-explicit-limit",
		Inbox:     json.RawMessage(`[{"tenantId": "pb.amritsar", "limit": 10, "isViewAll": true, "processSearchCriteria": {"moduleName": "PT"}}]`),
	}

	resp := eng.AggregateEmployee(context.Background(), req)

	if !resp.Success {
		t.Fatalf("expected success, got errors: %v", resp.Errors)
	}

	inboxMap, ok := receivedInboxPayload["inbox"].(map[string]interface{})
	if !ok {
		t.Fatalf("expected inbox payload object, got %v", receivedInboxPayload)
	}

	limitVal := inboxMap["limit"]
	if limitVal != float64(10) && limitVal != 10 {
		t.Errorf("expected explicit limit 10 to be preserved, got %v", limitVal)
	}
}

func TestEmployeeEngine_AggregateEmployee_WorkflowSortedBySLA_Ascending(t *testing.T) {
	workflowSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(`{
			"ProcessInstances": [
				{"id": "PI-HIGH-SLA", "businesssServiceSla": 86400000},
				{"id": "PI-NEG-SLA", "businesssServiceSla": -500},
				{"id": "PI-MID-SLA", "businesssServiceSla": 1000}
			],
			"totalCount": 3
		}`))
	}))
	defer workflowSrv.Close()

	dashSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(`{}`))
	}))
	defer dashSrv.Close()

	inboxSrv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(`{"items": [], "totalCount": 0}`))
	}))
	defer inboxSrv.Close()

	log := logger.New("test")
	m := getTestMetrics()

	inboxClient := clients.NewClient(clients.ClientConfig{ServiceName: "inbox", BaseURL: inboxSrv.URL, Timeout: 5 * time.Second}, log, m)
	dashClient := clients.NewClient(clients.ClientConfig{ServiceName: "dash", BaseURL: dashSrv.URL, Timeout: 5 * time.Second}, log, m)
	workflowClient := clients.NewClient(clients.ClientConfig{ServiceName: "wf", BaseURL: workflowSrv.URL, Timeout: 5 * time.Second}, log, m)

	eng := NewEmployeeEngine(inboxClient, dashClient, workflowClient, log, m)

	req := dto.EmployeeAggregateRequest{
		TenantID:  "pb.amritsar",
		RequestID: "req-sla-sort",
	}

	resp := eng.AggregateEmployee(context.Background(), req)

	if !resp.Success {
		t.Fatalf("expected success, got errors: %v", resp.Errors)
	}

	var wfData struct {
		ProcessInstances []struct {
			ID                  string `json:"id"`
			BusinesssServiceSLA int64  `json:"businesssServiceSla"`
		} `json:"ProcessInstances"`
	}
	if err := json.Unmarshal(resp.Workflow, &wfData); err != nil {
		t.Fatalf("failed to unmarshal workflow response: %v", err)
	}

	if len(wfData.ProcessInstances) != 3 {
		t.Fatalf("expected 3 process instances, got %d", len(wfData.ProcessInstances))
	}

	if wfData.ProcessInstances[0].ID != "PI-NEG-SLA" || wfData.ProcessInstances[1].ID != "PI-MID-SLA" || wfData.ProcessInstances[2].ID != "PI-HIGH-SLA" {
		t.Errorf("process instances not sorted in ASC SLA order, got: %+v", wfData.ProcessInstances)
	}
}
