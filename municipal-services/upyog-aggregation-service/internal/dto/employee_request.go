package dto

import "encoding/json"

// EmployeeAggregateRequest is the request payload for POST /api/v1/employee/aggregate
type EmployeeAggregateRequest struct {
	// RequestInfo is the standard eGov request info block passed through to backend services.
	RequestInfo json.RawMessage `json:"RequestInfo" binding:"required"`
	// RequestID is a client-provided unique identifier for the request.
	RequestID string `json:"requestId,omitempty"`
	// TenantID identifies the ULB/tenant for multi-tenant data isolation.
	TenantID string `json:"tenantId" binding:"required"`
	// Locale specifies the desired response locale (e.g., "en_IN").
	Locale string `json:"locale,omitempty"`
	// Services specifies the list of business services to query (e.g. ["PT", "TL", "PTR"]).
	Services []string `json:"services,omitempty"`
	// Inbox specifies custom search criteria for the Inbox service call.
	Inbox json.RawMessage `json:"inbox,omitempty"`
	// Workflow specifies custom search criteria for the Workflow service call.
	Workflow json.RawMessage `json:"workflow,omitempty"`
	// EmployeeDashboard specifies custom parameters for the Employee Dashboard call.
	EmployeeDashboard json.RawMessage `json:"employeeDashboard,omitempty"`
	// Limit specifies maximum items to retrieve.
	Limit *int `json:"limit,omitempty"`
	// Offset specifies pagination start index.
	Offset *int `json:"offset,omitempty"`
}
