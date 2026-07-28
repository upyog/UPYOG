// Package registry provides a thread-safe, map-backed registry for
// DataProvider instances. Providers are registered by name and resolved
// at request time — no switch-case dispatch is used.
package registry

import (
	"sort"
	"sync"

	apperrors "github.com/upyog/upyog-aggregation-service/internal/errors"
	"github.com/upyog/upyog-aggregation-service/internal/providers"
)

// Registry stores DataProvider implementations keyed by their Name().
// All methods are safe for concurrent access.
type Registry struct {
	providers sync.Map
}

// NewRegistry creates an empty provider registry.
func NewRegistry() *Registry {
	return &Registry{}
}

// Register adds a provider to the registry. If a provider with the same
// name already exists it is silently replaced.
func (r *Registry) Register(provider providers.DataProvider) {
	r.providers.Store(provider.Name(), provider)
}

// Resolve looks up a provider by name. It returns errors.NewProviderNotFound
// when the requested name has not been registered.
func (r *Registry) Resolve(name string) (providers.DataProvider, error) {
	v, ok := r.providers.Load(name)
	if !ok {
		return nil, apperrors.NewProviderNotFound(name)
	}
	return v.(providers.DataProvider), nil
}

// List returns the sorted names of all registered providers.
func (r *Registry) List() []string {
	var names []string
	r.providers.Range(func(key, _ any) bool {
		names = append(names, key.(string))
		return true
	})
	sort.Strings(names)
	return names
}
