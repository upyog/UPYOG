// Package providers — recent_applications.go implements the
// "recent-applications" data provider. It fetches a paginated, sorted
// list of the user's recent inbox applications from the UPYOG inbox
// search API.
package providers

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"
	"time"

	"go.uber.org/zap"

	"github.com/upyog/upyog-aggregation-service/internal/cache"
	"github.com/upyog/upyog-aggregation-service/internal/clients"
	"github.com/upyog/upyog-aggregation-service/internal/common"
	"github.com/upyog/upyog-aggregation-service/internal/dto"
	"github.com/upyog/upyog-aggregation-service/internal/metrics"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

const recentApplicationsProviderName = "recent-applications"

// Application represents a single UPYOG inbox application entry.
type Application struct {
	// ID is the unique application identifier.
	ID string `json:"id"`
	// BusinessService is the service module (e.g. "PT", "TL", "WS").
	BusinessService string `json:"businessService"`
	// ApplicationNumber is the human-readable application number.
	ApplicationNumber string `json:"applicationNumber"`
	// Status is the current workflow status.
	Status string `json:"status"`
	// LastModifiedTime is the epoch-millis timestamp of the last update.
	LastModifiedTime int64 `json:"lastModifiedTime"`
	// TenantID is the ULB/tenant this application belongs to.
	TenantID string `json:"tenantId"`
}

// RecentApplicationsProvider retrieves the most recently modified
// applications from the UPYOG inbox search endpoint.
type RecentApplicationsProvider struct {
	BaseProvider
}

// NewRecentApplicationsProvider creates a new RecentApplicationsProvider.
func NewRecentApplicationsProvider(
	client *clients.Client,
	c *cache.Cache,
	log *logger.Logger,
	m *metrics.Metrics,
	ttl time.Duration,
) *RecentApplicationsProvider {
	return &RecentApplicationsProvider{
		BaseProvider: NewBaseProvider(recentApplicationsProviderName, client, c, log, m, ttl),
	}
}

// Execute implements DataProvider. It builds query params from Pagination
// and Sort, calls the inbox search API, and returns the parsed list.
func (p *RecentApplicationsProvider) Execute(
	ctx context.Context,
	request dto.ProviderRequest,
	aggReq dto.AggregateRequest,
) (*dto.ProviderResponse, error) {
	path := p.buildSearchPath(request)

	headers := map[string]string{
		common.HeaderTenantID: aggReq.TenantID,
	}

	resp, err := p.Client.Get(ctx, path, headers)
	if err != nil {
		return nil, fmt.Errorf("GET %s: %w", path, err)
	}
	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("GET %s returned status %d", path, resp.StatusCode)
	}

	var result inboxSearchResponse
	if err := json.Unmarshal(resp.Body, &result); err != nil {
		return nil, fmt.Errorf("unmarshal inbox response: %w", err)
	}

	p.Log.WithContext(ctx).Debug("fetched recent applications",
		zap.Int("count", len(result.Items)),
	)

	return &dto.ProviderResponse{
		Status: common.StatusSuccess,
		Data:   result.Items,
	}, nil
}

// inboxSearchResponse mirrors the shape of the UPYOG /inbox/v2/_search
// JSON response.
type inboxSearchResponse struct {
	Items []Application `json:"items"`
}

// buildSearchPath constructs the query-string-encoded search path from
// the client-supplied pagination and sort parameters.
func (p *RecentApplicationsProvider) buildSearchPath(request dto.ProviderRequest) string {
	path := "/inbox/v2/_search?"

	if request.Pagination != nil {
		path += "offset=" + strconv.Itoa(request.Pagination.Page*request.Pagination.Size) +
			"&limit=" + strconv.Itoa(request.Pagination.Size) + "&"
	} else {
		path += "offset=0&limit=10&"
	}

	if request.Sort != nil {
		path += "sortBy=" + request.Sort.Field +
			"&sortOrder=" + request.Sort.Order + "&"
	} else {
		path += "sortBy=lastModifiedTime&sortOrder=DESC&"
	}

	// Trim trailing '&' or '?'.
	return path[:len(path)-1]
}
