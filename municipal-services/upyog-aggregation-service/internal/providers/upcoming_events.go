// Package providers — upcoming_events.go implements the
// "upcoming-events" data provider. It fetches future public events
// from the UPYOG user-event service, filtering by start date.
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

const upcomingEventsProviderName = "upcoming-events"

// Event represents a cleaned public event item returned in response.
type Event struct {
	ID            string `json:"id"`
	Name          string `json:"name"`
	Description   string `json:"description"`
	EventType     string `json:"eventType"`
	EventCategory string `json:"eventCategory"`
	EventDetails  *struct {
		ID        string  `json:"id"`
		Organizer string  `json:"organizer"`
		FromDate  int64   `json:"fromDate"`
		ToDate    int64   `json:"toDate"`
		Address   string  `json:"address"`
		Fees      float64 `json:"fees"`
	} `json:"eventDetails,omitempty"`
	RedirectURL string `json:"redirectUrl"`
}

// RawEvent mirrors the raw JSON event structure from egov-user-event.
type RawEvent struct {
	ID            string `json:"id"`
	TenantID      string `json:"tenantId"`
	Name          string `json:"name"`
	Description   string `json:"description"`
	Status        string `json:"status"`
	EventType     string `json:"eventType"`
	EventCategory string `json:"eventCategory"`
	EventDetails  *struct {
		ID        string  `json:"id"`
		Organizer string  `json:"organizer"`
		FromDate  int64   `json:"fromDate"`
		ToDate    int64   `json:"toDate"`
		Address   string  `json:"address"`
		Fees      float64 `json:"fees"`
	} `json:"eventDetails"`
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
// with status=ACTIVE and eventTypes=EVENTSONGROUND, filtering for active and upcoming/today events.
func (p *UpcomingEventsProvider) Execute(
	ctx context.Context,
	request dto.ProviderRequest,
	aggReq dto.AggregateRequest,
) (*dto.ProviderResponse, error) {
	path := fmt.Sprintf(
		"/egov-user-event/v1/events/_search?tenantId=%s&eventTypes=EVENTSONGROUND&status=ACTIVE",
		aggReq.TenantID,
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

	var result struct {
		Events []RawEvent `json:"events"`
	}
	if err := json.Unmarshal(resp.Body, &result); err != nil {
		return nil, fmt.Errorf("unmarshal upcoming events response: %w", err)
	}

	// Filter events: status must be ACTIVE, and event toDate/fromDate must be >= today's start of day
	now := time.Now()
	startOfTodayMillis := time.Date(now.Year(), now.Month(), now.Day(), 0, 0, 0, 0, now.Location()).UnixMilli()

	upcomingEvents := make([]Event, 0, len(result.Events))
	for _, event := range result.Events {
		if event.Status != "" && event.Status != "ACTIVE" {
			continue
		}
		// If eventDetails has end date or start date, check that it hasn't ended before today
		if event.EventDetails != nil {
			if event.EventDetails.ToDate > 0 && event.EventDetails.ToDate < startOfTodayMillis {
				continue
			}
			if event.EventDetails.ToDate == 0 && event.EventDetails.FromDate > 0 && event.EventDetails.FromDate < startOfTodayMillis {
				continue
			}
		}

		cleanedEvent := Event{
			ID:            event.ID,
			Name:          event.Name,
			Description:   event.Description,
			EventType:     event.EventType,
			EventCategory: event.EventCategory,
			RedirectURL:   fmt.Sprintf("/upyog-ui/citizen/engagement/events?eventId=%s", event.ID),
		}
		if event.EventDetails != nil {
			cleanedEvent.EventDetails = &struct {
				ID        string  `json:"id"`
				Organizer string  `json:"organizer"`
				FromDate  int64   `json:"fromDate"`
				ToDate    int64   `json:"toDate"`
				Address   string  `json:"address"`
				Fees      float64 `json:"fees"`
			}{
				ID:        event.EventDetails.ID,
				Organizer: event.EventDetails.Organizer,
				FromDate:  event.EventDetails.FromDate,
				ToDate:    event.EventDetails.ToDate,
				Address:   event.EventDetails.Address,
				Fees:      event.EventDetails.Fees,
			}
		}
		upcomingEvents = append(upcomingEvents, cleanedEvent)
	}

	p.Log.WithContext(ctx).Debug("fetched upcoming events",
		zap.Int("totalReturned", len(result.Events)),
		zap.Int("filteredUpcoming", len(upcomingEvents)),
	)

	return &dto.ProviderResponse{
		Status: common.StatusSuccess,
		Data:   upcomingEvents,
	}, nil
}
