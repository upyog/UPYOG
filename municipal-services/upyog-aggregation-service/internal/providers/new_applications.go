// Package providers — new_applications.go implements the "new-applications"
// data provider. It fetches newly created workflow applications (process
// instances) from the egov-workflow-v2 service, so the citizen dashboard can
// show applications that entered the workflow within a recent time window.
package providers

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"net/url"
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

const newApplicationsProviderName = "new-applications"

// workflowProcessSearchPath is the egov-workflow-v2 process-instance search
// endpoint. It is a POST endpoint: search criteria travel as query
// parameters while the DIGIT RequestInfo envelope travels in the JSON body.
const workflowProcessSearchPath = "/egov-workflow-v2/egov-wf/process/_search"

// defaultNewApplicationsWindowDays is how far back (in days) the provider
// looks for "new" applications when the client does not send a "sinceDays"
// filter.
const defaultNewApplicationsWindowDays = 7

// WorkflowApplication is the flattened, dashboard-friendly view of a single
// workflow process instance returned by this provider.
type WorkflowApplication struct {
	// ID is the unique process-instance identifier.
	ID string `json:"id"`
	// TenantID is the ULB/tenant the application belongs to.
	TenantID string `json:"tenantId"`
	// BusinessService is the workflow business service (e.g. "NewTL", "PT.CREATE").
	BusinessService string `json:"businessService"`
	// ModuleName is the owning module (e.g. "TL", "PT", "PGR").
	ModuleName string `json:"moduleName"`
	// ApplicationNumber is the human-readable business ID (application number).
	ApplicationNumber string `json:"applicationNumber"`
	// Status is the current application status from the workflow state.
	Status string `json:"status"`
	// State is the raw workflow state name (e.g. "APPLIED", "PENDINGFORPAYMENT").
	State string `json:"state"`
	// Action is the last workflow action taken (e.g. "APPLY", "FORWARD").
	Action string `json:"action"`
	// CreatedTime is the epoch-millis timestamp when the process instance was created.
	CreatedTime int64 `json:"createdTime"`
}

// NewApplicationsData is the payload returned by the new-applications
// provider: the page of applications plus the backend's total match count.
type NewApplicationsData struct {
	Applications []WorkflowApplication `json:"applications"`
	TotalCount   int                   `json:"totalCount"`
}

// NewApplicationsProvider retrieves recently created applications from the
// egov-workflow-v2 process-instance search API.
type NewApplicationsProvider struct {
	BaseProvider
}

// NewNewApplicationsProvider creates a new NewApplicationsProvider backed by
// the supplied workflow-service HTTP client.
func NewNewApplicationsProvider(
	client *clients.Client,
	c *cache.Cache,
	log *logger.Logger,
	m *metrics.Metrics,
	ttl time.Duration,
) *NewApplicationsProvider {
	return &NewApplicationsProvider{
		BaseProvider: NewBaseProvider(newApplicationsProviderName, client, c, log, m, ttl),
	}
}

// Execute implements DataProvider. It builds the workflow search query from
// the provider request (pagination + optional filters), checks the cache,
// calls POST /egov-workflow-v2/egov-wf/process/_search on a miss, and maps
// the ProcessInstances response into dashboard-friendly entries.
//
// Supported request.Filters keys:
//   - "businessService" (string) — restrict to one workflow business service.
//   - "moduleName"      (string) — restrict to one module (e.g. "TL").
//   - "status"          (string) — restrict to one application status/state UUID.
//   - "sinceDays"       (number) — look-back window in days (default 7).
func (p *NewApplicationsProvider) Execute(
	ctx context.Context,
	request dto.ProviderRequest,
	aggReq dto.AggregateRequest,
) (*dto.ProviderResponse, error) {
	query := p.buildSearchQuery(request, aggReq)
	path := workflowProcessSearchPath + "?" + query

	// Cache lookup — the query string uniquely identifies this search.
	cacheKey := p.BuildCacheKey(aggReq.TenantID, query)
	if p.Cache != nil {
		var cached NewApplicationsData
		hit, err := p.GetCached(ctx, cacheKey, &cached)
		if err != nil {
			p.Log.WithContext(ctx).Warn("cache lookup failed for new-applications", zap.Error(err))
		}
		if hit {
			return &dto.ProviderResponse{
				Status: common.StatusSuccess,
				Cached: true,
				Data:   cached,
			}, nil
		}
	}

	headers := map[string]string{
		common.HeaderTenantID: aggReq.TenantID,
	}

	// egov-workflow-v2 expects a DIGIT RequestInfo envelope in the POST body.
	body := workflowSearchBody{
		RequestInfo: aggReq.RequestInfo,
	}

	resp, err := p.Client.Post(ctx, path, body, headers)
	if err != nil {
		return nil, fmt.Errorf("POST %s: %w", path, err)
	}
	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("POST %s returned status %d", path, resp.StatusCode)
	}

	var result processInstanceSearchResponse
	if err := json.Unmarshal(resp.Body, &result); err != nil {
		return nil, fmt.Errorf("unmarshal workflow process search response: %w", err)
	}

	data := NewApplicationsData{
		Applications: mapProcessInstances(result.ProcessInstances),
		TotalCount:   result.TotalCount,
	}

	p.Log.WithContext(ctx).Debug("fetched new applications from workflow",
		zap.Int("count", len(data.Applications)),
		zap.Int("totalCount", data.TotalCount),
	)

	if p.Cache != nil {
		if cacheErr := p.SetCached(ctx, cacheKey, data, p.CacheTTL); cacheErr != nil {
			p.Log.WithContext(ctx).Warn("failed to cache new-applications", zap.Error(cacheErr))
		}
	}

	return &dto.ProviderResponse{
		Status: common.StatusSuccess,
		Data:   data,
	}, nil
}

