// Package mapper provides DTO mapping utilities for transforming data between
// API responses and internal domain types. It includes generic helpers, safe
// map accessors, and a JSON-based deep-copy mapper.
package mapper

import (
	"encoding/json"
	"fmt"
)

// MapResponse performs a deep copy from source to target by JSON
// marshalling/unmarshalling. Both source and target must be JSON-serializable.
// This is intentionally simple; for hot paths consider hand-written mappers.
func MapResponse(source, target interface{}) error {
	data, err := json.Marshal(source)
	if err != nil {
		return fmt.Errorf("mapper: marshal source: %w", err)
	}

	if err := json.Unmarshal(data, target); err != nil {
		return fmt.Errorf("mapper: unmarshal into target: %w", err)
	}

	return nil
}

// MapSlice transforms a slice of T into a slice of R by applying fn to each element.
func MapSlice[T any, R any](source []T, fn func(T) R) []R {
	if source == nil {
		return nil
	}

	result := make([]R, len(source))
	for i, v := range source {
		result[i] = fn(v)
	}
	return result
}

// SafeString returns the string value for key from m, or "" if the key is
// missing or not a string.
func SafeString(m map[string]interface{}, key string) string {
	v, ok := m[key]
	if !ok {
		return ""
	}

	s, ok := v.(string)
	if !ok {
		return ""
	}
	return s
}

// SafeInt64 returns the int64 value for key from m. JSON numbers are decoded as
// float64 by default, so this handles the float64 → int64 conversion.
func SafeInt64(m map[string]interface{}, key string) int64 {
	v, ok := m[key]
	if !ok {
		return 0
	}

	switch n := v.(type) {
	case float64:
		return int64(n)
	case int64:
		return n
	case json.Number:
		i, err := n.Int64()
		if err != nil {
			return 0
		}
		return i
	default:
		return 0
	}
}

// SafeFloat64 returns the float64 value for key from m.
func SafeFloat64(m map[string]interface{}, key string) float64 {
	v, ok := m[key]
	if !ok {
		return 0
	}

	switch n := v.(type) {
	case float64:
		return n
	case int64:
		return float64(n)
	case json.Number:
		f, err := n.Float64()
		if err != nil {
			return 0
		}
		return f
	default:
		return 0
	}
}

// SafeBool returns the bool value for key from m, or false if missing/wrong type.
func SafeBool(m map[string]interface{}, key string) bool {
	v, ok := m[key]
	if !ok {
		return false
	}

	b, ok := v.(bool)
	if !ok {
		return false
	}
	return b
}

// SafeStringSlice returns the []string value for key from m. It handles both
// []string and []interface{} (common when decoded from JSON).
func SafeStringSlice(m map[string]interface{}, key string) []string {
	v, ok := m[key]
	if !ok {
		return nil
	}

	switch s := v.(type) {
	case []string:
		return s
	case []interface{}:
		result := make([]string, 0, len(s))
		for _, item := range s {
			if str, ok := item.(string); ok {
				result = append(result, str)
			}
		}
		return result
	default:
		return nil
	}
}

// ToStringMap converts an arbitrary value to map[string]interface{} via JSON
// round-trip. Returns an error if the value cannot be represented as a map.
func ToStringMap(v interface{}) (map[string]interface{}, error) {
	data, err := json.Marshal(v)
	if err != nil {
		return nil, fmt.Errorf("mapper: marshal to map: %w", err)
	}

	var result map[string]interface{}
	if err := json.Unmarshal(data, &result); err != nil {
		return nil, fmt.Errorf("mapper: unmarshal to map: %w", err)
	}

	return result, nil
}
