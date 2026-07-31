package unit

import (
	"testing"

	"github.com/upyog/upyog-aggregation-service/internal/dto"
	"github.com/upyog/upyog-aggregation-service/internal/validator"
)

func TestValidateAggregateRequest_Valid(t *testing.T) {
	req := &dto.AggregateRequest{
		RequestID: "f58c5b1d-2d53-4f8d-9cb3-1f5d6f0c2b45",
		Page:      "citizen-home",
		TenantID:  "pb.amritsar",
		Locale:    "en_IN",
		Requests: []dto.ProviderRequest{
			{Provider: "quick-summary"},
			{Provider: "notifications", Pagination: &dto.Pagination{Page: 0, Size: 5}},
		},
	}

	err := validator.ValidateAggregateRequest(req)
	if err != nil {
		t.Errorf("expected no error, got: %v", err)
	}
}

func TestValidateAggregateRequest_NilRequest(t *testing.T) {
	err := validator.ValidateAggregateRequest(nil)
	if err == nil {
		t.Error("expected error for nil request")
	}
}

func TestValidateAggregateRequest_EmptyProviders(t *testing.T) {
	req := &dto.AggregateRequest{
		RequestID: "f58c5b1d-2d53-4f8d-9cb3-1f5d6f0c2b45",
		Page:      "citizen-home",
		TenantID:  "pb.amritsar",
		Requests:  []dto.ProviderRequest{},
	}

	err := validator.ValidateAggregateRequest(req)
	if err == nil {
		t.Error("expected error for empty providers")
	}
}

func TestValidateAggregateRequest_DuplicateProviders(t *testing.T) {
	req := &dto.AggregateRequest{
		RequestID: "f58c5b1d-2d53-4f8d-9cb3-1f5d6f0c2b45",
		Page:      "citizen-home",
		TenantID:  "pb.amritsar",
		Requests: []dto.ProviderRequest{
			{Provider: "quick-summary"},
			{Provider: "quick-summary"},
		},
	}

	err := validator.ValidateAggregateRequest(req)
	if err == nil {
		t.Error("expected error for duplicate providers")
	}
}

func TestValidateAggregateRequest_TooManyProviders(t *testing.T) {
	requests := make([]dto.ProviderRequest, 51)
	for i := range requests {
		requests[i] = dto.ProviderRequest{Provider: "provider-" + string(rune('a'+i%26)) + string(rune('a'+i/26))}
	}

	req := &dto.AggregateRequest{
		RequestID: "f58c5b1d-2d53-4f8d-9cb3-1f5d6f0c2b45",
		Page:      "citizen-home",
		TenantID:  "pb.amritsar",
		Requests:  requests,
	}

	err := validator.ValidateAggregateRequest(req)
	if err == nil {
		t.Error("expected error for too many providers")
	}
}

func TestValidateAggregateRequest_InvalidPaginationSize(t *testing.T) {
	req := &dto.AggregateRequest{
		RequestID: "f58c5b1d-2d53-4f8d-9cb3-1f5d6f0c2b45",
		Page:      "citizen-home",
		TenantID:  "pb.amritsar",
		Requests: []dto.ProviderRequest{
			{
				Provider:   "notifications",
				Pagination: &dto.Pagination{Page: 0, Size: 200},
			},
		},
	}

	err := validator.ValidateAggregateRequest(req)
	if err == nil {
		t.Error("expected error for pagination size > 100")
	}
}

func TestValidateAggregateRequest_InvalidSortOrder(t *testing.T) {
	req := &dto.AggregateRequest{
		RequestID: "f58c5b1d-2d53-4f8d-9cb3-1f5d6f0c2b45",
		Page:      "citizen-home",
		TenantID:  "pb.amritsar",
		Requests: []dto.ProviderRequest{
			{
				Provider: "recent-applications",
				Sort:     &dto.Sort{Field: "createdTime", Order: "INVALID"},
			},
		},
	}

	err := validator.ValidateAggregateRequest(req)
	if err == nil {
		t.Error("expected error for invalid sort order")
	}
}
