// Package providers — quick_summary.go implements the "quick-summary"
// data provider. It aggregates counts (applications, pending payments,
// completed services, drafts) by calling backend APIs concurrently and
// caching the composite result.
package providers

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"time"

	"go.uber.org/zap"
	"golang.org/x/sync/errgroup"

	"github.com/upyog/upyog-aggregation-service/internal/cache"
	"github.com/upyog/upyog-aggregation-service/internal/clients"
	"github.com/upyog/upyog-aggregation-service/internal/common"
	"github.com/upyog/upyog-aggregation-service/internal/dto"
	"github.com/upyog/upyog-aggregation-service/internal/metrics"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

const quickSummaryProviderName = "quick-summary"

const draftCountPath = "/upyog-draft-service/draft/v1/_count"

// QuickSummaryData holds the aggregated counts returned by the
// quick-summary provider.
type QuickSummaryData struct {
	// ApplicationCount is the total number of submitted applications.
	ApplicationCount int `json:"applicationCount"`
	// PendingPaymentsCount is the number of applications with outstanding payments.
	PendingPaymentsCount int `json:"pendingPaymentsCount"`
	// CompletedServicesCount is the number of fully completed service requests.
	CompletedServicesCount int `json:"completedServicesCount"`
	// DraftsCount is the number of applications still in draft state.
	DraftsCount int `json:"draftsCount"`
}

// QuickSummaryProvider fetches summary counts from multiple backend
// services concurrently and returns a single merged response.
type QuickSummaryProvider struct {
	BaseProvider
	billingClient     *clients.Client
	draftClient       *clients.Client
	workflowClient    *clients.Client
	advClient         *clients.Client
	chbClient         *clients.Client
	completedStatuses []string
}

// NewQuickSummaryProvider creates a new QuickSummaryProvider.
func NewQuickSummaryProvider(
	client *clients.Client,
	billingClient *clients.Client,
	draftClient *clients.Client,
	workflowClient *clients.Client,
	advClient *clients.Client,
	chbClient *clients.Client,
	c *cache.Cache,
	log *logger.Logger,
	m *metrics.Metrics,
	ttl time.Duration,
	completedStatuses []string,
) *QuickSummaryProvider {
	return &QuickSummaryProvider{
		BaseProvider:      NewBaseProvider(quickSummaryProviderName, client, c, log, m, ttl),
		billingClient:     billingClient,
		draftClient:       draftClient,
		workflowClient:    workflowClient,
		advClient:         advClient,
		chbClient:         chbClient,
		completedStatuses: completedStatuses,
	}
}

