// Package auth provides authentication and authorization primitives for the
// aggregation service. It includes JWT parsing compatible with Keycloak tokens
// and role-based access control.
package auth

import (
	"encoding/base64"
	"encoding/json"
	"strings"
	"time"

	"github.com/upyog/upyog-aggregation-service/internal/errors"
)

// Claims represents the JWT claims payload from a Keycloak-issued token.
type Claims struct {
	// Sub is the subject (user ID) of the token.
	Sub string `json:"sub"`
	// Iss is the token issuer URL.
	Iss string `json:"iss"`
	// Aud is the intended audience(s) for the token.
	Aud []string `json:"aud"`
	// Exp is the expiration time as a Unix timestamp.
	Exp int64 `json:"exp"`
	// Iat is the issued-at time as a Unix timestamp.
	Iat int64 `json:"iat"`
	// PreferredUsername is the user's preferred display name.
	PreferredUsername string `json:"preferred_username"`
	// Email is the user's email address.
	Email string `json:"email"`
	// Name is the user's full name.
	Name string `json:"name"`
	// RealmAccess contains the realm-level roles granted to the user.
	RealmAccess struct {
		Roles []string `json:"roles"`
	} `json:"realm_access"`
	// TenantId is the UPYOG tenant identifier embedded in the token.
	TenantId string `json:"tenantId"`
}

// JWTValidator validates JWT tokens by parsing the payload and checking claims.
// It does not verify cryptographic signatures — that responsibility belongs to
// the API gateway or a dedicated JWKS-based middleware.
type JWTValidator struct {
	issuer       string
	audience     string
	leewaySeconds int
}

// NewJWTValidator creates a JWTValidator with the specified issuer, audience,
// and clock-skew leeway (in seconds). Pass empty strings for issuer/audience
// to skip those validations.
func NewJWTValidator(issuer, audience string, leeway int) *JWTValidator {
	return &JWTValidator{
		issuer:        issuer,
		audience:      audience,
		leewaySeconds: leeway,
	}
}

// ParseToken splits a JWT by ".", base64-decodes the payload segment, and
// unmarshals it into Claims. Returns an authentication error on any failure.
func (v *JWTValidator) ParseToken(tokenString string) (*Claims, error) {
	parts := strings.Split(tokenString, ".")
	if len(parts) != 3 {
		return nil, errors.NewAuthentication("malformed JWT: expected 3 segments")
	}

	payload, err := decodeSegment(parts[1])
	if err != nil {
		return nil, errors.NewAuthentication("failed to decode JWT payload: " + err.Error())
	}

	var claims Claims
	if err := json.Unmarshal(payload, &claims); err != nil {
		return nil, errors.NewAuthentication("failed to unmarshal JWT claims: " + err.Error())
	}

	return &claims, nil
}

// ValidateClaims checks that the token has not expired (accounting for leeway)
// and that the issuer and audience match the configured values, if set.
func (v *JWTValidator) ValidateClaims(claims *Claims) error {
	now := time.Now().Unix()
	if claims.Exp > 0 && now > claims.Exp+int64(v.leewaySeconds) {
		return errors.NewAuthentication("token has expired")
	}

	if v.issuer != "" && claims.Iss != v.issuer {
		return errors.NewAuthentication("invalid token issuer")
	}

	if v.audience != "" && !containsAudience(claims.Aud, v.audience) {
		return errors.NewAuthentication("invalid token audience")
	}

	return nil
}

// GetRoles extracts the realm-level roles from the claims.
func GetRoles(claims *Claims) []string {
	if claims == nil {
		return nil
	}
	return claims.RealmAccess.Roles
}

// decodeSegment decodes a base64url-encoded JWT segment, adding padding as
// required by the encoding/base64 package.
func decodeSegment(seg string) ([]byte, error) {
	// Add padding if necessary.
	switch len(seg) % 4 {
	case 2:
		seg += "=="
	case 3:
		seg += "="
	}

	return base64.URLEncoding.DecodeString(seg)
}

// containsAudience checks whether the target audience is present in the list.
func containsAudience(audiences []string, target string) bool {
	for _, a := range audiences {
		if a == target {
			return true
		}
	}
	return false
}
