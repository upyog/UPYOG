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
	billingClient *clients.Client
	draftClient   *clients.Client
}

// NewQuickSummaryProvider creates a new QuickSummaryProvider.
//
// client is the inbox service client (application counts).
// billingClient is the billing service client (pending payments count).
// draftClient is the draft service client (draft count).
func NewQuickSummaryProvider(
	client *clients.Client,
	billingClient *clients.Client,
	draftClient *clients.Client,
	c *cache.Cache,
	log *logger.Logger,
	m *metrics.Metrics,
	ttl time.Duration,
) *QuickSummaryProvider {
	return &QuickSummaryProvider{
		BaseProvider:  NewBaseProvider(quickSummaryProviderName, client, c, log, m, ttl),
		billingClient: billingClient,
		draftClient:   draftClient,
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
	p.Metrics.CacheMissesTotal.WithLabelValues(p.Name()).Inc()

	// Fetch counts concurrently. Individual failures degrade to 0.
	data := QuickSummaryData{}
	g, gCtx := errgroup.WithContext(ctx)

	headers := map[string]string{
		common.HeaderTenantID: aggReq.TenantID,
	}

	g.Go(func() error {
		count, fetchErr := p.fetchCount(gCtx, "/inbox/v2/_count?status=ALL", headers)
		if fetchErr != nil {
			p.Log.WithContext(gCtx).Warn("failed to fetch application count", zap.Error(fetchErr))
			return nil // degrade gracefully
		}
		data.ApplicationCount = count
		return nil
	})

	g.Go(func() error {
		count, fetchErr := p.billingClient.Get(gCtx, "/billing-service/bill/v2/_count?status=ACTIVE", headers)
		if fetchErr != nil {
			p.Log.WithContext(gCtx).Warn("failed to fetch pending payments count", zap.Error(fetchErr))
			return nil
		}
		if count.StatusCode != http.StatusOK {
			p.Log.WithContext(gCtx).Warn("pending payments count returned non-200",
				zap.Int("status", count.StatusCode))
			return nil
		}
		var cr countResponse
		if err := json.Unmarshal(count.Body, &cr); err != nil {
			p.Log.WithContext(gCtx).Warn("failed to unmarshal pending payments count", zap.Error(err))
			return nil
		}
		data.PendingPaymentsCount = cr.Count
		return nil
	})

	g.Go(func() error {
		count, fetchErr := p.fetchCount(gCtx, "/inbox/v2/_count?status=COMPLETED", headers)
		if fetchErr != nil {
			p.Log.WithContext(gCtx).Warn("failed to fetch completed services count", zap.Error(fetchErr))
			return nil
		}
		data.CompletedServicesCount = count
		return nil
	})

	g.Go(func() error {
		count, fetchErr := p.fetchDraftCount(gCtx, aggReq.TenantID, common.UserID(ctx), headers)
		if fetchErr != nil {
			p.Log.WithContext(gCtx).Warn("failed to fetch drafts count", zap.Error(fetchErr))
			return nil
		}
		data.DraftsCount = count
		return nil
	})

	// errgroup goroutines never return errors (they degrade), so Wait is safe.
	_ = g.Wait()

	// Cache the composite result.
	if cacheErr := p.SetCached(ctx, cacheKey, data, p.CacheTTL); cacheErr != nil {
		p.Log.WithContext(ctx).Warn("failed to cache quick-summary", zap.Error(cacheErr))
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

// fetchCount issues a GET to the given path and extracts the integer count
// from the JSON response body.
func (p *QuickSummaryProvider) fetchCount(ctx context.Context, path string, headers map[string]string) (int, error) {
	resp, err := p.Client.Get(ctx, path, headers)
	if err != nil {
		return 0, fmt.Errorf("GET %s: %w", path, err)
	}
	if resp.StatusCode != http.StatusOK {
		return 0, fmt.Errorf("GET %s returned status %d", path, resp.StatusCode)
	}

	var cr countResponse
	if err := json.Unmarshal(resp.Body, &cr); err != nil {
		return 0, fmt.Errorf("unmarshal count from %s: %w", path, err)
	}
	return cr.Count, nil
}

type draftCountBody struct {
	RequestInfo struct {
		UserInfo struct {
			UUID string `json:"uuid"`
		} `json:"userInfo"`
	} `json:"RequestInfo"`
	Criteria struct {
		TenantID string `json:"tenantId"`
		UserUUID string `json:"userUuid"`
		Status   string `json:"status"`
	} `json:"DraftSearchCriteria"`
}

func (p *QuickSummaryProvider) fetchDraftCount(
	ctx context.Context,
	tenantID, userUUID string,
	headers map[string]string,
) (int, error) {
	client := p.draftClient
	if client == nil {
		client = p.Client
	}

	body := draftCountBody{}
	body.RequestInfo.UserInfo.UUID = userUUID
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
