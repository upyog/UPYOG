package providers

import (
	"context"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"
	"time"

	"github.com/upyog/upyog-aggregation-service/internal/clients"
	"github.com/upyog/upyog-aggregation-service/internal/dto"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

const sampleBillsResponse = `{
	"Bill": [
		{
			"id": "bill-3",
			"businessService": "chb-services",
			"consumerCode": "CHB-003",
			"dueDate": 1720000000000,
			"totalAmount": 300
		},
		{
			"id": "bill-1",
			"businessService": "chb-services",
			"consumerCode": "CHB-001",
			"dueDate": 1710000000000,
			"totalAmount": 100
		},
		{
			"id": "bill-2",
			"businessService": "chb-services",
			"consumerCode": "CHB-002",
			"dueDate": 1715000000000,
			"totalAmount": 200
		}
	]
}`

func newTestDueRenewalsProvider(t *testing.T, backendURL string) *DueRenewalsProvider {
	return newTestDueRenewalsProviderWithBase(t, backendURL, "/upyog-ui/citizen/payment/my-bills")
}

func newTestDueRenewalsProviderWithBase(t *testing.T, backendURL, base string) *DueRenewalsProvider {
	t.Helper()
	testLogger := logger.New("test")
	client := clients.NewClient(clients.ClientConfig{
		ServiceName: "billing-service-test",
		BaseURL:     backendURL,
		Timeout:     5 * time.Second,
	}, testLogger, nil)
	return NewDueRenewalsProvider(client, nil, testLogger, nil, 0, base)
}

func TestDueRenewals_OrderByDueDate_DefaultAscending(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(sampleBillsResponse))
	}))
	defer server.Close()

	p := newTestDueRenewalsProvider(t, server.URL)
	resp, err := p.Execute(context.Background(), dto.ProviderRequest{
		Provider: "due-renewals",
	}, dto.AggregateRequest{
		TenantID:    "pg.citya",
		RequestInfo: json.RawMessage(`{"userInfo": {"mobileNumber": "9999999999"}}`),
	})

	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}

	dataBytes, _ := json.Marshal(resp.Data)
	var result struct {
		Bills []struct {
			ID          string `json:"id"`
			DueDate     int64  `json:"dueDate"`
			RedirectURL string `json:"redirectUrl"`
		} `json:"bills"`
		TotalCount int `json:"totalCount"`
	}
	_ = json.Unmarshal(dataBytes, &result)

	if result.TotalCount != 3 {
		t.Errorf("expected totalCount 3, got %d", result.TotalCount)
	}

	// Should be sorted ascending: bill-1 (1710000000000) < bill-2 (1715000000000) < bill-3 (1720000000000)
	expectedOrder := []string{"bill-1", "bill-2", "bill-3"}
	for i, expID := range expectedOrder {
		if result.Bills[i].ID != expID {
			t.Errorf("expected bill at index %d to be %s, got %s", i, expID, result.Bills[i].ID)
		}
	}

	// Check redirectUrl format
	expectedRedirect := "/upyog-ui/citizen/payment/my-bills/chb-services/CHB-001"
	if result.Bills[0].RedirectURL != expectedRedirect {
		t.Errorf("expected redirectUrl %s, got %s", expectedRedirect, result.Bills[0].RedirectURL)
	}
}

func TestDueRenewals_OrderByDueDate_Descending(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(sampleBillsResponse))
	}))
	defer server.Close()

	p := newTestDueRenewalsProvider(t, server.URL)
	resp, err := p.Execute(context.Background(), dto.ProviderRequest{
		Provider: "due-renewals",
		Sort: &dto.Sort{
			Field: "dueDate",
			Order: "DESC",
		},
	}, dto.AggregateRequest{
		TenantID:    "pg.citya",
		RequestInfo: json.RawMessage(`{"userInfo": {"mobileNumber": "9999999999"}}`),
	})

	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}

	dataBytes, _ := json.Marshal(resp.Data)
	var result struct {
		Bills []struct {
			ID      string `json:"id"`
			DueDate int64  `json:"dueDate"`
		} `json:"bills"`
	}
	_ = json.Unmarshal(dataBytes, &result)

	// Should be sorted descending: bill-3 (1720000000000) > bill-2 (1715000000000) > bill-1 (1710000000000)
	expectedOrder := []string{"bill-3", "bill-2", "bill-1"}
	for i, expID := range expectedOrder {
		if result.Bills[i].ID != expID {
			t.Errorf("expected bill at index %d to be %s, got %s", i, expID, result.Bills[i].ID)
		}
	}
}

func TestDueRenewals_CustomRedirectURLBase(t *testing.T) {
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(sampleBillsResponse))
	}))
	defer server.Close()

	customBase := "/custom-portal/citizen/pay"
	p := newTestDueRenewalsProviderWithBase(t, server.URL, customBase)
	resp, err := p.Execute(context.Background(), dto.ProviderRequest{
		Provider: "due-renewals",
	}, dto.AggregateRequest{
		TenantID:    "pg.citya",
		RequestInfo: json.RawMessage(`{"userInfo": {"mobileNumber": "9999999999"}}`),
	})

	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}

	dataBytes, _ := json.Marshal(resp.Data)
	var result struct {
		Bills []struct {
			ID          string `json:"id"`
			RedirectURL string `json:"redirectUrl"`
		} `json:"bills"`
	}
	_ = json.Unmarshal(dataBytes, &result)

	expectedRedirect := "/custom-portal/citizen/pay/chb-services/CHB-001"
	if result.Bills[0].RedirectURL != expectedRedirect {
		t.Errorf("expected redirectUrl %s, got %s", expectedRedirect, result.Bills[0].RedirectURL)
	}
}
