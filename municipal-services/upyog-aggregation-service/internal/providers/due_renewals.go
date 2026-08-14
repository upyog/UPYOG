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
	// Parse mobileNumber and tenantId from the RequestInfo instead of relying on context defaults
	var reqInfo struct {
		UserInfo struct {
			MobileNumber string `json:"mobileNumber"`
			TenantID     string `json:"tenantId"`
		} `json:"userInfo"`
	}
	_ = json.Unmarshal(aggReq.RequestInfo, &reqInfo)
	userMobile := reqInfo.UserInfo.MobileNumber
	tenantID := reqInfo.UserInfo.TenantID
	if tenantID == "" {
		tenantID = aggReq.TenantID
	}

	// Previously called /tl-services/v1/BPAREG/_search. Changed to hit billing service to fetch due renewals
	path := fmt.Sprintf("/billing-service/bill/v2/short/_search?tenantId=%s&mobileNumber=%s&isActive=true&status=ACTIVE", tenantID, userMobile)

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

	var searchResult struct {
		Bill []interface{} `json:"Bill"`
	}
	if err := json.Unmarshal(resp.Body, &searchResult); err != nil {
		return nil, fmt.Errorf("unmarshal renewals response: %w", err)
	}

	p.Log.WithContext(ctx).Debug("fetched due renewals",
		zap.Int("count", len(searchResult.Bill)),
	)

	return &dto.ProviderResponse{
		Status: common.StatusSuccess,
		Data:   searchResult.Bill,
	}, nil
}
