// Package providers — draft_applications.go implements the
// "draft-applications" data provider. It fetches the user's saved
// draft applications from the UPYOG inbox search API, filtered by
// DRAFT status and supporting pagination.
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

const draftApplicationsProviderName = "draft-applications"

// DraftApplication represents a saved but unsubmitted application.
type DraftApplication struct {
	// ID is the unique draft identifier.
	ID string `json:"id"`
	// BusinessService is the service module (e.g. "PT", "TL").
	BusinessService string `json:"businessService"`
	// ApplicationNumber is the human-readable draft number.
	ApplicationNumber string `json:"applicationNumber"`
	// LastModifiedTime is the epoch-millis timestamp of the last update.
	LastModifiedTime int64 `json:"lastModifiedTime"`
	// CompletionPercentage is the percentage of the form that has been filled.
	CompletionPercentage float64 `json:"completionPercentage"`
}

// DraftApplicationsProvider retrieves draft-status applications from
// the UPYOG inbox search endpoint.
type DraftApplicationsProvider struct {
	BaseProvider
}

// NewDraftApplicationsProvider creates a new DraftApplicationsProvider.
func NewDraftApplicationsProvider(
	client *clients.Client,
	c *cache.Cache,
	log *logger.Logger,
	m *metrics.Metrics,
	ttl time.Duration,
) *DraftApplicationsProvider {
	return &DraftApplicationsProvider{
		BaseProvider: NewBaseProvider(draftApplicationsProviderName, client, c, log, m, ttl),
	}
}

// Execute implements DataProvider. It queries /inbox/v2/_search with
// status=DRAFT and the requested pagination.
func (p *DraftApplicationsProvider) Execute(
	ctx context.Context,
	request dto.ProviderRequest,
	aggReq dto.AggregateRequest,
) (*dto.ProviderResponse, error) {
	path := p.buildSearchPath(request, aggReq.TenantID)

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

	var result draftSearchResponse
	if err := json.Unmarshal(resp.Body, &result); err != nil {
		return nil, fmt.Errorf("unmarshal draft applications response: %w", err)
	}

	p.Log.WithContext(ctx).Debug("fetched draft applications",
		zap.Int("count", len(result.Items)),
	)

	return &dto.ProviderResponse{
		Status: common.StatusSuccess,
		Data:   result.Items,
	}, nil
}

// draftSearchResponse mirrors the shape returned by the inbox search API.
type draftSearchResponse struct {
	Items []DraftApplication `json:"items"`
}

// buildSearchPath constructs the query-string-encoded search path with
// a mandatory DRAFT status filter and optional pagination parameters.
func (p *DraftApplicationsProvider) buildSearchPath(request dto.ProviderRequest, tenantID string) string {
	path := "/inbox/v2/_search?status=DRAFT&tenantId=" + tenantID + "&"

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

	// Trim trailing '&'.
	return path[:len(path)-1]
}
