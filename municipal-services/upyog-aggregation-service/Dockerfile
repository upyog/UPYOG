# =============================================================================
# Multi-stage Docker build for UPYOG Aggregation Service
# Stage 1: Build the Go binary
# Stage 2: Create a minimal production image
# =============================================================================

# ---- Build Stage ----
FROM golang:1.24-alpine AS builder

RUN apk add --no-cache git ca-certificates tzdata

WORKDIR /build

# Cache dependency downloads.
COPY go.mod go.sum ./
RUN go mod download

# Copy source code and build.
COPY . .
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build \
    -ldflags="-w -s -X main.version=$(git describe --tags --always --dirty 2>/dev/null || echo dev)" \
    -o /build/bin/upyog-aggregation-service \
    .

# ---- Production Stage ----
FROM alpine:3.20

RUN apk add --no-cache ca-certificates tzdata && \
    addgroup -S appgroup && \
    adduser -S appuser -G appgroup

WORKDIR /app

# Copy binary and configs.
COPY --from=builder /build/bin/upyog-aggregation-service .
COPY --from=builder /build/configs/ ./configs/

# Run as non-root user.
USER appuser

# Expose service port.
EXPOSE 8080

# Health check.
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD wget -q --spider http://localhost:8080/health || exit 1

ENTRYPOINT ["./upyog-aggregation-service"]
