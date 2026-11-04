// Package providers — advertisement_banners.go implements the
// "advertisement-banners" data provider. It fetches advertisement
// banners from the UPYOG advertisement service, optionally filtered
// by placement.
package providers

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"time"

	"go.uber.org/zap"

	"github.com/upyog/upyog-aggregation-service/internal/cache"
	"github.com/upyog/upyog-aggregation-service/internal/clients"
	"github.com/upyog/upyog-aggregation-service/internal/common"
	"github.com/upyog/upyog-aggregation-service/internal/dto"
	"github.com/upyog/upyog-aggregation-service/internal/metrics"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

const advertisementBannersProviderName = "advertisement-banners"

// Banner represents a single advertisement banner.
type Banner struct {
	// ID is the unique banner identifier.
	ID string `json:"id"`
	// Title is the banner headline/alt-text.
	Title string `json:"title"`
	// ImageURL is the URL of the banner image asset.
	ImageURL string `json:"imageUrl"`
	// TargetURL is the click-through destination URL.
	TargetURL string `json:"targetUrl"`
	// Placement indicates where the banner is displayed (e.g. "HOME_TOP").
	Placement string `json:"placement"`
	// Priority controls the display ordering (lower = higher priority).
	Priority int `json:"priority"`
	// StartDate is the epoch-millis campaign start time.
	StartDate int64 `json:"startDate"`
	// EndDate is the epoch-millis campaign end time.
	EndDate int64 `json:"endDate"`
}

// AdvertisementBannersProvider retrieves advertisement banners from the
// UPYOG advertisement service.
type AdvertisementBannersProvider struct {
	BaseProvider
}

// NewAdvertisementBannersProvider creates a new AdvertisementBannersProvider.
func NewAdvertisementBannersProvider(
	client *clients.Client,
	c *cache.Cache,
	log *logger.Logger,
	m *metrics.Metrics,
	ttl time.Duration,
) *AdvertisementBannersProvider {
	return &AdvertisementBannersProvider{
		BaseProvider: NewBaseProvider(advertisementBannersProviderName, client, c, log, m, ttl),
	}
}

// Execute implements DataProvider. It supports an optional "placement"
// string filter. It calls the advertisement search API and returns the
// banner list.
func (p *AdvertisementBannersProvider) Execute(
	ctx context.Context,
	request dto.ProviderRequest,
	aggReq dto.AggregateRequest,
) (*dto.ProviderResponse, error) {
	path := fmt.Sprintf(
		"/adv-services/booking/v1/_search?tenantId=%s",
		aggReq.TenantID,
	)

	// Apply placement filter if supplied.
	if request.Filters != nil {
		if placement, ok := request.Filters["placement"].(string); ok && placement != "" {
			path += "&placement=" + placement
		}
	}

	headers := map[string]string{
		common.HeaderTenantID: aggReq.TenantID,
	}

	body := struct {
		RequestInfo json.RawMessage `json:"RequestInfo"`
	}{
		RequestInfo: aggReq.RequestInfo,
	}

	resp, err := p.Client.Post(ctx, path, body, headers)
	if err != nil {
		return nil, fmt.Errorf("POST %s: %w", path, err)
	}
	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("POST %s returned status %d", path, resp.StatusCode)
	}

	var result bannerSearchResponse
	if err := json.Unmarshal(resp.Body, &result); err != nil {
		return nil, fmt.Errorf("unmarshal banners response: %w", err)
	}

	p.Log.WithContext(ctx).Debug("fetched advertisement banners",
		zap.Int("count", len(result.Advertisements)),
	)

	return &dto.ProviderResponse{
		Status: common.StatusSuccess,
		Data:   result.Advertisements,
	}, nil
}

// bannerSearchResponse mirrors the shape returned by the UPYOG
// advertisement search API.
type bannerSearchResponse struct {
	Advertisements []Banner `json:"advertisements"`
}