// Execute implements DataProvider. It checks the cache first; on a miss it
// concurrently fetches all four counts, gracefully defaulting any individual
// failure to zero.
func (p *QuickSummaryProvider) Execute(
	ctx context.Context,
	request dto.ProviderRequest,
	aggReq dto.AggregateRequest,
) (*dto.ProviderResponse, error) {
	cacheKey := p.BuildCacheKey(aggReq.TenantID)

	// Attempt cache hit.
	var cached QuickSummaryData
	if p.Cache != nil {
		hit, err := p.GetCached(ctx, cacheKey, &cached)
		if err != nil {
			p.Log.WithContext(ctx).Warn("cache lookup failed for quick-summary", zap.Error(err))
		}
		if hit {
			p.Metrics.CacheHitsTotal.WithLabelValues(p.Name()).Inc()
			return &dto.ProviderResponse{
				Status: common.StatusSuccess,
				Cached: true,
				Data:   cached,
			}, nil
		}
	}
	p.Metrics.CacheMissesTotal.WithLabelValues(p.Name()).Inc()

	// Fetch counts concurrently. Individual failures degrade to 0.
	data := QuickSummaryData{}
	g, gCtx := errgroup.WithContext(ctx)

	headers := map[string]string{
		common.HeaderTenantID: aggReq.TenantID,
	}

	// Explicitly extract UUID, MobileNumber, and TenantID from RequestInfo to avoid relying on the local context.
	var reqInfo struct {
		UserInfo struct {
			UUID         string `json:"uuid"`
			MobileNumber string `json:"mobileNumber"`
			TenantID     string `json:"tenantId"`
		} `json:"userInfo"`
	}
	_ = json.Unmarshal(aggReq.RequestInfo, &reqInfo)
	userUUID := reqInfo.UserInfo.UUID
	if userUUID == "" {
		userUUID = common.UserID(ctx)
	}
	userMobile := reqInfo.UserInfo.MobileNumber
	tenantID := reqInfo.UserInfo.TenantID
	if tenantID == "" {
		tenantID = aggReq.TenantID
	}

	g.Go(func() error {
		path := "/egov-workflow-v2/egov-wf/process/dashboard/_count"
		p.Log.WithContext(gCtx).Info("fetching applicationCount from workflow dashboard API", zap.String("api", path))

		wfCount, fetchErr := p.fetchApplicationCount(gCtx, aggReq.RequestInfo, aggReq.TenantID, userUUID, headers)
		if fetchErr != nil {
			p.Log.WithContext(gCtx).Warn("failed to fetch workflow application count", zap.Error(fetchErr))
		}

		advCount := p.fetchAdvCount(gCtx, aggReq.RequestInfo, aggReq.TenantID, userMobile, headers)
		chbCount := p.fetchChbCount(gCtx, aggReq.RequestInfo, aggReq.TenantID, userMobile, headers)

		totalAppCount := wfCount + advCount + chbCount
		p.Log.WithContext(gCtx).Info("successfully calculated applicationCount",
			zap.Int("workflowCount", wfCount),
			zap.Int("advCount", advCount),
			zap.Int("chbCount", chbCount),
			zap.Int("totalApplicationCount", totalAppCount),
		)
		data.ApplicationCount = totalAppCount
		return nil
	})

	g.Go(func() error {
		body := struct {
			RequestInfo json.RawMessage `json:"RequestInfo"`
		}{
			RequestInfo: aggReq.RequestInfo,
		}
		// Changed endpoint from bill/v2/_count to bill/v2/short/_search to properly count pending payments
		path := fmt.Sprintf("/billing-service/bill/v2/short/_search?tenantId=%s&mobileNumber=%s&isActive=true&status=ACTIVE", tenantID, userMobile)
		p.Log.WithContext(gCtx).Info("fetching pendingPaymentsCount from billing API", zap.String("api", path))

		resp, fetchErr := p.billingClient.Post(gCtx, path, body, headers)
		if fetchErr != nil {
			p.Log.WithContext(gCtx).Warn("failed to fetch pending payments count", zap.Error(fetchErr))
			return nil
		}
		if resp.StatusCode != http.StatusOK {
			p.Log.WithContext(gCtx).Warn("pending payments count returned non-200",
				zap.Int("status", resp.StatusCode))
			return nil
		}
		var searchResult struct {
			Bill []interface{} `json:"Bill"`
		}
		if err := json.Unmarshal(resp.Body, &searchResult); err != nil {
			p.Log.WithContext(gCtx).Warn("failed to unmarshal pending payments search result", zap.Error(err))
			return nil
		}

		count := len(searchResult.Bill)
		p.Log.WithContext(gCtx).Info("successfully fetched pendingPaymentsCount", zap.Int("pendingPaymentsCount", count))
		data.PendingPaymentsCount = count
		return nil
	})

	g.Go(func() error {
		path := "/egov-workflow-v2/egov-wf/process/dashboard/_search"
		p.Log.WithContext(gCtx).Info("fetching completedServicesCount from workflow dashboard API", zap.String("api", path))

		wfCompletedCount, fetchErr := p.fetchCompletedServicesCount(gCtx, aggReq.RequestInfo, aggReq.TenantID, userUUID, headers)
		if fetchErr != nil {
			p.Log.WithContext(gCtx).Warn("failed to fetch completed services count from workflow", zap.Error(fetchErr))
		}

		advBookedCount := p.fetchAdvBookedCount(gCtx, aggReq.RequestInfo, aggReq.TenantID, userMobile, headers)

		totalCompleted := wfCompletedCount + advBookedCount
		p.Log.WithContext(gCtx).Info("successfully calculated completedServicesCount",
			zap.Int("workflowCompletedCount", wfCompletedCount),
			zap.Int("advBookedCount", advBookedCount),
			zap.Int("totalCompletedServicesCount", totalCompleted),
		)
		data.CompletedServicesCount = totalCompleted
		return nil
	})

	g.Go(func() error {
		p.Log.WithContext(gCtx).Info("fetching draftsCount from draft API", zap.String("api", draftCountPath))

		count, fetchErr := p.fetchDraftCount(gCtx, aggReq.RequestInfo, aggReq.TenantID, userUUID, headers)
		if fetchErr != nil {
			p.Log.WithContext(gCtx).Warn("failed to fetch drafts count", zap.Error(fetchErr))
			return nil
		}

		p.Log.WithContext(gCtx).Info("successfully fetched draftsCount", zap.Int("draftsCount", count))
		data.DraftsCount = count
		return nil
	})

	// errgroup goroutines never return errors (they degrade), so Wait is safe.
	_ = g.Wait()

	// Cache the composite result.
	if p.Cache != nil {
		if cacheErr := p.SetCached(ctx, cacheKey, data, p.CacheTTL); cacheErr != nil {
			p.Log.WithContext(ctx).Warn("failed to cache quick-summary", zap.Error(cacheErr))
		}
	}

	return &dto.ProviderResponse{
		Status: common.StatusSuccess,
		Data:   data,
	}, nil
}

