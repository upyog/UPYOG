// Package providers — draft_applications.go implements the
// "draft-applications" data provider. It fetches the user's saved
// draft applications from the upyog-draft-service, filtered by
// ACTIVE status and supporting pagination.
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

const draftApplicationsProviderName = "draft-applications"

const draftSearchPath = "/upyog-draft-service/draft/v1/_search"

// DraftApplication represents a saved but unsubmitted application.
type DraftApplication struct {
	// ID is the unique draft identifier.
	ID string `json:"id"`
	// BusinessService is the service module (e.g. "PT", "TL").
	BusinessService string `json:"businessService"`
	// ApplicationNumber is the human-readable draft number.
	ApplicationNumber string `json:"applicationNumber"`
	// LastModifiedTime is the epoch-millis timestamp of the last update.
	LastModifiedTime int64 `json:"lastModifiedTime"`
	// CompletionPercentage is the percentage of the form that has been filled.
	CompletionPercentage float64 `json:"completionPercentage"`
	// RedirectURL is the action link to resume draft application.
	RedirectURL string `json:"redirectUrl"`
}

// DraftApplicationsProvider retrieves active drafts from upyog-draft-service.
type DraftApplicationsProvider struct {
	BaseProvider
}

// NewDraftApplicationsProvider creates a new DraftApplicationsProvider.
func NewDraftApplicationsProvider(
	client *clients.Client,
	c *cache.Cache,
	log *logger.Logger,
	m *metrics.Metrics,
	ttl time.Duration,
) *DraftApplicationsProvider {
	return &DraftApplicationsProvider{
		BaseProvider: NewBaseProvider(draftApplicationsProviderName, client, c, log, m, ttl),
	}
}

// Execute implements DataProvider. It queries draft/v1/_search with
// status=ACTIVE and the requested pagination.
func (p *DraftApplicationsProvider) Execute(
	ctx context.Context,
	request dto.ProviderRequest,
	aggReq dto.AggregateRequest,
) (*dto.ProviderResponse, error) {
	// Extract user UUID directly from the RequestInfo instead of context to fix draft mapping
	var reqInfo struct {
		UserInfo struct {
			UUID string `json:"uuid"`
		} `json:"userInfo"`
	}
	_ = json.Unmarshal(aggReq.RequestInfo, &reqInfo)
	userUUID := reqInfo.UserInfo.UUID
	if userUUID == "" {
		userUUID = common.UserID(ctx)
	}

	body := p.buildSearchBody(request, aggReq.TenantID, userUUID)
	body.RequestInfo = aggReq.RequestInfo

	headers := map[string]string{
		common.HeaderTenantID: aggReq.TenantID,
	}

	resp, err := p.Client.Post(ctx, draftSearchPath, body, headers)
	if err != nil {
		return nil, fmt.Errorf("POST %s: %w", draftSearchPath, err)
	}
	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("POST %s returned status %d", draftSearchPath, resp.StatusCode)
	}

	var result draftSearchResponse
	if err := json.Unmarshal(resp.Body, &result); err != nil {
		return nil, fmt.Errorf("unmarshal draft applications response: %w", err)
	}

	items := result.Items
	if len(items) == 0 && len(result.Drafts) > 0 {
		items = result.Drafts
	}

	for i := range items {
		items[i].RedirectURL = fmt.Sprintf("/upyog-ui/citizen/%s/apply?draftId=%s", items[i].BusinessService, items[i].ID)
	}

	p.Log.WithContext(ctx).Debug("fetched draft applications",
		zap.Int("count", len(items)),
	)

	return &dto.ProviderResponse{
		Status: common.StatusSuccess,
		Data:   items,
	}, nil
}

type draftSearchResponse struct {
	Items  []DraftApplication `json:"items"`
	Drafts []DraftApplication `json:"Drafts"`
}

type draftSearchBody struct {
	RequestInfo json.RawMessage     `json:"RequestInfo"`
	Criteria    draftSearchCriteria `json:"draftSearchCriteria"`
}

type draftSearchCriteria struct {
	TenantID  string `json:"tenantId"`
	UserUUID  string `json:"userUuid"`
	Status    string `json:"status"`
	Offset    int    `json:"offset"`
	Limit     int    `json:"limit"`
	SortBy    string `json:"sortBy,omitempty"`
	SortOrder string `json:"sortOrder,omitempty"`
}

func (p *DraftApplicationsProvider) buildSearchBody(
	request dto.ProviderRequest,
	tenantID, userUUID string,
) draftSearchBody {
	criteria := draftSearchCriteria{
		TenantID:  tenantID,
		UserUUID:  userUUID,
		Status:    "ACTIVE",
		Offset:    0,
		Limit:     10,
		SortBy:    "lastModifiedTime",
		SortOrder: "DESC",
	}

	if request.Pagination != nil {
		criteria.Offset = request.Pagination.Page * request.Pagination.Size
		criteria.Limit = request.Pagination.Size
	}
	if request.Sort != nil {
		criteria.SortBy = request.Sort.Field
		criteria.SortOrder = request.Sort.Order
	}

	body := draftSearchBody{Criteria: criteria}
	return body
}
