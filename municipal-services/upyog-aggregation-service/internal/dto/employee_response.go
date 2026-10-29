package dto

import "encoding/json"

// EmployeeAggregateResponse is the composite response payload returned by POST /api/v1/employee/aggregate.
type EmployeeAggregateResponse struct {
	// Success indicates whether the overall request processing succeeded.
	Success bool `json:"success"`
	// RequestID echoes the client or trace request ID.
	RequestID string `json:"requestId,omitempty"`
	// ResponseInfo contains standard eGov response info if available.
	ResponseInfo json.RawMessage `json:"ResponseInfo,omitempty"`
	// EmployeeDashboard contains the raw/parsed payload from Employee Dashboard service.
	EmployeeDashboard json.RawMessage `json:"employeeDashboard,omitempty"`
	// Inbox contains the raw/parsed payload from Inbox service.
	Inbox json.RawMessage `json:"inbox,omitempty"`
	// Workflow contains the raw/parsed payload from Workflow service.
	Workflow json.RawMessage `json:"workflow,omitempty"`
	// Errors contains error details for any individual service that failed or timed out.
	Errors map[string]string `json:"errors,omitempty"`
}
