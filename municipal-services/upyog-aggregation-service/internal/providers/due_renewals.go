// Package providers — due_renewals.go implements the "due-renewals"
// data provider. It fetches trade-licence / BPA registrations that are
// approaching their expiry date from the UPYOG TL/BPAREG search API.
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

const dueRenewalsProviderName = "due-renewals"

// Renewal represents a single licence/registration renewal entry.
type Renewal struct {
	// ID is the unique licence/registration identifier.
	ID string `json:"id"`
	// BusinessService is the service module (e.g. "TL", "BPAREG").
	BusinessService string `json:"businessService"`
	// ApplicationNumber is the human-readable licence number.
	ApplicationNumber string `json:"applicationNumber"`
	// ExpiryDate is the epoch-millis timestamp when the licence expires.
	ExpiryDate int64 `json:"expiryDate"`
	// Status is the current renewal status.
	Status string `json:"status"`
	// DaysRemaining is the number of calendar days until expiry.
	DaysRemaining int `json:"daysRemaining"`
}

// DueRenewalsProvider fetches licences and registrations nearing expiry.
type DueRenewalsProvider struct {
	BaseProvider
}

// NewDueRenewalsProvider creates a new DueRenewalsProvider.
func NewDueRenewalsProvider(
	client *clients.Client,
	c *cache.Cache,
	log *logger.Logger,
	m *metrics.Metrics,
	ttl time.Duration,
) *DueRenewalsProvider {
	return &DueRenewalsProvider{
		BaseProvider: NewBaseProvider(dueRenewalsProviderName, client, c, log, m, ttl),
	}
}

// Execute implements DataProvider. It calls the TL/BPAREG search endpoint
// with a status filter and returns the list of due renewals.
func (p *DueRenewalsProvider) Execute(
	ctx context.Context,
	request dto.ProviderRequest,
	aggReq dto.AggregateRequest,
) (*dto.ProviderResponse, error) {
	// Build status filter from request filters or default to APPROVED.
	statusFilter := "APPROVED"
	if request.Filters != nil {
		if v, ok := request.Filters["status"].(string); ok && v != "" {
			statusFilter = v
		}
	}

	path := fmt.Sprintf(
		"/tl-services/v1/BPAREG/_search?tenantId=%s&status=%s",
		aggReq.TenantID,
		statusFilter,
	)

	headers := map[string]string{
		common.HeaderTenantID: aggReq.TenantID,
	}

	body := struct {
		RequestInfo struct {
			APIID     string `json:"apiId"`
			Ver       string `json:"ver"`
			Ts        int64  `json:"ts"`
			MsgID     string `json:"msgId"`
			AuthToken string `json:"authToken"`
		} `json:"RequestInfo"`
	}{}
	body.RequestInfo.APIID = "upyog-aggregation-service"
	body.RequestInfo.Ver = "1.0"
	body.RequestInfo.Ts = time.Now().UnixMilli()
	body.RequestInfo.MsgID = aggReq.RequestID
	body.RequestInfo.AuthToken = common.AuthToken(ctx)

	resp, err := p.Client.Post(ctx, path, body, headers)
	if err != nil {
		return nil, fmt.Errorf("POST %s: %w", path, err)
	}
	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("POST %s returned status %d", path, resp.StatusCode)
	}

	var result renewalSearchResponse
	if err := json.Unmarshal(resp.Body, &result); err != nil {
		return nil, fmt.Errorf("unmarshal renewals response: %w", err)
	}

	p.Log.WithContext(ctx).Debug("fetched due renewals",
		zap.Int("count", len(result.Licenses)),
	)

	return &dto.ProviderResponse{
		Status: common.StatusSuccess,
		Data:   result.Licenses,
	}, nil
}

// renewalSearchResponse mirrors the shape of the UPYOG TL/BPAREG search
// JSON response.
type renewalSearchResponse struct {
	Licenses []Renewal `json:"Licenses"`
}
