package engine

import (
	"context"
	"encoding/json"
	"fmt"
	"math"
	"net/url"
	"sort"
	"strconv"
	"strings"
	"sync"

	"go.uber.org/zap"
	"golang.org/x/sync/errgroup"

	"github.com/upyog/upyog-aggregation-service/internal/clients"
	"github.com/upyog/upyog-aggregation-service/internal/dto"
	"github.com/upyog/upyog-aggregation-service/internal/metrics"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

// EmployeeEngine orchestrates multi-service fan-out for employee aggregate data.
type EmployeeEngine struct {
	inboxClient             *clients.Client
	employeeDashboardClient *clients.Client
	workflowClient          *clients.Client
	log                     *logger.Logger
	m                       *metrics.Metrics
}

// NewEmployeeEngine creates a new EmployeeEngine with the required backend clients.
func NewEmployeeEngine(
	inboxClient *clients.Client,
	employeeDashboardClient *clients.Client,
	workflowClient *clients.Client,
	log *logger.Logger,
	m *metrics.Metrics,
) *EmployeeEngine {
	return &EmployeeEngine{
		inboxClient:             inboxClient,
		employeeDashboardClient: employeeDashboardClient,
		workflowClient:          workflowClient,
		log:                     log,
		m:                       m,
	}
}

// AggregateEmployee fans out calls to Inbox and Employee Dashboard backend services concurrently.
// Inbox responses are processed to construct the Workflow object with items sorted by SLA (ASC).
func (e *EmployeeEngine) AggregateEmployee(ctx context.Context, req dto.EmployeeAggregateRequest) *dto.EmployeeAggregateResponse {
	e.log.WithContext(ctx).Info("starting employee aggregate requests fan-out",
		zap.String("tenantId", req.TenantID),
		zap.String("requestId", req.RequestID),
	)

	resp := &dto.EmployeeAggregateResponse{
		Success:   true,
		RequestID: req.RequestID,
		Errors:    make(map[string]string),
	}

	var reqInfo map[string]interface{}
	if len(req.RequestInfo) > 0 {
		_ = json.Unmarshal(req.RequestInfo, &reqInfo)
	}

	hasWorkflow := isJSONRawMessageNotEmpty(req.Workflow)
	hasInbox := isJSONRawMessageNotEmpty(req.Inbox)
	hasEmployeeDashboard := isJSONRawMessageNotEmpty(req.EmployeeDashboard)
	hasServices := len(req.Services) > 0

	hasExplicitFilter := hasWorkflow || hasInbox || hasEmployeeDashboard || hasServices

	callEmployeeDashboard := !hasExplicitFilter || hasEmployeeDashboard
	callInbox := !hasExplicitFilter || hasInbox || hasServices
	callWorkflow := !hasExplicitFilter || hasWorkflow || hasServices

	totalRequestedServices := 0
	if callEmployeeDashboard {
		totalRequestedServices++
	}
	if callInbox {
		totalRequestedServices++
	}
	if callWorkflow {
		totalRequestedServices++
	}

	var mu sync.Mutex
	g, gCtx := errgroup.WithContext(ctx)

	// 1. Employee Dashboard call
	if callEmployeeDashboard {
		g.Go(func() error {
			defer func() {
				if r := recover(); r != nil {
					mu.Lock()
					resp.Errors["employeeDashboard"] = "panic occurred while calling employee dashboard service"
					mu.Unlock()
				}
			}()

			var payload interface{}
			if len(req.EmployeeDashboard) > 0 {
				var raw map[string]interface{}
				if err := json.Unmarshal(req.EmployeeDashboard, &raw); err == nil {
					if _, hasReqInfo := raw["RequestInfo"]; !hasReqInfo && reqInfo != nil {
						raw["RequestInfo"] = reqInfo
					}
					if _, hasTenant := raw["tenantId"]; !hasTenant && req.TenantID != "" {
						raw["tenantId"] = req.TenantID
					}
					payload = raw
				} else {
					payload = req.EmployeeDashboard
				}
			} else {
				payload = map[string]interface{}{
					"tenantId":    req.TenantID,
					"RequestInfo": reqInfo,
				}
			}

			payloadBytes, _ := json.Marshal(payload)
			e.log.WithContext(gCtx).Info("outbound employee-dashboard request payload",
				zap.String("endpoint", "/employee-dashboard/v2/_search"),
				zap.String("payload", string(payloadBytes)),
			)

			httpResp, err := e.employeeDashboardClient.Post(gCtx, "/employee-dashboard/v2/_search", payload, nil)
			if err != nil {
				e.log.WithContext(gCtx).Error("employee-dashboard service call failed", zap.Error(err))
				mu.Lock()
				resp.Errors["employeeDashboard"] = err.Error()
				mu.Unlock()
				return nil
			}

			e.log.WithContext(gCtx).Info("employee-dashboard service response received",
				zap.Int("status", httpResp.StatusCode),
				zap.String("responseBody", string(httpResp.Body)),
			)

			mu.Lock()
			resp.EmployeeDashboard = json.RawMessage(httpResp.Body)
			mu.Unlock()
			return nil
		})
	}

	// 2. Inbox Service call (Supports multi-service fan-out; also extracts & sorts items by SLA to populate Workflow response)
	if callInbox {
		g.Go(func() error {
			defer func() {
				if r := recover(); r != nil {
					mu.Lock()
					resp.Errors["inbox"] = "panic occurred while calling inbox service"
					mu.Unlock()
				}
			}()

			inboxPayloads := e.buildInboxPayloads(req, reqInfo)
			if len(inboxPayloads) == 0 {
				return nil
			}

			var inboxMu sync.Mutex
			moduleResponses := make(map[string]json.RawMessage)
			var inboxErrors []string

			inboxGroup, igCtx := errgroup.WithContext(gCtx)

			for i, item := range inboxPayloads {
				itemInfo := item
				idx := i
				inboxGroup.Go(func() error {
					payloadBytes, _ := json.Marshal(itemInfo.Payload)
					e.log.WithContext(igCtx).Info("outbound inbox request payload",
						zap.Int("index", idx),
						zap.String("moduleName", itemInfo.ModuleName),
						zap.String("endpoint", "/inbox/v1/_search"),
						zap.String("payload", string(payloadBytes)),
					)

					httpResp, err := e.inboxClient.Post(igCtx, "/inbox/v1/_search", itemInfo.Payload, nil)
					if err != nil {
						e.log.WithContext(igCtx).Error("inbox service call failed", zap.Int("index", idx), zap.String("moduleName", itemInfo.ModuleName), zap.Error(err))
						inboxMu.Lock()
						inboxErrors = append(inboxErrors, itemInfo.ModuleName+": "+err.Error())
						if httpResp != nil && len(httpResp.Body) > 0 {
							moduleResponses[itemInfo.ModuleName] = json.RawMessage(httpResp.Body)
						}
						inboxMu.Unlock()
						return nil
					}

					e.log.WithContext(igCtx).Info("inbox service response received",
						zap.Int("index", idx),
						zap.String("moduleName", itemInfo.ModuleName),
						zap.Int("status", httpResp.StatusCode),
						zap.String("responseBody", string(httpResp.Body)),
					)

					inboxMu.Lock()
					moduleResponses[itemInfo.ModuleName] = json.RawMessage(httpResp.Body)
					inboxMu.Unlock()
					return nil
				})
			}

			_ = inboxGroup.Wait()

			if len(moduleResponses) > 0 {
				mergedResult, _ := json.Marshal(moduleResponses)
				mu.Lock()
				resp.Inbox = json.RawMessage(mergedResult)
				mu.Unlock()
			} else if len(inboxErrors) > 0 {
				mu.Lock()
				resp.Errors["inbox"] = strings.Join(inboxErrors, "; ")
				mu.Unlock()
			}

			return nil
		})
	}

	// 3. Workflow Service call (POST /egov-workflow-v2/egov-wf/process/_search?tenantId={{tenantId}}&assignee={{uuid}})
	if callWorkflow {
		g.Go(func() error {
			defer func() {
				if r := recover(); r != nil {
					mu.Lock()
					resp.Errors["workflow"] = "panic occurred while calling workflow service"
					mu.Unlock()
				}
			}()

			_, tenantID := e.extractUUIDAndTenantID(req, reqInfo)
			limit, offset := e.extractWorkflowLimitAndOffset(req)

			var queryParts []string
			if tenantID != "" {
				queryParts = append(queryParts, "tenantId="+url.QueryEscape(tenantID))
			}
			queryParts = append(queryParts, fmt.Sprintf("limit=%d", limit))
			queryParts = append(queryParts, fmt.Sprintf("offset=%d", offset))

			endpoint := "/egov-workflow-v2/egov-wf/process/_search"
			if len(queryParts) > 0 {
				endpoint += "?" + strings.Join(queryParts, "&")
			}

			payload := map[string]interface{}{
				"RequestInfo": reqInfo,
			}

			payloadBytes, _ := json.Marshal(payload)
			e.log.WithContext(gCtx).Info("outbound workflow request payload",
				zap.String("endpoint", endpoint),
				zap.String("payload", string(payloadBytes)),
			)

			httpResp, err := e.workflowClient.Post(gCtx, endpoint, payload, nil)
			if err != nil {
				e.log.WithContext(gCtx).Error("workflow service call failed", zap.Error(err))
				mu.Lock()
				resp.Errors["workflow"] = err.Error()
				mu.Unlock()
				return nil
			}

			e.log.WithContext(gCtx).Info("workflow service response received",
				zap.Int("status", httpResp.StatusCode),
				zap.String("responseBody", string(httpResp.Body)),
			)

			mu.Lock()
			resp.Workflow = json.RawMessage(httpResp.Body)
			mu.Unlock()
			return nil
		})
	}

	_ = g.Wait()

	// Ensure resp.Workflow process instances are ordered by SLA in ascending (ASC) order
	if callWorkflow {
		e.ensureWorkflowSortedBySLA(resp)
	}

	if totalRequestedServices > 0 && len(resp.Errors) == totalRequestedServices {
		resp.Success = false
	}

	e.log.WithContext(ctx).Info("employee aggregate requests fan-out complete",
		zap.Bool("success", resp.Success),
		zap.Int("errorCount", len(resp.Errors)),
	)

	return resp
}

// ensureWorkflowSortedBySLA sorts process instances in resp.Workflow by SLA (ASC).
// If resp.Workflow has no ProcessInstances, it extracts and sorts ProcessInstances from resp.Inbox.
func (e *EmployeeEngine) ensureWorkflowSortedBySLA(resp *dto.EmployeeAggregateResponse) {
	if resp == nil {
		return
	}

	// Case 1: resp.Workflow is present and contains ProcessInstances
	if len(resp.Workflow) > 0 {
		var wfMap map[string]interface{}
		if err := json.Unmarshal(resp.Workflow, &wfMap); err == nil {
			if instances, ok := wfMap["ProcessInstances"].([]interface{}); ok && len(instances) > 0 {
				sortProcessInstancesBySLA(instances)
				wfMap["ProcessInstances"] = instances
				if b, err := json.Marshal(wfMap); err == nil {
					resp.Workflow = json.RawMessage(b)
					return
				}
			}
		}
	}

	// Case 2: Fallback — extract ProcessInstances from resp.Inbox if resp.Workflow has no items
	if len(resp.Inbox) == 0 {
		return
	}

	var inboxModules map[string]json.RawMessage
	if err := json.Unmarshal(resp.Inbox, &inboxModules); err != nil {
		return
	}

	var allInstances []interface{}
	for _, rawModResp := range inboxModules {
		var modResp map[string]interface{}
		if err := json.Unmarshal(rawModResp, &modResp); err != nil {
			continue
		}
		items, ok := modResp["items"].([]interface{})
		if !ok {
			continue
		}
		for _, item := range items {
			itemMap, ok := item.(map[string]interface{})
			if !ok {
				continue
			}
			if pi, ok := itemMap["ProcessInstance"].(map[string]interface{}); ok && pi != nil {
				allInstances = append(allInstances, pi)
			}
		}
	}

	if len(allInstances) == 0 {
		return
	}

	sortProcessInstancesBySLA(allInstances)

	newWf := map[string]interface{}{
		"ResponseInfo":     nil,
		"ProcessInstances": allInstances,
		"totalCount":       len(allInstances),
	}
	if b, err := json.Marshal(newWf); err == nil {
		resp.Workflow = json.RawMessage(b)
	}
}

// sortProcessInstancesBySLA sorts a slice of ProcessInstance maps/interfaces by SLA (ASC).
func sortProcessInstancesBySLA(instances []interface{}) {
	sort.SliceStable(instances, func(i, j int) bool {
		piI, _ := instances[i].(map[string]interface{})
		piJ, _ := instances[j].(map[string]interface{})
		return extractSLA(piI) < extractSLA(piJ)
	})
}

// extractSLA extracts the numeric SLA value from a ProcessInstance map.
func extractSLA(pi map[string]interface{}) int64 {
	if pi == nil {
		return math.MaxInt64
	}
	keys := []string{"businesssServiceSla", "businessServiceSla", "stateSla", "sla"}
	for _, k := range keys {
		if val, ok := pi[k]; ok && val != nil {
			if num, ok := parseAsInt64(val); ok {
				return num
			}
		}
	}
	return math.MaxInt64
}

// extractUUIDAndTenantID extracts tenantId and user uuid from requestInfo map or request struct.
func (e *EmployeeEngine) extractUUIDAndTenantID(req dto.EmployeeAggregateRequest, reqInfo map[string]interface{}) (string, string) {
	tenantID := req.TenantID
	var uuid string

	if reqInfo != nil {
		if userInfo, ok := reqInfo["userInfo"].(map[string]interface{}); ok {
			if u, ok := userInfo["uuid"].(string); ok && u != "" {
				uuid = u
			}
			if tenantID == "" {
				if t, ok := userInfo["tenantId"].(string); ok && t != "" {
					tenantID = t
				}
			}
		}
		if uuid == "" {
			if u, ok := reqInfo["uuid"].(string); ok && u != "" {
				uuid = u
			}
		}
		if tenantID == "" {
			if t, ok := reqInfo["tenantId"].(string); ok && t != "" {
				tenantID = t
			}
		}
	}

	return uuid, tenantID
}

// extractWorkflowLimitAndOffset extracts limit and offset from request struct or workflow JSON criteria.
func (e *EmployeeEngine) extractWorkflowLimitAndOffset(req dto.EmployeeAggregateRequest) (int, int) {
	limit := 10
	offset := 0

	if req.Limit != nil {
		limit = *req.Limit
	}
	if req.Offset != nil {
		offset = *req.Offset
	}

	if len(req.Workflow) > 0 {
		var wfObj map[string]interface{}
		if err := json.Unmarshal(req.Workflow, &wfObj); err == nil {
			if v, ok := wfObj["isViewAll"].(bool); ok && v {
				limit = 300
			}
			if lim, ok := wfObj["limit"]; ok {
				if num, ok := parseAsInt64(lim); ok {
					if num <= 0 {
						limit = 300
					} else {
						limit = int(num)
					}
				}
			}
			if off, ok := wfObj["offset"]; ok {
				if num, ok := parseAsInt64(off); ok && num >= 0 {
					offset = int(num)
				}
			}
		} else {
			var wfArr []map[string]interface{}
			if err := json.Unmarshal(req.Workflow, &wfArr); err == nil && len(wfArr) > 0 {
				if v, ok := wfArr[0]["isViewAll"].(bool); ok && v {
					limit = 300
				}
				if lim, ok := wfArr[0]["limit"]; ok {
					if num, ok := parseAsInt64(lim); ok {
						if num <= 0 {
							limit = 300
						} else {
							limit = int(num)
						}
					}
				}
				if off, ok := wfArr[0]["offset"]; ok {
					if num, ok := parseAsInt64(off); ok && num >= 0 {
						offset = int(num)
					}
				}
			}
		}
	}

	return limit, offset
}

// InboxRequestItem pairs an Inbox request payload with its target module name.
type InboxRequestItem struct {
	ModuleName string
	Payload    interface{}
}

// buildInboxPayloads constructs one or more Inbox API request payloads for single or multi-module queries.
func (e *EmployeeEngine) buildInboxPayloads(req dto.EmployeeAggregateRequest, reqInfo map[string]interface{}) []InboxRequestItem {
	var payloads []InboxRequestItem

	globalIsViewAll := false
	if len(req.Workflow) > 0 {
		var wfObj map[string]interface{}
		if err := json.Unmarshal(req.Workflow, &wfObj); err == nil {
			if v, ok := wfObj["isViewAll"].(bool); ok && v {
				globalIsViewAll = true
			}
			if limitVal, ok := wfObj["limit"]; ok {
				if num, ok := parseAsInt64(limitVal); ok && num <= 0 {
					globalIsViewAll = true
				}
			}
		}
	}

	// Case A: req.Inbox is provided (Array or Object)
	if len(req.Inbox) > 0 {
		var rawArray []map[string]interface{}
		if err := json.Unmarshal(req.Inbox, &rawArray); err == nil {
			for _, raw := range rawArray {
				p := e.formatInboxPayload(raw, req.TenantID, reqInfo, globalIsViewAll)
				if p != nil {
					modName := extractModuleNameFromPayload(p)
					payloads = append(payloads, InboxRequestItem{
						ModuleName: modName,
						Payload:    p,
					})
				}
			}
			return payloads
		}

		var rawObj map[string]interface{}
		if err := json.Unmarshal(req.Inbox, &rawObj); err == nil {
			p := e.formatInboxPayload(rawObj, req.TenantID, reqInfo, globalIsViewAll)
			if p != nil {
				modName := extractModuleNameFromPayload(p)
				payloads = append(payloads, InboxRequestItem{
					ModuleName: modName,
					Payload:    p,
				})
			}
			return payloads
		}
	}

	// Case B: req.Workflow is provided (e.g. when user clicks "View All" on Workflow)
	if len(req.Workflow) > 0 {
		var rawArray []map[string]interface{}
		if err := json.Unmarshal(req.Workflow, &rawArray); err == nil {
			for _, raw := range rawArray {
				p := e.formatInboxPayload(raw, req.TenantID, reqInfo, true)
				if p != nil {
					modName := extractModuleNameFromPayload(p)
					payloads = append(payloads, InboxRequestItem{
						ModuleName: modName,
						Payload:    p,
					})
				}
			}
			return payloads
		}

		var rawObj map[string]interface{}
		if err := json.Unmarshal(req.Workflow, &rawObj); err == nil {
			p := e.formatInboxPayload(rawObj, req.TenantID, reqInfo, true)
			if p != nil {
				modName := extractModuleNameFromPayload(p)
				payloads = append(payloads, InboxRequestItem{
					ModuleName: modName,
					Payload:    p,
				})
			}
			return payloads
		}
	}

	// Case C: Construct payloads based on req.Services list (e.g. ["PT", "PTR"])
	if len(req.Services) > 0 {
		for _, svc := range req.Services {
			modName, bsList := mapServiceToInboxCriteria(svc)
			limitVal := 10
			if globalIsViewAll {
				limitVal = 300
			}
			p := map[string]interface{}{
				"RequestInfo": reqInfo,
				"inbox": map[string]interface{}{
					"tenantId": req.TenantID,
					"processSearchCriteria": map[string]interface{}{
						"moduleName":      modName,
						"businessService": bsList,
					},
					"moduleSearchCriteria": map[string]interface{}{
						"isInboxSearch": true,
					},
					"limit":  limitVal,
					"offset": 0,
				},
			}
			payloads = append(payloads, InboxRequestItem{
				ModuleName: modName,
				Payload:    p,
			})
		}
		return payloads
	}

	// Default fallback payload
	defaultPayload := map[string]interface{}{
		"RequestInfo": reqInfo,
		"inbox": map[string]interface{}{
			"tenantId": req.TenantID,
			"processSearchCriteria": map[string]interface{}{
				"moduleName": "PT",
			},
			"moduleSearchCriteria": map[string]interface{}{
				"isInboxSearch": true,
			},
			"limit":  10,
			"offset": 0,
		},
	}
	return []InboxRequestItem{
		{
			ModuleName: "PT",
			Payload:    defaultPayload,
		},
	}
}

// formatInboxPayload normalizes a single inbox search map.
func (e *EmployeeEngine) formatInboxPayload(raw map[string]interface{}, defaultTenantID string, reqInfo map[string]interface{}, globalIsViewAll bool) interface{} {
	var inboxObj map[string]interface{}
	if inboxData, exists := raw["inbox"]; exists {
		if m, ok := inboxData.(map[string]interface{}); ok {
			inboxObj = m
		} else {
			inboxObj = raw
		}
	} else {
		inboxObj = raw
	}

	// Copy map to cleanObj, excluding non-DTO fields like isViewAll that cause Jackson deserialization errors
	cleanObj := make(map[string]interface{})
	for k, v := range inboxObj {
		if k != "isViewAll" {
			cleanObj[k] = v
		}
	}

	if _, hasTenant := cleanObj["tenantId"]; !hasTenant && defaultTenantID != "" {
		cleanObj["tenantId"] = defaultTenantID
	}

	// Ensure processSearchCriteria exists and businessService is populated
	var psc map[string]interface{}
	if existingPSC, exists := cleanObj["processSearchCriteria"].(map[string]interface{}); exists {
		psc = existingPSC
	} else {
		psc = make(map[string]interface{})
		if mod, ok := cleanObj["moduleName"]; ok {
			psc["moduleName"] = mod
			delete(cleanObj, "moduleName")
		}
		if bs, ok := cleanObj["businessService"]; ok {
			psc["businessService"] = bs
			delete(cleanObj, "businessService")
		}
	}

	if modName, ok := psc["moduleName"].(string); ok && modName != "" {
		if bs, hasBS := psc["businessService"]; !hasBS || bs == nil {
			_, bsList := mapServiceToInboxCriteria(modName)
			psc["businessService"] = bsList
		} else if str, ok := bs.(string); ok {
			psc["businessService"] = []string{str}
		}
	}
	cleanObj["processSearchCriteria"] = psc

	// Ensure moduleSearchCriteria exists and contains tenantId
	var msc map[string]interface{}
	if existingMSC, hasMSC := cleanObj["moduleSearchCriteria"].(map[string]interface{}); hasMSC {
		msc = existingMSC
	} else {
		msc = make(map[string]interface{})
		msc["isInboxSearch"] = true
	}
	if _, hasTenant := msc["tenantId"]; !hasTenant {
		if t, ok := cleanObj["tenantId"].(string); ok && t != "" {
			msc["tenantId"] = t
		} else if defaultTenantID != "" {
			msc["tenantId"] = defaultTenantID
		}
	}
	cleanObj["moduleSearchCriteria"] = msc

	// Ensure offset exists
	if _, hasOffset := cleanObj["offset"]; !hasOffset {
		cleanObj["offset"] = 0
	}

	// Handle View All / limit <= 0 scenarios
	isViewAll := globalIsViewAll
	if v, ok := raw["isViewAll"].(bool); ok && v {
		isViewAll = true
	} else if v, ok := inboxObj["isViewAll"].(bool); ok && v {
		isViewAll = true
	}

	if limitVal, hasLimit := cleanObj["limit"]; hasLimit {
		if num, ok := parseAsInt64(limitVal); ok && num <= 0 {
			cleanObj["limit"] = 300
		}
	} else if isViewAll {
		cleanObj["limit"] = 300
	} else {
		cleanObj["limit"] = 10
	}

	return map[string]interface{}{
		"RequestInfo": reqInfo,
		"inbox":       cleanObj,
	}
}

// extractModuleNameFromPayload extracts the moduleName string from a formatted inbox payload.
func extractModuleNameFromPayload(payload interface{}) string {
	rawMap, ok := payload.(map[string]interface{})
	if !ok {
		return "UNKNOWN"
	}
	if inboxMap, ok := rawMap["inbox"].(map[string]interface{}); ok {
		if psc, ok := inboxMap["processSearchCriteria"].(map[string]interface{}); ok {
			if mod, ok := psc["moduleName"].(string); ok && mod != "" {
				return mod
			}
		}
		if mod, ok := inboxMap["moduleName"].(string); ok && mod != "" {
			return mod
		}
	}
	if psc, ok := rawMap["processSearchCriteria"].(map[string]interface{}); ok {
		if mod, ok := psc["moduleName"].(string); ok && mod != "" {
			return mod
		}
	}
	if mod, ok := rawMap["moduleName"].(string); ok && mod != "" {
		return mod
	}
	return "UNKNOWN"
}

// mapServiceToInboxCriteria maps business service / module identifiers to Inbox moduleName and businessServices list.
func mapServiceToInboxCriteria(service string) (string, []string) {
	s := strings.ToUpper(strings.TrimSpace(service))
	switch s {
	case "PT":
		return "PT", []string{"PT.CREATE", "PT.MUTATION", "PT.UPDATE"}
	case "PTR", "PETSERVICES", "PET-SERVICES":
		return "pet-services", []string{"ptr"}
	case "TL":
		return "TL", []string{"TL.CREATE"}
	case "WATER", "WS", "WS-SERVICES":
		return "ws-services", []string{"NewWS1"}
	case "SEWERAGE", "SW", "SW-SERVICES":
		return "sw-services", []string{"NewSW1"}
	case "BPA":
		return "BPA", []string{"BPA"}
	case "CHB", "CHB-SERVICES":
		return "chb-services", []string{"CHB"}
	case "CND", "CND-SERVICES":
		return "cnd-services", []string{"CND"}
	case "SV", "SV-SERVICES":
		return "sv-services", []string{"SV"}
	default:
		return service, []string{service}
	}
}

// parseAsInt64 safely converts numeric or string interface values to int64.
func parseAsInt64(val interface{}) (int64, bool) {
	switch v := val.(type) {
	case float64:
		return int64(v), true
	case int64:
		return v, true
	case int:
		return int64(v), true
	case json.Number:
		if i, err := v.Int64(); err == nil {
			return i, true
		}
		if f, err := v.Float64(); err == nil {
			return int64(f), true
		}
	case string:
		if i, err := strconv.ParseInt(v, 10, 64); err == nil {
			return i, true
		}
		if f, err := strconv.ParseFloat(v, 64); err == nil {
			return int64(f), true
		}
	}
	return 0, false
}

// isJSONRawMessageNotEmpty checks if raw JSON contains meaningful data (not empty, nil, null, {}, or []).
func isJSONRawMessageNotEmpty(raw json.RawMessage) bool {
	if len(raw) == 0 {
		return false
	}
	s := strings.TrimSpace(string(raw))
	return s != "" && s != "null" && s != "{}" && s != "[]"
}