// buildSearchQuery translates the provider request into the query parameters
// understood by egov-workflow-v2's ProcessInstanceSearchCriteria.
func (p *NewApplicationsProvider) buildSearchQuery(
	request dto.ProviderRequest,
	aggReq dto.AggregateRequest,
) string {
	params := url.Values{}
	params.Set("tenantId", aggReq.TenantID)
	params.Set("history", "false")

	// Look-back window: applications created within the last N days.
	sinceDays := defaultNewApplicationsWindowDays
	if request.Filters != nil {
		// JSON numbers arrive as float64 through the free-form filter map.
		if v, ok := request.Filters["sinceDays"].(float64); ok && v > 0 {
			sinceDays = int(v)
		}
	}
	fromDate := time.Now().AddDate(0, 0, -sinceDays).UnixMilli()
	params.Set("fromDate", strconv.FormatInt(fromDate, 10))

	// Pagination → offset/limit.
	offset, limit := 0, 10
	if request.Pagination != nil {
		offset = request.Pagination.Page * request.Pagination.Size
		limit = request.Pagination.Size
	}
	params.Set("offset", strconv.Itoa(offset))
	params.Set("limit", strconv.Itoa(limit))

	// Optional pass-through filters.
	if request.Filters != nil {
		if v, ok := request.Filters["businessService"].(string); ok && v != "" {
			params.Set("businessService", v)
		}
		if v, ok := request.Filters["moduleName"].(string); ok && v != "" {
			params.Set("moduleName", v)
		}
		if v, ok := request.Filters["status"].(string); ok && v != "" {
			params.Set("status", v)
		}
	}

	return params.Encode()
}

// workflowSearchBody is the JSON body sent to the process search endpoint.
type workflowSearchBody struct {
	RequestInfo json.RawMessage `json:"RequestInfo"`
}

// processInstanceSearchResponse mirrors the shape of the egov-workflow-v2
// /process/_search JSON response.
type processInstanceSearchResponse struct {
	ProcessInstances []processInstance `json:"ProcessInstances"`
	TotalCount       int               `json:"totalCount"`
}

// processInstance is the subset of the workflow ProcessInstance model this
// provider consumes.
type processInstance struct {
	ID              string          `json:"id"`
	TenantID        string          `json:"tenantId"`
	BusinessService string          `json:"businessService"`
	BusinessID      string          `json:"businessId"`
	Action          string          `json:"action"`
	ModuleName      string          `json:"moduleName"`
	State           *processState   `json:"state"`
	AuditDetails    *wfAuditDetails `json:"auditDetails"`
}

// processState is the subset of the workflow State model this provider consumes.
type processState struct {
	State             string `json:"state"`
	ApplicationStatus string `json:"applicationStatus"`
}

// wfAuditDetails carries the creation timestamp of a process instance.
type wfAuditDetails struct {
	CreatedTime int64 `json:"createdTime"`
}

// mapProcessInstances flattens raw workflow process instances into
// dashboard-friendly WorkflowApplication entries.
func mapProcessInstances(instances []processInstance) []WorkflowApplication {
	apps := make([]WorkflowApplication, 0, len(instances))
	for _, pi := range instances {
		app := WorkflowApplication{
			ID:                pi.ID,
			TenantID:          pi.TenantID,
			BusinessService:   pi.BusinessService,
			ModuleName:        pi.ModuleName,
			ApplicationNumber: pi.BusinessID,
			Action:            pi.Action,
		}
		if pi.State != nil {
			app.Status = pi.State.ApplicationStatus
			app.State = pi.State.State
		}
		if pi.AuditDetails != nil {
			app.CreatedTime = pi.AuditDetails.CreatedTime
		}
		apps = append(apps, app)
	}
	return apps
}