// countResponse is the expected shape returned by UPYOG *_count endpoints.
type countResponse struct {
	Count int `json:"count"`
}

// fetchCount issues a POST to the given path and extracts the integer count
// from the JSON response body.
func (p *QuickSummaryProvider) fetchCount(ctx context.Context, requestInfo json.RawMessage, path string, headers map[string]string) (int, error) {
	body := struct {
		RequestInfo json.RawMessage `json:"RequestInfo"`
	}{
		RequestInfo: requestInfo,
	}
	resp, err := p.Client.Post(ctx, path, body, headers)
	if err != nil {
		return 0, fmt.Errorf("POST %s: %w", path, err)
	}
	if resp.StatusCode != http.StatusOK {
		return 0, fmt.Errorf("POST %s returned status %d", path, resp.StatusCode)
	}

	var cr countResponse
	if err := json.Unmarshal(resp.Body, &cr); err != nil {
		return 0, fmt.Errorf("unmarshal count from %s: %w", path, err)
	}
	return cr.Count, nil
}

type draftCountBody struct {
	RequestInfo json.RawMessage `json:"RequestInfo"`
	Criteria    struct {
		TenantID string `json:"tenantId"`
		UserUUID string `json:"userUuid"`
		Status   string `json:"status"`
	} `json:"DraftSearchCriteria"`
}

func (p *QuickSummaryProvider) fetchDraftCount(
	ctx context.Context,
	requestInfo json.RawMessage,
	tenantID, userUUID string,
	headers map[string]string,
) (int, error) {
	client := p.draftClient
	if client == nil {
		client = p.Client
	}

	body := draftCountBody{}
	body.RequestInfo = requestInfo
	body.Criteria.TenantID = tenantID
	body.Criteria.UserUUID = userUUID
	body.Criteria.Status = "ACTIVE"

	resp, err := client.Post(ctx, draftCountPath, body, headers)
	if err != nil {
		return 0, fmt.Errorf("POST %s: %w", draftCountPath, err)
	}
	if resp.StatusCode != http.StatusOK {
		return 0, fmt.Errorf("POST %s returned status %d", draftCountPath, resp.StatusCode)
	}

	var cr countResponse
	if err := json.Unmarshal(resp.Body, &cr); err != nil {
		return 0, fmt.Errorf("unmarshal count from %s: %w", draftCountPath, err)
	}
	return cr.Count, nil
}

type completedServicesSearchBody struct {
	RequestInfo json.RawMessage `json:"RequestInfo"`
	Criteria    struct {
		TenantID  string   `json:"tenantId"`
		CreatedBy string   `json:"createdBy"`
		Offset    int      `json:"offset"`
		Limit     int      `json:"limit"`
		Status    []string `json:"status"`
	} `json:"ProcessInstanceSearchCriteria"`
}

type completedServicesSearchResponse struct {
	TotalCount int `json:"totalCount"`
}

func (p *QuickSummaryProvider) fetchCompletedServicesCount(
	ctx context.Context,
	requestInfo json.RawMessage,
	tenantID, userUUID string,
	headers map[string]string,
) (int, error) {
	body := completedServicesSearchBody{}
	body.RequestInfo = requestInfo
	body.Criteria.TenantID = tenantID
	body.Criteria.CreatedBy = userUUID
	body.Criteria.Offset = 0
	body.Criteria.Limit = 50
	body.Criteria.Status = p.completedStatuses

	path := "/egov-workflow-v2/egov-wf/process/dashboard/_search"
	resp, err := p.workflowClient.Post(ctx, path, body, headers)
	if err != nil {
		return 0, fmt.Errorf("POST %s: %w", path, err)
	}
	if resp.StatusCode != http.StatusOK {
		return 0, fmt.Errorf("POST %s returned status %d", path, resp.StatusCode)
	}

	var sr completedServicesSearchResponse
	if err := json.Unmarshal(resp.Body, &sr); err != nil {
		return 0, fmt.Errorf("unmarshal search from %s: %w", path, err)
	}
	return sr.TotalCount, nil
}

