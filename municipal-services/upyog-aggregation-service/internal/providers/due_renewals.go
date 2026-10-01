// Package providers — due_renewals.go implements the "due-renewals"
// data provider. It fetches trade-licence / BPA registrations that are
// approaching their expiry date from the UPYOG TL/BPAREG search API.
package providers

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"sort"
	"strings"
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
	paymentRedirectURLBase string
}

// NewDueRenewalsProvider creates a new DueRenewalsProvider.
func NewDueRenewalsProvider(
	client *clients.Client,
	c *cache.Cache,
	log *logger.Logger,
	m *metrics.Metrics,
	ttl time.Duration,
	paymentRedirectURLBase string,
) *DueRenewalsProvider {
	if paymentRedirectURLBase == "" {
		paymentRedirectURLBase = "/upyog-ui/citizen/payment/my-bills"
	}
	return &DueRenewalsProvider{
		BaseProvider:           NewBaseProvider(dueRenewalsProviderName, client, c, log, m, ttl),
		paymentRedirectURLBase: strings.TrimRight(paymentRedirectURLBase, "/"),
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

	// Fetch all bills to get accurate totalCount and perform pagination in-memory
	path := fmt.Sprintf("/billing-service/bill/v2/_searchsummary?tenantId=%s&mobileNumber=%s&isActive=true&status=ACTIVE", tenantID, userMobile)

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

	allBills := searchResult.Bill
	totalCount := len(allBills)

	// Enrich each bill with payment redirectUrl
	for i, billRaw := range allBills {
		if billMap, ok := billRaw.(map[string]interface{}); ok {
			consumerCode, _ := billMap["consumerCode"].(string)
			businessService, _ := billMap["businessService"].(string)
			billMap["redirectUrl"] = fmt.Sprintf("%s/%s/%s", p.paymentRedirectURLBase, businessService, consumerCode)
			allBills[i] = billMap
		}
	}

	// Sort bills by dueDate
	sortAsc := true // Default: ascending (earliest due date / imminent deadline first)
	if request.Sort != nil && strings.EqualFold(request.Sort.Order, "DESC") {
		sortAsc = false
	}

	getDueDate := func(billRaw interface{}) int64 {
		if billMap, ok := billRaw.(map[string]interface{}); ok {
			switch v := billMap["dueDate"].(type) {
			case float64:
				return int64(v)
			case int64:
				return v
			case int:
				return int64(v)
			case json.Number:
				n, _ := v.Int64()
				return n
			}
		}
		return 0
	}

	sort.SliceStable(allBills, func(i, j int) bool {
		d1 := getDueDate(allBills[i])
		d2 := getDueDate(allBills[j])
		if d1 == d2 {
			return false
		}
		if d1 == 0 {
			return false // Push items without due date to the bottom
		}
		if d2 == 0 {
			return true
		}
		if sortAsc {
			return d1 < d2
		}
		return d1 > d2
	})

	// Apply pagination if specified in request
	finalBills := allBills
	if request.Pagination != nil {
		offset := request.Pagination.Page * request.Pagination.Size
		limit := request.Pagination.Size

		if offset < len(allBills) {
			end := offset + limit
			if end > len(allBills) {
				end = len(allBills)
			}
			finalBills = allBills[offset:end]
		} else {
			finalBills = []interface{}{}
		}
	}

	p.Log.WithContext(ctx).Debug("fetched due renewals",
		zap.Int("totalCount", totalCount),
		zap.Int("returnedCount", len(finalBills)),
	)

	type DueRenewalsData struct {
		Bills      []interface{} `json:"bills"`
		TotalCount int           `json:"totalCount"`
	}

	return &dto.ProviderResponse{
		Status: common.StatusSuccess,
		Data: DueRenewalsData{
			Bills:      finalBills,
			TotalCount: totalCount,
		},
	}, nil
}
