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

type RecentApplicationsProvider struct {
	BaseProvider
	sinceDays int
}

// NewRecentApplicationsProvider creates a new RecentApplicationsProvider.
func NewRecentApplicationsProvider(
	client *clients.Client,
	c *cache.Cache,
	log *logger.Logger,
	m *metrics.Metrics,
	ttl time.Duration,
	sinceDays int,
) *RecentApplicationsProvider {
	return &RecentApplicationsProvider{
		BaseProvider: NewBaseProvider(recentApplicationsProviderName, client, c, log, m, ttl),
		sinceDays:    sinceDays,
	}
}

// Execute implements DataProvider. It builds the search body with Pagination,
// calls the workflow dashboard search API, and returns the parsed list.
func (p *RecentApplicationsProvider) Execute(
	ctx context.Context,
	request dto.ProviderRequest,
	aggReq dto.AggregateRequest,
) (*dto.ProviderResponse, error) {
	path := "/egov-workflow-v2/egov-wf/process/dashboard/_search"

	headers := map[string]string{
		common.HeaderTenantID: aggReq.TenantID,
	}

	body := recentAppSearchBody{}
	body.RequestInfo = aggReq.RequestInfo
	body.Criteria.TenantID = aggReq.TenantID

	// Extract user UUID directly from the RequestInfo instead of context
	var reqInfo struct {
		UserInfo struct {
			UUID string `json:"uuid"`
		} `json:"userInfo"`
	}
	_ = json.Unmarshal(aggReq.RequestInfo, &reqInfo)
	if reqInfo.UserInfo.UUID != "" {
		body.Criteria.CreatedBy = reqInfo.UserInfo.UUID
	} else {
		body.Criteria.CreatedBy = common.UserID(ctx)
	}
	if request.Pagination != nil {
		body.Criteria.Offset = request.Pagination.Page * request.Pagination.Size
		body.Criteria.Limit = request.Pagination.Size
	} else {
		body.Criteria.Offset = 0
		body.Criteria.Limit = 50
	}

	sinceDays := p.sinceDays
	if sinceDays <= 0 {
		sinceDays = 7
	}
	body.Criteria.FromDate = time.Now().AddDate(0, 0, -sinceDays).UnixMilli()

	body.Criteria.Status = []string{
		"APPLIED",
		"APPROVALPENDING",
		"ASSIGN_DSO",
		"ASSING_DSO",
		"CHALLAN_GENERATED",
		"CREATED",
		"INITIATED",
		"INWORKFLOW",
		"OPEN",
		"PENDINGFORASSIGNMENT",
		"PENDINGPAYMENT",
		"PENDING_APPL_FEE_PAYMENT",
		"PENDING_FOR_APPROVAL",
		"PENDING_FOR_COUNTER_EMPLOYEE_ACTION",
		"PENDING_FOR_FIELD_INSPECTOR_ASSIGNMENT",
		"PENDING_FOR_VERIFICATION",
		"REFUNDPENDING",
		"REQUESTCREATED",
		"SCHEDULED",
		"SEND_TO_CITIZEN",
		"WAITING_FOR_DISPOSAL",
		"WASTE_PICKUP_INPROGRESS",
	}

	resp, err := p.Client.Post(ctx, path, body, headers)
	if err != nil {
		return nil, fmt.Errorf("POST %s: %w", path, err)
	}
	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("POST %s returned status %d", path, resp.StatusCode)
	}

	var result recentAppProcessInstanceSearchResponse
	if err := json.Unmarshal(resp.Body, &result); err != nil {
		return nil, fmt.Errorf("unmarshal workflow response: %w", err)
	}

	p.Log.WithContext(ctx).Debug("fetched recent applications",
		zap.Int("count", len(result.ProcessInstances)),
	)

	return &dto.ProviderResponse{
		Status: common.StatusSuccess,
		Data:   result.ProcessInstances,
	}, nil
}

type recentAppSearchBody struct {
	RequestInfo json.RawMessage `json:"RequestInfo"`
	Criteria    struct {
		TenantID  string   `json:"tenantId"`
		CreatedBy string   `json:"createdBy"`
		Offset    int      `json:"offset"`
		Limit     int      `json:"limit"`
		Status    []string `json:"status"`
		FromDate  int64    `json:"fromDate,omitempty"`
		ToDate    int64    `json:"toDate,omitempty"`
	} `json:"ProcessInstanceSearchCriteria"`
}

type recentAppProcessInstanceSearchResponse struct {
	ProcessInstances []recentAppProcessInstance `json:"ProcessInstances"`
	TotalCount       int                        `json:"totalCount"`
}

type recentAppProcessInstance struct {
	ID              string `json:"id"`
	TenantID        string `json:"tenantId"`
	BusinessService string `json:"businessService"`
	BusinessID      string `json:"businessId"`
	Action          string `json:"action"`
	ModuleName      string `json:"moduleName"`
}
