package auth

import (
	"context"

	"github.com/upyog/upyog-aggregation-service/internal/common"
	"github.com/upyog/upyog-aggregation-service/internal/errors"
)

// Authorizer performs role-based access control using roles stored in the
// request context. It is stateless and safe for concurrent use.
type Authorizer struct{}

// NewAuthorizer creates a new Authorizer instance.
func NewAuthorizer() *Authorizer {
	return &Authorizer{}
}

// Authorize checks whether the user (identified by roles in the context)
// holds at least one of the required roles. Returns an authorization error
// if no matching role is found.
func (a *Authorizer) Authorize(ctx context.Context, requiredRoles ...string) error {
	if len(requiredRoles) == 0 {
		return nil
	}

	userRoles := common.UserRoles(ctx)
	if len(userRoles) == 0 {
		return errors.NewAuthorization("no roles assigned to user")
	}

	roleSet := make(map[string]struct{}, len(userRoles))
	for _, r := range userRoles {
		roleSet[r] = struct{}{}
	}

	for _, required := range requiredRoles {
		if _, ok := roleSet[required]; ok {
			return nil
		}
	}

	return errors.NewAuthorization("insufficient permissions: none of the required roles found")
}

// HasRole returns true if the user in the given context holds the specified role.
func (a *Authorizer) HasRole(ctx context.Context, role string) bool {
	for _, r := range common.UserRoles(ctx) {
		if r == role {
			return true
		}
	}
	return false
}
