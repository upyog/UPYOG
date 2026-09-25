// Package dto defines the data transfer objects for the aggregation service API.
// These structures define the contract between the API layer and consumers.
package dto

import "encoding/json"

// AggregateRequest is the top-level request body for the POST /api/v1/aggregate endpoint.
type AggregateRequest struct {
	// RequestInfo is the standard eGov request info block passed through to backend services.
	RequestInfo json.RawMessage `json:"requestInfo" binding:"required"`
	// RequestID is a client-provided unique identifier for the request.
	RequestID string `json:"requestId" binding:"required,uuid"`
	// Page identifies the logical page the client is rendering (e.g., "citizen-home").
	// This is metadata only — the service does NOT use it to decide what data to return.
	Page string `json:"page" binding:"required,min=1,max=128"`
	// TenantID identifies the ULB/tenant for multi-tenant data isolation.
	TenantID string `json:"tenantId" binding:"required,min=1,max=128"`
	// Locale specifies the desired response locale (e.g., "en_IN").
	Locale string `json:"locale,omitempty"`
	// Requests is the list of data provider requests. At least one is required.
	Requests []ProviderRequest `json:"requests" binding:"required,min=1,dive"`
}

// ProviderRequest describes a single data provider invocation within an aggregation request.
type ProviderRequest struct {
	// Provider is the registered name of the data provider to invoke.
	Provider string `json:"provider" binding:"required,min=1,max=64"`
	// Pagination controls the page number and page size for list-type providers.
	Pagination *Pagination `json:"pagination,omitempty"`
	// Sort specifies the sort field and order for list-type providers.
	Sort *Sort `json:"sort,omitempty"`
	// Filters is a free-form map of provider-specific filter criteria.
	Filters map[string]interface{} `json:"filters,omitempty"`
}

// Pagination holds pagination parameters.
type Pagination struct {
	// Page is the zero-based page number.
	Page int `json:"page" binding:"min=0"`
	// Size is the number of items per page.
	Size int `json:"size" binding:"required,min=1,max=100"`
}

// Sort holds sorting parameters.
type Sort struct {
	// Field is the name of the field to sort by.
	Field string `json:"field" binding:"required,min=1"`
	// Order is the sort direction: "ASC" or "DESC".
	Order string `json:"order" binding:"required,oneof=ASC DESC asc desc"`
}
