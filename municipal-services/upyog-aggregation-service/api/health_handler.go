package api

import (
	"context"
	"net/http"
	"time"

	"github.com/gin-gonic/gin"

	"github.com/upyog/upyog-aggregation-service/internal/cache"
	"github.com/upyog/upyog-aggregation-service/internal/dto"
)

const serviceName = "upyog-aggregation-service"

// HealthHandler provides health, readiness, and liveness probe endpoints
// for Kubernetes and monitoring systems.
type HealthHandler struct {
	cache   *cache.Cache
	version string
}

// NewHealthHandler creates a new HealthHandler with the given cache client
// and service version string.
func NewHealthHandler(cache *cache.Cache, version string) *HealthHandler {
	return &HealthHandler{
		cache:   cache,
		version: version,
	}
}

// Health returns the overall health status of the service.
//
//	@Summary     Health check
//	@Description Returns the health status of the aggregation service
//	@Tags        Health
//	@Produce     json
//	@Success     200 {object} dto.HealthResponse
//	@Router      /health [get]
func (h *HealthHandler) Health(c *gin.Context) {
	c.JSON(http.StatusOK, dto.HealthResponse{
		Status:  "UP",
		Service: serviceName,
		Version: h.version,
	})
}

// Readiness checks whether all downstream dependencies (e.g., Redis) are
// reachable. Returns 503 Service Unavailable if any dependency is unhealthy.
//
//	@Summary     Readiness probe
//	@Description Checks downstream dependency health (Redis)
//	@Tags        Health
//	@Produce     json
//	@Success     200 {object} dto.HealthResponse
//	@Failure     503 {object} dto.HealthResponse
//	@Router      /readiness [get]
func (h *HealthHandler) Readiness(c *gin.Context) {
	ctx, cancel := context.WithTimeout(c.Request.Context(), 3*time.Second)
	defer cancel()

	if h.cache != nil {
		if err := h.cache.Health(ctx); err != nil {
			c.JSON(http.StatusServiceUnavailable, dto.HealthResponse{
				Status:  "DOWN",
				Service: serviceName,
				Version: h.version,
				Details: map[string]string{
					"redis": err.Error(),
				},
			})
			return
		}
	}

	c.JSON(http.StatusOK, dto.HealthResponse{
		Status:  "UP",
		Service: serviceName,
		Version: h.version,
	})
}

// Liveness returns a simple 200 OK indicating the process is alive. This
// endpoint should remain lightweight — it must never call external services.
//
//	@Summary     Liveness probe
//	@Description Returns 200 if the service process is alive
//	@Tags        Health
//	@Produce     json
//	@Success     200 {object} dto.HealthResponse
//	@Router      /liveness [get]
func (h *HealthHandler) Liveness(c *gin.Context) {
	c.JSON(http.StatusOK, dto.HealthResponse{
		Status:  "UP",
		Service: serviceName,
		Version: h.version,
	})
}
