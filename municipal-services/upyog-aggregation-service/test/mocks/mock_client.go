package mocks

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"sync"
)

// MockBackendClient is a test double for the HTTP client that simulates backend service responses.
type MockBackendClient struct {
	mu        sync.RWMutex
	responses map[string]*MockResponse
	calls     map[string]int
}

// MockResponse defines a canned response for a specific path.
type MockResponse struct {
	StatusCode int
	Body       interface{}
	Err        error
}

// Response mirrors the clients.Response struct for test compatibility.
type Response struct {
	StatusCode int
	Body       []byte
	Headers    http.Header
}

// NewMockBackendClient creates a new mock backend client.
func NewMockBackendClient() *MockBackendClient {
	return &MockBackendClient{
		responses: make(map[string]*MockResponse),
		calls:     make(map[string]int),
	}
}

// Register configures a canned response for a given method+path combination.
func (m *MockBackendClient) Register(method, path string, resp *MockResponse) {
	m.mu.Lock()
	defer m.mu.Unlock()
	key := method + " " + path
	m.responses[key] = resp
}

// Get simulates a GET request to the mock backend.
func (m *MockBackendClient) Get(_ context.Context, path string, _ map[string]string) (*Response, error) {
	return m.do("GET", path)
}

// Post simulates a POST request to the mock backend.
func (m *MockBackendClient) Post(_ context.Context, path string, _ interface{}, _ map[string]string) (*Response, error) {
	return m.do("POST", path)
}

func (m *MockBackendClient) do(method, path string) (*Response, error) {
	m.mu.Lock()
	key := method + " " + path
	m.calls[key]++
	m.mu.Unlock()

	m.mu.RLock()
	resp, ok := m.responses[key]
	m.mu.RUnlock()

	if !ok {
		return nil, fmt.Errorf("no mock response registered for %s", key)
	}
	if resp.Err != nil {
		return nil, resp.Err
	}

	body, err := json.Marshal(resp.Body)
	if err != nil {
		return nil, fmt.Errorf("failed to marshal mock response: %w", err)
	}

	return &Response{
		StatusCode: resp.StatusCode,
		Body:       body,
		Headers:    make(http.Header),
	}, nil
}

// CallCount returns the number of times a method+path was called.
func (m *MockBackendClient) CallCount(method, path string) int {
	m.mu.RLock()
	defer m.mu.RUnlock()
	return m.calls[method+" "+path]
}

// Reset clears all registered responses and call counts.
func (m *MockBackendClient) Reset() {
	m.mu.Lock()
	defer m.mu.Unlock()
	m.responses = make(map[string]*MockResponse)
	m.calls = make(map[string]int)
}
