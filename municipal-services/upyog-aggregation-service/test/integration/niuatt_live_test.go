//go:build integration

// Live integration test against the NIUA test environment (niuatt).
//
// This test is SKIPPED unless credentials are supplied via environment
// variables, so it never breaks CI:
//
//	NIUATT_USERNAME   (required) login of a niuatt user
//	NIUATT_PASSWORD   (required) password of that user
//	NIUATT_TENANT_ID  (optional) tenant to search, default "pg.citya"
//	NIUATT_USER_TYPE  (optional) CITIZEN or EMPLOYEE, default "CITIZEN"
//	NIUATT_BASE_URL   (optional) default "https://niuatt.niua.in"
//
// Run with:
//
//	NIUATT_USERNAME=... NIUATT_PASSWORD=... \
//	  go test ./test/integration/... -tags=integration -run Niuatt -v -count=1
//
// It performs the same two steps a real client performs:
//  1. Obtain an OAuth access token from the DIGIT user service
//     (POST /user/oauth/token, password grant, egov-user-client).
//  2. Execute the new-applications provider against the live
//     egov-workflow-v2 process search and print the mapped response.
package integration

import (
	"context"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"net/url"
	"os"
	"strings"
	"testing"
	"time"

	"github.com/upyog/upyog-aggregation-service/internal/clients"
	"github.com/upyog/upyog-aggregation-service/internal/common"
	"github.com/upyog/upyog-aggregation-service/internal/dto"
	"github.com/upyog/upyog-aggregation-service/internal/providers"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

// digitOAuthClientBasicAuth is base64("egov-user-client:") — the standard
// public OAuth client id (empty secret) used by DIGIT/UPYOG frontends.
const digitOAuthClientBasicAuth = "Basic ZWdvdi11c2VyLWNsaWVudDo="

func envOr(key, fallback string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return fallback
}

// oauthTokenResponse is the subset of the DIGIT /user/oauth/token response
// this test consumes.
type oauthTokenResponse struct {
	AccessToken string `json:"access_token"`
	UserRequest struct {
		UUID     string `json:"uuid"`
		UserName string `json:"userName"`
		Name     string `json:"name"`
		Type     string `json:"type"`
		TenantID string `json:"tenantId"`
	} `json:"UserRequest"`
}

// fetchNiuattToken performs the OAuth password grant against the niuatt
// user service and returns the access token.
func fetchNiuattToken(t *testing.T, baseURL, username, password, tenantID, userType string) oauthTokenResponse {
	t.Helper()

	form := url.Values{}
	form.Set("grant_type", "password")
	form.Set("scope", "read")
	form.Set("username", username)
	form.Set("password", password)
	form.Set("tenantId", tenantID)
	form.Set("userType", userType)

	req, err := http.NewRequest(http.MethodPost, baseURL+"/user/oauth/token",
		strings.NewReader(form.Encode()))
	if err != nil {
		t.Fatalf("build oauth request: %v", err)
	}
	req.Header.Set("Content-Type", "application/x-www-form-urlencoded")
	req.Header.Set("Authorization", digitOAuthClientBasicAuth)

	httpClient := &http.Client{Timeout: 30 * time.Second}
	resp, err := httpClient.Do(req)
	if err != nil {
		t.Fatalf("call %s/user/oauth/token: %v", baseURL, err)
	}
	defer resp.Body.Close() //nolint:errcheck

	body, _ := io.ReadAll(resp.Body)
	if resp.StatusCode != http.StatusOK {
		t.Fatalf("oauth token request failed: HTTP %d — %s", resp.StatusCode, string(body))
	}

	var token oauthTokenResponse
	if err := json.Unmarshal(body, &token); err != nil {
		t.Fatalf("parse oauth response: %v — body: %s", err, string(body))
	}
	if token.AccessToken == "" {
		t.Fatalf("oauth response contained no access_token: %s", string(body))
	}
	return token
}

func TestNiuatt_Live_OAuthAndNewApplications(t *testing.T) {
	username := os.Getenv("NIUATT_USERNAME")
	password := os.Getenv("NIUATT_PASSWORD")
	if username == "" || password == "" {
		t.Skip("NIUATT_USERNAME / NIUATT_PASSWORD not set — skipping live niuatt test")
	}

	baseURL := envOr("NIUATT_BASE_URL", "https://niuatt.niua.in")
	tenantID := envOr("NIUATT_TENANT_ID", "pg.citya")
	userType := envOr("NIUATT_USER_TYPE", "CITIZEN")

	// Step 1 — OAuth token from the niuatt user service.
	token := fetchNiuattToken(t, baseURL, username, password, tenantID, userType)
	t.Logf("niuatt OAuth OK — logged in as %q (uuid=%s, type=%s, tenant=%s)",
		token.UserRequest.UserName, token.UserRequest.UUID,
		token.UserRequest.Type, token.UserRequest.TenantID)

	// Step 2 — run the real provider against the live workflow service,
	// exactly as the aggregation service would in production.
	log := logger.New("test")
	m := testMetrics()

	workflowClient := clients.NewClient(clients.ClientConfig{
		ServiceName: "egov-workflow-v2-niuatt",
		BaseURL:     baseURL,
		Timeout:     30 * time.Second,
	}, log, m)

	p := providers.NewNewApplicationsProvider(workflowClient, nil, log, m, time.Minute)

	ctx, cancel := context.WithTimeout(context.Background(), 45*time.Second)
	defer cancel()
	ctx = common.WithAuthToken(ctx, token.AccessToken)

	provReq := dto.ProviderRequest{
		Provider:   "new-applications",
		Pagination: &dto.Pagination{Page: 0, Size: 10},
		Filters:    map[string]interface{}{"sinceDays": float64(30)},
	}
	aggReq := dto.AggregateRequest{
		RequestID: testRequestID,
		Page:      "citizen-home",
		TenantID:  tenantID,
		Requests:  []dto.ProviderRequest{provReq},
	}

	resp, err := p.Execute(ctx, provReq, aggReq)
	if err != nil {
		t.Fatalf("new-applications against niuatt failed: %v", err)
	}

	pretty, err := json.MarshalIndent(resp, "", "  ")
	if err != nil {
		t.Fatalf("marshal response: %v", err)
	}

	fmt.Printf("\n===== new-applications response from %s (tenant %s) =====\n%s\n\n",
		baseURL, tenantID, string(pretty))

	data, ok := resp.Data.(providers.NewApplicationsData)
	if !ok {
		t.Fatalf("unexpected payload type %T", resp.Data)
	}
	t.Logf("niuatt returned %d applications in the last 30 days (totalCount=%d)",
		len(data.Applications), data.TotalCount)
}
