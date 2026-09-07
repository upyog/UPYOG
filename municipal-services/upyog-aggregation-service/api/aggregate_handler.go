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

// AggregateHandler handles the POST /api/v1/aggregate endpoint.
type AggregateHandler struct {
	engine *engine.Engine
	log    *logger.Logger
}

// NewAggregateHandler creates a new AggregateHandler with the given engine
// and logger.
func NewAggregateHandler(e *engine.Engine, log *logger.Logger) *AggregateHandler {
	return &AggregateHandler{
		engine: e,
		log:    log,
	}
}

// Handle processes an aggregate request by binding the JSON payload, validating
// it, dispatching to the aggregation engine, and returning the combined result.
//
//	@Summary     Aggregate data from multiple providers
//	@Description Invokes registered data providers concurrently and returns combined results
//	@Tags        Aggregation
//	@Accept      json
//	@Produce     json
//	@Param       request body dto.AggregateRequest true "Aggregation request"
//	@Success     200 {object} dto.AggregateResponse
//	@Failure     400 {object} dto.ErrorResponseBody
//	@Failure     500 {object} dto.ErrorResponseBody
//	@Router      /api/v1/aggregate [post]
func (h *AggregateHandler) Handle(c *gin.Context) {
	ctx := c.Request.Context()

	var req dto.AggregateRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		h.log.WithContext(ctx).Warn("failed to bind aggregate request", zap.Error(err))
		c.JSON(http.StatusBadRequest, dto.ErrorResponseBody{
			Success:       false,
			Code:          string(apperrors.CodeBadRequest),
			Message:       "invalid request body: " + err.Error(),
			TraceID:       tracing.TraceIDFromContext(ctx),
			CorrelationID: logger.CorrelationID(ctx),
		})
		return
	}

	// Deep validation beyond struct tags.
	if validationErr := validator.ValidateAggregateRequest(&req); validationErr != nil {
		c.JSON(validationErr.HTTPStatus, dto.ErrorResponseBody{
			Success:       false,
			Code:          string(validationErr.Code),
			Message:       validationErr.Message,
			TraceID:       tracing.TraceIDFromContext(ctx),
			CorrelationID: logger.CorrelationID(ctx),
		})
		return
	}

	resp := h.engine.Aggregate(ctx, req)
	c.JSON(http.StatusOK, resp)
}
