// Package providers — recent_applications.go implements the
// "recent-applications" data provider. It fetches recent applications/bookings
// across Workflow, Advertisement, and CHB services, maps them into a unified
// application item structure, and sorts them with the most recent applications on top.
package providers

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"sort"
	"sync"
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

// RecentApplicationItem represents the unified structure for recent applications across services.
type RecentApplicationItem struct {
	ID                string `json:"id,omitempty"`
	ApplicationNumber string `json:"applicationNumber,omitempty"`
	TenantID          string `json:"tenantId,omitempty"`
	Service           string `json:"service,omitempty"`
	ModuleName        string `json:"moduleName,omitempty"`
	BusinessService   string `json:"businessService,omitempty"`
	Status            string `json:"status,omitempty"`
	CreatedTime       int64  `json:"createdTime,omitempty"`
}

type RecentApplicationsProvider struct {
	BaseProvider
	advClient  *clients.Client
	chbClient  *clients.Client
	sinceDays  int
}

// NewRecentApplicationsProvider creates a new RecentApplicationsProvider.
func NewRecentApplicationsProvider(
	workflowClient *clients.Client,
	advClient *clients.Client,
	chbClient *clients.Client,
	c *cache.Cache,
	log *logger.Logger,
	m *metrics.Metrics,
	ttl time.Duration,
	sinceDays int,
) *RecentApplicationsProvider {
	return &RecentApplicationsProvider{
		BaseProvider: NewBaseProvider(recentApplicationsProviderName, workflowClient, c, log, m, ttl),
		advClient:    advClient,
		chbClient:    chbClient,
		sinceDays:    sinceDays,
	}
}

// Execute fetches recent applications from Workflow, Advertisement, and CHB services concurrently,
// unifies their schema, sorts them in descending order of createdTime (most recent first), and applies pagination.
func (p *RecentApplicationsProvider) Execute(
	ctx context.Context,
	request dto.ProviderRequest,
	aggReq dto.AggregateRequest,
) (*dto.ProviderResponse, error) {
	var (
		wg       sync.WaitGroup
		mu       sync.Mutex
		allApps  []RecentApplicationItem
	)

	// Fetch Workflow process instances
	wg.Add(1)
	go func() {
		defer wg.Done()
		apps, err := p.fetchWorkflowApplications(ctx, request, aggReq)
		if err != nil {
			p.Log.WithContext(ctx).Error("failed to fetch workflow recent applications", zap.Error(err))
			return
		}
		p.Log.WithContext(ctx).Debug("fetched workflow apps count", zap.Int("count", len(apps)))
		mu.Lock()
		allApps = append(allApps, apps...)
		mu.Unlock()
	}()

	// Fetch Advertisement bookings
	wg.Add(1)
	go func() {
		defer wg.Done()
		apps, err := p.fetchAdvBookings(ctx, request, aggReq)
		if err != nil {
			p.Log.WithContext(ctx).Error("failed to fetch adv recent applications", zap.Error(err))
			return
		}
		p.Log.WithContext(ctx).Debug("fetched adv apps count", zap.Int("count", len(apps)))
		mu.Lock()
		allApps = append(allApps, apps...)
		mu.Unlock()
	}()

	// Fetch CHB bookings
	wg.Add(1)
	go func() {
		defer wg.Done()
		apps, err := p.fetchChbBookings(ctx, request, aggReq)
		if err != nil {
			p.Log.WithContext(ctx).Error("failed to fetch chb recent applications", zap.Error(err))
			return
		}
		p.Log.WithContext(ctx).Debug("fetched chb apps count", zap.Int("count", len(apps)))
		mu.Lock()
		allApps = append(allApps, apps...)
		mu.Unlock()
	}()

	wg.Wait()

	// Sort final combined list in descending order of createdTime (recent on top)
	sort.Slice(allApps, func(i, j int) bool {
		return allApps[i].CreatedTime > allApps[j].CreatedTime
	})

	// Apply pagination if specified
	finalApps := allApps
	if request.Pagination != nil {
		offset := request.Pagination.Page * request.Pagination.Size
		limit := request.Pagination.Size

		if offset < len(allApps) {
			end := offset + limit
			if end > len(allApps) {
				end = len(allApps)
			}
			finalApps = allApps[offset:end]
		} else {
			finalApps = []RecentApplicationItem{}
		}
	}

	type RecentApplicationsData struct {
		Applications []RecentApplicationItem `json:"applications"`
		TotalCount   int                     `json:"totalCount"`
	}

	data := RecentApplicationsData{
		Applications: finalApps,
		TotalCount:   len(allApps),
	}

	p.Log.WithContext(ctx).Debug("fetched total recent applications",
		zap.Int("totalCombined", len(allApps)),
		zap.Int("returnedCount", len(finalApps)),
	)

	return &dto.ProviderResponse{
		Status: common.StatusSuccess,
		Data:   data,
	}, nil
}

