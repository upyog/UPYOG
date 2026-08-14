// Package providers — upcoming_events.go implements the
// "upcoming-events" data provider. It fetches future public events
// from the UPYOG user-event service, filtering by start date.
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

const upcomingEventsProviderName = "upcoming-events"

// Event represents a scheduled public event.
type Event struct {
	// ID is the unique event identifier.
	ID string `json:"id"`
	// Title is the event headline.
	Title string `json:"title"`
	// Description is the detailed event description.
	Description string `json:"description"`
	// StartDate is the epoch-millis event start time.
	StartDate int64 `json:"startDate"`
	// EndDate is the epoch-millis event end time.
	EndDate int64 `json:"endDate"`
	// Venue is the physical or virtual location.
	Venue string `json:"venue"`
	// Category classifies the event (e.g. "WORKSHOP", "TOWN_HALL").
	Category string `json:"category"`
}

// UpcomingEventsProvider retrieves future events from the UPYOG
// user-event service.
type UpcomingEventsProvider struct {
	BaseProvider
}

// NewUpcomingEventsProvider creates a new UpcomingEventsProvider.
func NewUpcomingEventsProvider(
	client *clients.Client,
	c *cache.Cache,
	log *logger.Logger,
	m *metrics.Metrics,
	ttl time.Duration,
) *UpcomingEventsProvider {
	return &UpcomingEventsProvider{
		BaseProvider: NewBaseProvider(upcomingEventsProviderName, client, c, log, m, ttl),
	}
}

// Execute implements DataProvider. It queries the user-event search API
// with a fromDate filter set to the current time so that only future
// events are returned.
func (p *UpcomingEventsProvider) Execute(
	ctx context.Context,
	request dto.ProviderRequest,
	aggReq dto.AggregateRequest,
) (*dto.ProviderResponse, error) {
	nowMillis := time.Now().UnixMilli()

	path := fmt.Sprintf(
		"/egov-user-event/v1/events/_search?tenantId=%s&eventType=EVENTSONGROUND&fromDate=%s",
		aggReq.TenantID,
		strconv.FormatInt(nowMillis, 10),
	)

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

	var result eventSearchResponse
	if err := json.Unmarshal(resp.Body, &result); err != nil {
		return nil, fmt.Errorf("unmarshal upcoming events response: %w", err)
	}

	p.Log.WithContext(ctx).Debug("fetched upcoming events",
		zap.Int("count", len(result.Events)),
	)

	return &dto.ProviderResponse{
		Status: common.StatusSuccess,
		Data:   result.Events,
	}, nil
}

// eventSearchResponse mirrors the shape returned by the UPYOG
// user-event search API.
type eventSearchResponse struct {
	Events []Event `json:"events"`
}
