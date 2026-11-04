package api

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"go.uber.org/zap"

	"github.com/upyog/upyog-aggregation-service/internal/aggregation/engine"
	"github.com/upyog/upyog-aggregation-service/internal/dto"
	apperrors "github.com/upyog/upyog-aggregation-service/internal/errors"
	"github.com/upyog/upyog-aggregation-service/internal/tracing"
	"github.com/upyog/upyog-aggregation-service/internal/validator"
	"github.com/upyog/upyog-aggregation-service/pkg/logger"
)

// EmployeeAggregateHandler handles the POST /api/v1/employee/aggregate endpoint.
type EmployeeAggregateHandler struct {
	engine *engine.EmployeeEngine
	log    *logger.Logger
}

// NewEmployeeAggregateHandler creates a new EmployeeAggregateHandler.
func NewEmployeeAggregateHandler(e *engine.EmployeeEngine, log *logger.Logger) *EmployeeAggregateHandler {
	return &EmployeeAggregateHandler{
		engine: e,
		log:    log,
	}
}

// Handle processes an employee aggregate request by binding JSON, validating,
// dispatching to EmployeeEngine, and returning the aggregated payload.
func (h *EmployeeAggregateHandler) Handle(c *gin.Context) {
	ctx := c.Request.Context()

	var req dto.EmployeeAggregateRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		h.log.WithContext(ctx).Warn("failed to bind employee aggregate request", zap.Error(err))
		c.JSON(http.StatusBadRequest, dto.ErrorResponseBody{
			Success:       false,
			Code:          string(apperrors.CodeBadRequest),
			Message:       "invalid request body: " + err.Error(),
			TraceID:       tracing.TraceIDFromContext(ctx),
			CorrelationID: logger.CorrelationID(ctx),
		})
		return
	}

	if validationErr := validator.ValidateEmployeeAggregateRequest(&req); validationErr != nil {
		c.JSON(validationErr.HTTPStatus, dto.ErrorResponseBody{
			Success:       false,
			Code:          string(validationErr.Code),
			Message:       validationErr.Message,
			TraceID:       tracing.TraceIDFromContext(ctx),
			CorrelationID: logger.CorrelationID(ctx),
		})
		return
	}

	resp := h.engine.AggregateEmployee(ctx, req)
	c.JSON(http.StatusOK, resp)
}
