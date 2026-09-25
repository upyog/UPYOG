.PHONY: all build test lint clean docker swagger run dev

# Variables
APP_NAME := upyog-aggregation-service
VERSION := $(shell git describe --tags --always --dirty 2>/dev/null || echo "dev")
BUILD_TIME := $(shell date -u '+%Y-%m-%dT%H:%M:%SZ')
GO := go
GOFLAGS := -v
LDFLAGS := -ldflags "-X main.version=$(VERSION) -X main.buildTime=$(BUILD_TIME)"
DOCKER_IMAGE := upyog/$(APP_NAME)
DOCKER_TAG := $(VERSION)

# Default target
all: lint test build

# Build the binary
build:
	$(GO) build $(GOFLAGS) $(LDFLAGS) -o bin/$(APP_NAME) .

# Run locally
run: build
	APP_ENV=local ./bin/$(APP_NAME)

# Run with hot reload (requires air: go install github.com/air-verse/air@latest)
dev:
	APP_ENV=local air

# Run tests
test:
	$(GO) test ./... -v -count=1 -race -coverprofile=coverage.out

# Run tests with coverage report
test-cover: test
	$(GO) tool cover -html=coverage.out -o coverage.html
	@echo "Coverage report: coverage.html"

# Run benchmarks
bench:
	$(GO) test ./... -bench=. -benchmem -run=^$$ -count=3

# Run linter
lint:
	golangci-lint run ./...

# Format code
fmt:
	$(GO) fmt ./...
	goimports -w .

# Vet code
vet:
	$(GO) vet ./...

# Generate Swagger docs (requires swag: go install github.com/swaggo/swag/cmd/swag@latest)
swagger:
	swag init -g main.go -o docs/swagger --parseDependency --parseInternal

# Tidy dependencies
tidy:
	$(GO) mod tidy

# Download dependencies
deps:
	$(GO) mod download

# Docker build
docker:
	docker build -t $(DOCKER_IMAGE):$(DOCKER_TAG) .
	docker tag $(DOCKER_IMAGE):$(DOCKER_TAG) $(DOCKER_IMAGE):latest

# Docker Compose up
compose-up:
	docker-compose -f deployments/docker-compose.yml up -d

# Docker Compose down
compose-down:
	docker-compose -f deployments/docker-compose.yml down

# Clean build artifacts
clean:
	rm -rf bin/ coverage.out coverage.html tmp/

# Security audit
audit:
	$(GO) list -json -deps ./... | docker run --rm -i sonatypecommunity/nancy:latest sleuth

# Run integration tests
test-integration:
	$(GO) test ./test/integration/... -v -tags=integration -count=1

# Run load tests (requires k6: brew install k6)
test-load:
	k6 run test/loadtest/aggregate_load_test.js

# Help
help:
	@echo "Available targets:"
	@echo "  all              - Lint, test, and build"
	@echo "  build            - Build the binary"
	@echo "  run              - Build and run locally"
	@echo "  dev              - Run with hot reload (requires air)"
	@echo "  test             - Run unit tests with race detector"
	@echo "  test-cover       - Run tests and generate coverage report"
	@echo "  bench            - Run benchmarks"
	@echo "  lint             - Run golangci-lint"
	@echo "  fmt              - Format code"
	@echo "  swagger          - Generate Swagger documentation"
	@echo "  docker           - Build Docker image"
	@echo "  compose-up       - Start Docker Compose stack"
	@echo "  compose-down     - Stop Docker Compose stack"
	@echo "  clean            - Remove build artifacts"
	@echo "  test-integration - Run integration tests"
	@echo "  test-load        - Run load tests with k6"
