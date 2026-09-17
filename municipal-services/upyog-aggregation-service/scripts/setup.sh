#!/usr/bin/env bash
# =============================================================================
# Local development setup script for UPYOG Aggregation Service
# =============================================================================
set -euo pipefail

echo "=== UPYOG Aggregation Service - Local Setup ==="

# Check Go version.
required_go="1.24"
if command -v go &>/dev/null; then
    go_version=$(go version | grep -oP 'go([0-9]+\.[0-9]+)' | head -1 | sed 's/go//')
    echo "✓ Go ${go_version} found"
else
    echo "✗ Go is not installed. Please install Go ${required_go}+"
    exit 1
fi

# Install development tools.
echo ""
echo "Installing development tools..."

echo "  → golangci-lint"
go install github.com/golangci/golangci-lint/cmd/golangci-lint@latest 2>/dev/null || true

echo "  → swag (Swagger generator)"
go install github.com/swaggo/swag/cmd/swag@latest 2>/dev/null || true

echo "  → air (hot reload)"
go install github.com/air-verse/air@latest 2>/dev/null || true

# Download dependencies.
echo ""
echo "Downloading Go dependencies..."
go mod download
go mod tidy

echo ""
echo "=== Setup Complete ==="
echo ""
echo "Quick start:"
echo "  make run          - Build and run locally"
echo "  make dev          - Run with hot reload"
echo "  make test         - Run tests"
echo "  make compose-up   - Start Docker Compose stack"
echo ""
