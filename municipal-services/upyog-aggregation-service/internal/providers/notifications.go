// Package providers — notifications.go implements the "notifications"
// data provider. It fetches user events/notifications from the UPYOG
// user-event service with optional unread-only filtering.
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

const notificationsProviderName = "notifications"

// Notification represents a single user notification/event.
type Notification struct {
	// ID is the unique notification identifier.
	ID string `json:"id"`
	// Type is the notification category (e.g. "SYSTEM", "ACTION_REQUIRED").
	Type string `json:"type"`
	// Subject is the notification headline.
	Subject string `json:"subject"`
	// Message is the full notification body text.
	Message string `json:"message"`
	// Timestamp is the epoch-millis creation time.
	Timestamp int64 `json:"timestamp"`
	// Read indicates whether the user has viewed this notification.
	Read bool `json:"read"`
	// ActionURL is an optional deep-link for the notification action.
	ActionURL string `json:"actionUrl"`
}

// NotificationsProvider retrieves user events from the UPYOG
// egov-user-event service.
type NotificationsProvider struct {
	BaseProvider
}

// NewNotificationsProvider creates a new NotificationsProvider.
func NewNotificationsProvider(
	client *clients.Client,
	c *cache.Cache,
	log *logger.Logger,
	m *metrics.Metrics,
	ttl time.Duration,
) *NotificationsProvider {
	return &NotificationsProvider{
		BaseProvider: NewBaseProvider(notificationsProviderName, client, c, log, m, ttl),
	}
}

// Execute implements DataProvider. It supports an optional "unreadOnly"
// boolean filter. It calls the user-event search API and returns the
// notification list.
func (p *NotificationsProvider) Execute(
	ctx context.Context,
	request dto.ProviderRequest,
	aggReq dto.AggregateRequest,
) (*dto.ProviderResponse, error) {
	path := fmt.Sprintf(
		"/egov-user-event/v1/events/_search?tenantId=%s",
		aggReq.TenantID,
	)

	// Apply unread-only filter if requested.
	if request.Filters != nil {
		if unread, ok := request.Filters["unreadOnly"].(bool); ok && unread {
			path += "&isRead=false"
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

	var result notificationSearchResponse
	if err := json.Unmarshal(resp.Body, &result); err != nil {
		return nil, fmt.Errorf("unmarshal notifications response: %w", err)
	}

	notifications := make([]Notification, 0, len(result.Events))
	for _, evt := range result.Events {
		var actionUrl string
		if len(evt.Actions.ActionUrls) > 0 {
			actionUrl = evt.Actions.ActionUrls[0].ActionUrl
		}

		notifications = append(notifications, Notification{
			ID:        evt.ID,
			Type:      evt.EventType,
			Subject:   evt.Name,
			Message:   evt.Description,
			Timestamp: evt.AuditDetails.CreatedTime,
			Read:      false,
			ActionURL: actionUrl,
		})
	}

	p.Log.WithContext(ctx).Debug("fetched notifications",
		zap.Int("count", len(notifications)),
	)

	return &dto.ProviderResponse{
		Status: common.StatusSuccess,
		Data:   notifications,
	}, nil
}

// notificationSearchResponse mirrors the shape returned by the UPYOG
// user-event search API.
type notificationSearchResponse struct {
	Events []egovEvent `json:"events"`
}

// egovEvent models the raw JSON structure returned by egov-user-event.
type egovEvent struct {
	ID           string `json:"id"`
	EventType    string `json:"eventType"`
	Name         string `json:"name"`
	Description  string `json:"description"`
	AuditDetails struct {
		CreatedTime int64 `json:"createdTime"`
	} `json:"auditDetails"`
	Actions struct {
		ActionUrls []struct {
			ActionUrl string `json:"actionUrl"`
		} `json:"actionUrls"`
	} `json:"actions"`
}
