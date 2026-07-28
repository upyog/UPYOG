// Package validator provides request validation for the aggregation service
// using the go-playground/validator library integrated with Gin.
package validator

import (
	"fmt"
	"strings"

	"github.com/gin-gonic/gin/binding"
	"github.com/go-playground/validator/v10"

	"github.com/upyog/upyog-aggregation-service/internal/dto"
	apperrors "github.com/upyog/upyog-aggregation-service/internal/errors"
)

// Setup registers custom validators with Gin's binding engine.
func Setup() {
	if v, ok := binding.Validator.Engine().(*validator.Validate); ok {
		_ = v.RegisterValidation("provider_name", validateProviderName)
	}
}

// validateProviderName ensures the provider name contains only lowercase letters,
// digits, and hyphens.
func validateProviderName(fl validator.FieldLevel) bool {
	name := fl.Field().String()
	if name == "" {
		return false
	}
	for _, c := range name {
		if !((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '-') {
			return false
		}
	}
	return true
}

// ValidateAggregateRequest performs deep validation on the aggregate request
// beyond what struct tags can express.
func ValidateAggregateRequest(req *dto.AggregateRequest) *apperrors.AppError {
	if req == nil {
		return apperrors.NewValidation("request body is required")
	}

	if len(req.Requests) == 0 {
		return apperrors.NewValidation("at least one provider request is required")
	}

	if len(req.Requests) > 50 {
		return apperrors.NewValidation("maximum 50 provider requests allowed per aggregation call")
	}

	// Check for duplicate providers.
	seen := make(map[string]struct{}, len(req.Requests))
	for _, pr := range req.Requests {
		if _, exists := seen[pr.Provider]; exists {
			return apperrors.NewValidation(
				fmt.Sprintf("duplicate provider '%s' in request", pr.Provider),
			)
		}
		seen[pr.Provider] = struct{}{}
	}

	// Validate pagination.
	for _, pr := range req.Requests {
		if pr.Pagination != nil && pr.Pagination.Size > 100 {
			return apperrors.NewValidation(
				fmt.Sprintf("provider '%s': pagination size must be <= 100", pr.Provider),
			)
		}
		if pr.Sort != nil {
			order := strings.ToUpper(pr.Sort.Order)
			if order != "ASC" && order != "DESC" {
				return apperrors.NewValidation(
					fmt.Sprintf("provider '%s': sort order must be ASC or DESC", pr.Provider),
				)
			}
		}
	}

	return nil
}
