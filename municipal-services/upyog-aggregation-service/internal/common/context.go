package common

import "context"

// contextKey is an unexported type for context keys to prevent collisions.
type contextKey string

const (
	ctxKeyLocale    contextKey = "locale"
	ctxKeyAuthToken contextKey = "authToken"
	ctxKeyUserRoles contextKey = "userRoles"
	ctxKeyUserID    contextKey = "userId"
	ctxKeyUserType  contextKey = "userType"
)

// WithLocale stores the locale in the context.
func WithLocale(ctx context.Context, locale string) context.Context {
	return context.WithValue(ctx, ctxKeyLocale, locale)
}

// Locale extracts the locale from the context.
func Locale(ctx context.Context) string {
	if v, ok := ctx.Value(ctxKeyLocale).(string); ok {
		return v
	}
	return "en_IN"
}

// WithAuthToken stores the raw bearer token in the context.
func WithAuthToken(ctx context.Context, token string) context.Context {
	return context.WithValue(ctx, ctxKeyAuthToken, token)
}

// AuthToken extracts the bearer token from the context.
func AuthToken(ctx context.Context) string {
	if v, ok := ctx.Value(ctxKeyAuthToken).(string); ok {
		return v
	}
	return ""
}

// WithUserRoles stores the user's roles in the context.
func WithUserRoles(ctx context.Context, roles []string) context.Context {
	return context.WithValue(ctx, ctxKeyUserRoles, roles)
}

// UserRoles extracts the user's roles from the context.
func UserRoles(ctx context.Context) []string {
	if v, ok := ctx.Value(ctxKeyUserRoles).([]string); ok {
		return v
	}
	return nil
}

// WithUserID stores the user ID in the context.
func WithUserID(ctx context.Context, id string) context.Context {
	return context.WithValue(ctx, ctxKeyUserID, id)
}

// UserID extracts the user ID from the context.
func UserID(ctx context.Context) string {
	if v, ok := ctx.Value(ctxKeyUserID).(string); ok {
		return v
	}
	return ""
}

// WithUserType stores the user type in the context.
func WithUserType(ctx context.Context, userType string) context.Context {
	return context.WithValue(ctx, ctxKeyUserType, userType)
}

// UserType extracts the user type from the context.
func UserType(ctx context.Context) string {
	if v, ok := ctx.Value(ctxKeyUserType).(string); ok {
		return v
	}
	return ""
}
