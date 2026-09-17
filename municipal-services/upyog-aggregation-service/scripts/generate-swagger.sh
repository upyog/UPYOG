#!/usr/bin/env bash
# =============================================================================
# Generate Swagger documentation
# =============================================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "${SCRIPT_DIR}")"

cd "${PROJECT_DIR}"

echo "Generating Swagger documentation..."

if ! command -v swag &>/dev/null; then
    echo "Installing swag..."
    go install github.com/swaggo/swag/cmd/swag@latest
fi

swag init \
    -g cmd/server/main.go \
    -o docs/swagger \
    --parseDependency \
    --parseInternal

echo "✓ Swagger docs generated in docs/swagger/"