type applicationDashboardCountBody struct {
	RequestInfo json.RawMessage `json:"RequestInfo"`
	Criteria    struct {
		TenantID  string `json:"tenantId"`
		CreatedBy string `json:"createdBy"`
	} `json:"ProcessInstanceSearchCriteria"`
}

func (p *QuickSummaryProvider) fetchApplicationCount(
	ctx context.Context,
	requestInfo json.RawMessage,
	tenantID, userUUID string,
	headers map[string]string,
) (int, error) {
	body := applicationDashboardCountBody{}
	body.RequestInfo = requestInfo
	body.Criteria.TenantID = tenantID
	body.Criteria.CreatedBy = userUUID

	path := "/egov-workflow-v2/egov-wf/process/dashboard/_count"
	resp, err := p.workflowClient.Post(ctx, path, body, headers)
	if err != nil {
		return 0, fmt.Errorf("POST %s: %w", path, err)
	}
	if resp.StatusCode != http.StatusOK {
		return 0, fmt.Errorf("POST %s returned status %d", path, resp.StatusCode)
	}

	var dcr struct {
		TotalCount int `json:"totalCount"`
	}
	if err := json.Unmarshal(resp.Body, &dcr); err != nil {
		return 0, fmt.Errorf("unmarshal dashboard count from %s: %w", path, err)
	}
	return dcr.TotalCount, nil
}

func (p *QuickSummaryProvider) fetchAdvCount(
	ctx context.Context,
	requestInfo json.RawMessage,
	tenantID, userMobile string,
	headers map[string]string,
) int {
	if p.advClient == nil {
		return 0
	}
	path := fmt.Sprintf("/adv-services/booking/v1/_search?tenantId=%s&limit=1&offset=0", tenantID)
	if userMobile != "" {
		path += fmt.Sprintf("&mobileNumber=%s", userMobile)
	}

	body := map[string]interface{}{
		"RequestInfo": requestInfo,
	}

	resp, err := p.advClient.Post(ctx, path, body, headers)
	if err != nil || resp.StatusCode != http.StatusOK {
		return 0
	}

	var res struct {
		BookingApplication []interface{} `json:"bookingApplication"`
	}
	if err := json.Unmarshal(resp.Body, &res); err != nil {
		return 0
	}
	return len(res.BookingApplication)
}

func (p *QuickSummaryProvider) fetchChbCount(
	ctx context.Context,
	requestInfo json.RawMessage,
	tenantID, userMobile string,
	headers map[string]string,
) int {
	if p.chbClient == nil {
		return 0
	}
	path := fmt.Sprintf("/chb-services/booking/v1/_search?tenantId=%s&limit=1&offset=0", tenantID)
	if userMobile != "" {
		path += fmt.Sprintf("&mobileNumber=%s", userMobile)
	}

	body := map[string]interface{}{
		"RequestInfo": requestInfo,
	}

	resp, err := p.chbClient.Post(ctx, path, body, headers)
	if err != nil || resp.StatusCode != http.StatusOK {
		return 0
	}

	var res struct {
		BookingApplication []interface{} `json:"hallsBookingApplication"`
	}
	if err := json.Unmarshal(resp.Body, &res); err != nil {
		return 0
	}
	return len(res.BookingApplication)
}

func (p *QuickSummaryProvider) fetchAdvBookedCount(
	ctx context.Context,
	requestInfo json.RawMessage,
	tenantID, userMobile string,
	headers map[string]string,
) int {
	if p.advClient == nil {
		return 0
	}
	path := fmt.Sprintf("/adv-services/booking/v1/_search?tenantId=%s&bookingStatus=BOOKED&limit=50&offset=0", tenantID)
	if userMobile != "" {
		path += fmt.Sprintf("&mobileNumber=%s", userMobile)
	}

	body := map[string]interface{}{
		"RequestInfo": requestInfo,
	}

	resp, err := p.advClient.Post(ctx, path, body, headers)
	if err != nil || resp.StatusCode != http.StatusOK {
		return 0
	}

	var res struct {
		BookingApplication []interface{} `json:"bookingApplication"`
	}
	if err := json.Unmarshal(resp.Body, &res); err != nil {
		return 0
	}
	return len(res.BookingApplication)
}
