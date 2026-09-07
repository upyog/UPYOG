package mocks

import (
	"context"

	"github.com/upyog/upyog-aggregation-service/internal/dto"
)

// MockProvider is a test double for the DataProvider interface.
type MockProvider struct {
	ProviderName string
	Response     *dto.ProviderResponse
	Err          error
	ExecuteFn    func(ctx context.Context, req dto.ProviderRequest, aggReq dto.AggregateRequest) (*dto.ProviderResponse, error)
}

// Name returns the mock provider name.
func (m *MockProvider) Name() string {
	return m.ProviderName
}

// Execute calls the mock function if set, otherwise returns the configured response/error.
func (m *MockProvider) Execute(ctx context.Context, req dto.ProviderRequest, aggReq dto.AggregateRequest) (*dto.ProviderResponse, error) {
	if m.ExecuteFn != nil {
		return m.ExecuteFn(ctx, req, aggReq)
	}
	return m.Response, m.Err
}

// NewSuccessProvider creates a mock provider that always returns success.
func NewSuccessProvider(name string, data interface{}) *MockProvider {
	return &MockProvider{
		ProviderName: name,
		Response: &dto.ProviderResponse{
			Status:        "SUCCESS",
			ExecutionTime: 10,
			Data:          data,
		},
	}
}

// NewFailureProvider creates a mock provider that always returns an error.
func NewFailureProvider(name string, errMsg string) *MockProvider {
	return &MockProvider{
		ProviderName: name,
		Response: &dto.ProviderResponse{
			Status:    "FAILED",
			ErrorCode: "MOCK_ERROR",
			Message:   errMsg,
		},
	}
}

// NewSlowProvider creates a mock provider that blocks until context is cancelled.
func NewSlowProvider(name string) *MockProvider {
	return &MockProvider{
		ProviderName: name,
		ExecuteFn: func(ctx context.Context, req dto.ProviderRequest, aggReq dto.AggregateRequest) (*dto.ProviderResponse, error) {
			<-ctx.Done()
			return &dto.ProviderResponse{
				Status:  "TIMEOUT",
				Message: "provider timed out",
			}, ctx.Err()
		},
	}
}