// fetchWorkflowApplications fetches process instances from Workflow dashboard search API.
func (p *RecentApplicationsProvider) fetchWorkflowApplications(
	ctx context.Context,
	request dto.ProviderRequest,
	aggReq dto.AggregateRequest,
) ([]RecentApplicationItem, error) {
	path := "/egov-workflow-v2/egov-wf/process/dashboard/_search"
	headers := map[string]string{
		common.HeaderTenantID: aggReq.TenantID,
	}

	body := recentAppSearchBody{}
	body.RequestInfo = aggReq.RequestInfo
	body.Criteria.TenantID = aggReq.TenantID

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

	body.Criteria.Offset = 0
	body.Criteria.Limit = 100

	if p.sinceDays > 0 {
		body.Criteria.FromDate = time.Now().AddDate(0, 0, -p.sinceDays).UnixMilli()
	}

	resp, err := p.Client.Post(ctx, path, body, headers)
	if err != nil {
		return nil, fmt.Errorf("POST %s: %w", path, err)
	}
	if resp.StatusCode != http.StatusOK {
		p.Log.WithContext(ctx).Warn("workflow search status non-200", zap.Int("status", resp.StatusCode), zap.String("body", string(resp.Body)))
		return nil, fmt.Errorf("POST %s returned status %d", path, resp.StatusCode)
	}

	var result recentAppProcessInstanceSearchResponse
	if err := json.Unmarshal(resp.Body, &result); err != nil {
		p.Log.WithContext(ctx).Warn("workflow search unmarshal error", zap.Error(err), zap.String("body", string(resp.Body)))
		return nil, fmt.Errorf("unmarshal workflow response: %w", err)
	}

	items := make([]RecentApplicationItem, 0, len(result.ProcessInstances))
	for _, pi := range result.ProcessInstances {
		items = append(items, RecentApplicationItem{
			ID:                pi.ID,
			ApplicationNumber: pi.BusinessID,
			TenantID:          pi.TenantID,
			Service:           "WORKFLOW",
			ModuleName:        pi.ModuleName,
			BusinessService:   pi.BusinessService,
			Status:            pi.Action,
			CreatedTime:       pi.AuditDetails.CreatedTime,
		})
	}
	return items, nil
}

// fetchAdvBookings fetches advertisement bookings from adv-services.
func (p *RecentApplicationsProvider) fetchAdvBookings(
	ctx context.Context,
	request dto.ProviderRequest,
	aggReq dto.AggregateRequest,
) ([]RecentApplicationItem, error) {
	if p.advClient == nil {
		return nil, nil
	}

	var reqInfo struct {
		UserInfo struct {
			MobileNumber string `json:"mobileNumber"`
		} `json:"userInfo"`
	}
	_ = json.Unmarshal(aggReq.RequestInfo, &reqInfo)

	path := fmt.Sprintf("/adv-services/booking/v1/_search?tenantId=%s&limit=50&offset=0&sortBy=createdTime&sortOrder=DESC", aggReq.TenantID)
	if reqInfo.UserInfo.MobileNumber != "" {
		path += fmt.Sprintf("&mobileNumber=%s", reqInfo.UserInfo.MobileNumber)
	}

	headers := map[string]string{
		common.HeaderTenantID: aggReq.TenantID,
	}

	body := map[string]interface{}{
		"RequestInfo": aggReq.RequestInfo,
	}

	resp, err := p.advClient.Post(ctx, path, body, headers)
	if err != nil {
		return nil, fmt.Errorf("POST %s: %w", path, err)
	}
	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("POST %s returned status %d", path, resp.StatusCode)
	}

	var result advBookingSearchResponse
	if err := json.Unmarshal(resp.Body, &result); err != nil {
		return nil, fmt.Errorf("unmarshal adv response: %w", err)
	}

	items := make([]RecentApplicationItem, 0, len(result.BookingApplication))
	for _, b := range result.BookingApplication {
		items = append(items, RecentApplicationItem{
			ID:                b.BookingId,
			ApplicationNumber: b.BookingNo,
			TenantID:          b.TenantId,
			Service:           "ADV",
			ModuleName:        "ADV",
			BusinessService:   "advertisement-service",
			Status:            b.BookingStatus,
			CreatedTime:       b.AuditDetails.CreatedTime,
		})
	}
	return items, nil
}

// fetchChbBookings fetches hall bookings from chb-services.
func (p *RecentApplicationsProvider) fetchChbBookings(
	ctx context.Context,
	request dto.ProviderRequest,
	aggReq dto.AggregateRequest,
) ([]RecentApplicationItem, error) {
	if p.chbClient == nil {
		return nil, nil
	}

	var reqInfo struct {
		UserInfo struct {
			MobileNumber string `json:"mobileNumber"`
		} `json:"userInfo"`
	}
	_ = json.Unmarshal(aggReq.RequestInfo, &reqInfo)

	path := fmt.Sprintf("/chb-services/booking/v1/_search?tenantId=%s&limit=50&offset=0&sortBy=createdTime&sortOrder=DESC", aggReq.TenantID)
	if reqInfo.UserInfo.MobileNumber != "" {
		path += fmt.Sprintf("&mobileNumber=%s", reqInfo.UserInfo.MobileNumber)
	}

	headers := map[string]string{
		common.HeaderTenantID: aggReq.TenantID,
	}

	body := map[string]interface{}{
		"RequestInfo": aggReq.RequestInfo,
	}

	resp, err := p.chbClient.Post(ctx, path, body, headers)
	if err != nil {
		return nil, fmt.Errorf("POST %s: %w", path, err)
	}
	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("POST %s returned status %d", path, resp.StatusCode)
	}

	var result chbBookingSearchResponse
	if err := json.Unmarshal(resp.Body, &result); err != nil {
		return nil, fmt.Errorf("unmarshal chb response: %w", err)
	}

	items := make([]RecentApplicationItem, 0, len(result.BookingApplication))
	for _, b := range result.BookingApplication {
		items = append(items, RecentApplicationItem{
			ID:                b.BookingId,
			ApplicationNumber: b.BookingNo,
			TenantID:          b.TenantId,
			Service:           "CHB",
			ModuleName:        "CHB",
			BusinessService:   "chb-services",
			Status:            b.BookingStatus,
			CreatedTime:       b.AuditDetails.CreatedTime,
		})
	}
	return items, nil
}

type recentAppSearchBody struct {
	RequestInfo json.RawMessage `json:"RequestInfo"`
	Criteria    struct {
		TenantID  string   `json:"tenantId"`
		CreatedBy string   `json:"createdBy"`
		Offset    int      `json:"offset"`
		Limit     int      `json:"limit"`
		Status    []string `json:"status,omitempty"`
		FromDate  int64    `json:"fromDate,omitempty"`
		ToDate    int64    `json:"toDate,omitempty"`
	} `json:"ProcessInstanceSearchCriteria"`
}

type recentAppProcessInstanceSearchResponse struct {
	ProcessInstances []recentAppProcessInstance `json:"ProcessInstances"`
	TotalCount       int                        `json:"totalCount"`
}

type recentAppProcessInstance struct {
	ID              string       `json:"id"`
	TenantID        string       `json:"tenantId"`
	BusinessService string       `json:"businessService"`
	BusinessID      string       `json:"businessId"`
	Action          string       `json:"action"`
	ModuleName      string       `json:"moduleName"`
	AuditDetails    auditDetails `json:"auditDetails"`
}

type advBookingSearchResponse struct {
	BookingApplication []advBookingItem `json:"bookingApplication"`
}

type advBookingItem struct {
	BookingId     string       `json:"bookingId"`
	BookingNo     string       `json:"bookingNo"`
	BookingStatus string       `json:"bookingStatus"`
	TenantId      string       `json:"tenantId"`
	AuditDetails  auditDetails `json:"auditDetails"`
}

type chbBookingSearchResponse struct {
	BookingApplication []chbBookingItem `json:"hallsBookingApplication"`
}

type chbBookingItem struct {
	BookingId     string       `json:"bookingId"`
	BookingNo     string       `json:"bookingNo"`
	BookingStatus string       `json:"bookingStatus"`
	TenantId      string       `json:"tenantId"`
	AuditDetails  auditDetails `json:"auditDetails"`
}

type auditDetails struct {
	CreatedTime int64 `json:"createdTime"`
}
