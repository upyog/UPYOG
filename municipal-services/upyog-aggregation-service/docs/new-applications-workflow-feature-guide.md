# UPYOG Aggregation Service

## How the Service Works & Feature Integration Guide: "New Applications from Workflow"

> **Audience:** An experienced Java solution architect (12+ years) building their first Go service.
> Every Go concept in this document is mapped to its closest Java/Spring equivalent.
>
> **Scope:** Part 1 explains how the service works end to end, file by file.
> Part 2 is a complete integration walkthrough for one real feature — **fetching all new
> applications from the workflow service (`egov-workflow-v2`)** — including every file that
> changed, the full code, why each change was made, and how the pieces interact at runtime.
> Everything in Part 2 is implemented, compiled, unit-tested, and smoke-tested against a mock
> workflow backend; the request/response samples shown are real captured output.

---

# Part 1 — How the Service Works

## 1.1 What this service is

The **UPYOG Aggregation Service** is a **Backend-for-Frontend (BFF)** microservice written in
**Go 1.24** using the **Gin** HTTP framework. The citizen dashboard needs data from several
backend UPYOG services (inbox, billing, trade licence, user events, drafts, advertisements —
and now workflow). Instead of the mobile/web client making six or seven round trips, it makes
**one** call:

```
POST /api/v1/aggregate
```

The service fans that single request out **concurrently** to the relevant downstream services,
tolerates partial failures (one broken backend never takes down the whole dashboard), and
merges everything into one JSON payload — with Redis caching and JWT passthrough on the way.

> **Java mental model:** a Spring Boot `@RestController` that uses
> `CompletableFuture.allOf()` to call several downstream services in parallel through a
> `WebClient` wrapped in Resilience4j `@CircuitBreaker` + `@Retry`, then combines the results
> into one response DTO. Every section below carries this comparison forward.

## 1.2 Project layout — the package map

In Go, the directory structure *is* the package structure. There is no `pom.xml`; dependencies
live in `go.mod` (with `go.sum` as the lockfile). Anything under `internal/` cannot be imported
by code outside this module — a compiler-enforced visibility boundary (think Java 9 module
`exports`, but stricter and free).

```
municipal-services/upyog-aggregation-service/
├─ main.go                        ← entry point (≈ SpringApplication.run + all @Bean wiring)
├─ go.mod / go.sum                ← dependencies (≈ pom.xml + lockfile)
├─ configs/                       ← application-{env}.yaml (≈ application-{profile}.yml)
├─ api/
│  ├─ router.go                   ← route + middleware registration (≈ SecurityFilterChain/WebMvcConfigurer)
│  ├─ aggregate_handler.go        ← POST /api/v1/aggregate (≈ @PostMapping)
│  └─ health_handler.go           ← /health /readiness /liveness (≈ Actuator)
├─ internal/
│  ├─ aggregation/
│  │  ├─ engine/engine.go         ← fan-out orchestrator (≈ CompletableFuture.allOf)
│  │  ├─ executor/executor.go     ← per-provider timeout/metrics wrapper
│  │  └─ registry/registry.go     ← name → provider lookup (≈ ApplicationContext.getBean)
│  ├─ auth/                       ← JWT validation + RBAC authorizer
│  ├─ cache/redis.go              ← Redis JSON cache, tenant-scoped keys (≈ @Cacheable)
│  ├─ clients/                    ← resilient HTTP client + circuit breaker + retry
│  ├─ common/                     ← constants + typed context accessors (≈ MDC/RequestContextHolder)
│  ├─ config/                     ← Viper config loading (≈ @ConfigurationProperties)
│  ├─ dto/                        ← request/response contracts (≈ DTO classes + Bean Validation)
│  ├─ errors/                     ← AppError type + factory functions (≈ exception hierarchy)
│  ├─ mapper/ metrics/ tracing/   ← utilities, Prometheus, OpenTelemetry
│  ├─ middleware/                 ← 13 Gin middlewares (≈ servlet Filters)
│  ├─ providers/                  ← THE BUSINESS LOGIC — one file per data provider
│  └─ validator/                  ← deep request validation
├─ pkg/logger/                    ← Zap structured logging (≈ SLF4J + Logback + MDC)
└─ test/                          ← mocks + unit tests
```

## 1.3 Request lifecycle, step by step

A single `POST /api/v1/aggregate` flows through these stages. File names are the actual
source files so you can follow along in the code.

```
 Client (React/Flutter dashboard)
   │  POST /api/v1/aggregate
   │  Authorization: Bearer <JWT>   X-Tenant-Id: pb.amritsar
   ▼
 [1] api/router.go — middleware chain (order matters, like Spring filter order):
     Recovery → RequestID → CorrelationID → Tracing → Logging → HTTPMetrics
     → Compression → LocaleResolver → TenantResolver → Authentication(JWT)
     → RateLimiter → Audit
   ▼
 [2] api/aggregate_handler.go — Handle()
     • binds JSON body into dto.AggregateRequest   (≈ @RequestBody + @Valid)
     • deep-validates via internal/validator        (≈ custom ConstraintValidator)
   ▼
 [3] internal/aggregation/engine/engine.go — Aggregate()
     • launches ONE GOROUTINE PER requested provider (errgroup fan-out)
       (≈ CompletableFuture.runAsync per provider, then allOf().join())
   ▼
 [4] internal/aggregation/executor/executor.go — Execute()   (one per goroutine)
     • resolves provider by name from registry      (≈ context.getBean(name))
     • wraps it in a per-provider timeout context   (≈ orTimeout())
     • opens a tracing span, records metrics
     • translates provider errors → SUCCESS/FAILED/TIMEOUT status
   ▼
 [5] internal/providers/<provider>.go — Execute()
     • optional Redis cache lookup (tenant-scoped key)
     • calls the backend service through internal/clients/client.go:
         circuit breaker → retry w/ exponential backoff → JWT + header propagation
     • parses backend JSON into a dashboard-friendly struct
   ▼
 [6] Engine collects every ProviderResponse into a sync.Map
     (≈ ConcurrentHashMap), merges into one dto.AggregateResponse
   ▼
 HTTP 200 — one JSON payload; failed providers appear as
 {"status": "FAILED"/"TIMEOUT", ...} entries WITHOUT failing the request.
```

**Key design property:** the aggregate call always returns HTTP 200 once it passes
validation/auth. Individual provider failures are *data*, not errors — each provider's entry
carries its own `status`. The dashboard renders whatever succeeded.

## 1.4 The components in more detail

### main.go — manual dependency injection

There is no DI container in Go. `main()` constructs and wires every component by hand, in
order: logger → config → tracing → metrics → Redis cache → validator → HTTP clients →
provider registry → executor → engine → JWT validator → router → HTTP server with graceful
shutdown. This "manual DI" is idiomatic Go, not a shortcut. When you add a new provider, this
is where you register it (you will see this in Part 2).

### dto/ — the API contract

`dto.AggregateRequest` is the request body. The client lists which providers it wants:

```json
{
  "requestId": "f58c5b1d-2d53-4f8d-9cb3-1f5d6f0c2b45",
  "page": "citizen-home",
  "tenantId": "pb.amritsar",
  "requests": [
    { "provider": "quick-summary" },
    { "provider": "new-applications",
      "pagination": { "page": 0, "size": 10 },
      "filters": { "moduleName": "TL", "sinceDays": 7 } }
  ]
}
```

Struct tags like `binding:"required,uuid"` are Gin's Bean Validation (≈ `@NotNull @UUID`).
`ProviderRequest.Filters` is a free-form `map[string]interface{}` (≈ `Map<String,Object>`)
so each provider defines its own filter vocabulary without changing the API contract.

### providers/ — the plugin point

Every data source implements one small interface:

```go
type DataProvider interface {
    Name() string
    Execute(ctx context.Context, request dto.ProviderRequest,
            aggReq dto.AggregateRequest) (*dto.ProviderResponse, error)
}
```

> **Java equivalent:** `interface DataProvider { String getName(); ProviderResponse execute(...); }`
> — but Go interfaces are satisfied *implicitly*. There is no `implements` keyword: any struct
> with matching `Name()` and `Execute()` methods automatically qualifies.

All providers **embed** `BaseProvider` (composition, Go's substitute for `extends`), which
gives them a shared HTTP client handle, Redis cache handle, logger, metrics, and default cache
TTL, plus helpers `BuildCacheKey` / `GetCached` / `SetCached`.

The registry (`internal/aggregation/registry`) is a thread-safe `sync.Map` of
`name → DataProvider`, filled once at startup, resolved per request — the moral equivalent of
`ApplicationContext.getBean(name, DataProvider.class)`.

**This is the whole extension model:** to add a data source you write one provider file and
register it in `main.go`. Nothing in the handler, engine, executor, or registry changes.

### clients/ — the resilient HTTP client

`clients.Client` is the project's `WebClient + Resilience4j` in one place:

- connection pooling and keep-alive via `http.Transport`
- **JWT passthrough** — reads the caller's bearer token from `context.Context` and forwards it
- `X-Request-Id` / `X-Correlation-Id` propagation
- retry with exponential backoff + jitter (`retry.go`), only on network errors and 5xx
- a circuit breaker per client instance (`circuit_breaker.go`: Closed → Open → Half-Open)
- a tracing span and Prometheus metrics per call

Because the breaker is **per client instance**, giving a downstream service its own
`Client` gives it its own failure isolation domain. That is why Part 2 creates a dedicated
workflow client rather than reusing the shared one.

### cache/ — Redis

Values are JSON-serialised under tenant-scoped keys: `agg:{tenantId}:{provider}:{extra...}`.
`Get` returns `(hit bool, err error)` — a miss is not an error. Note that when Redis is
unreachable at startup, `main.go` logs a warning and passes a **nil** cache handle to
providers, so provider code must nil-check the cache before using it (Part 2's provider does).

### Cross-cutting: context.Context

Every function takes `ctx context.Context` as its first parameter. It carries deadlines,
cancellation, and request-scoped values (auth token, user id, locale, request id). It is the
explicit, type-safe replacement for `ThreadLocal` + `CancellationToken` — nothing is ambient;
everything is passed.

---

# Part 2 — Feature Integration: Fetch All New Applications from Workflow

## 2.1 What we are building

**Feature:** a new data provider, **`new-applications`**, that returns all applications that
recently entered the workflow engine (`egov-workflow-v2`) — e.g. "show the citizen every
application created in the last 7 days, across modules, with its current workflow status."

**Consumer view.** After this change, the dashboard adds one entry to the `requests` array of
the same `POST /api/v1/aggregate` call it already makes:

```json
{ "provider": "new-applications",
  "pagination": { "page": 0, "size": 10 },
  "filters": { "sinceDays": 7, "moduleName": "TL" } }
```

No new endpoint, no new route, no handler change — that is the payoff of the provider
architecture described in Part 1.

## 2.2 The downstream API we integrate with

`egov-workflow-v2` is a Java/Spring service in this same monorepo
(`core-services/egov-workflow-v2`). Its search endpoint, from
`org.egov.wf.web.controllers.WorkflowController`:

```java
@RequestMapping("/egov-wf")
public class WorkflowController {
    @RequestMapping(value = "/process/_search", method = RequestMethod.POST)
    public ResponseEntity<ProcessInstanceResponse> search(
            @Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
            @Valid @ModelAttribute ProcessInstanceSearchCriteria criteria) { ... }
}
```

Combined with the service's `server.servlet.context-path=/egov-workflow-v2`, the full path is:

```
POST /egov-workflow-v2/egov-wf/process/_search
```

This endpoint has the classic DIGIT/eGov shape, which trips up newcomers:

1. **It is a POST used for searching.** The JSON body carries only the `RequestInfo`
   envelope (API id, timestamp, **authToken**); the actual **search criteria travel as URL
   query parameters** (`@ModelAttribute` binds them from the query string).
2. Useful criteria from `ProcessInstanceSearchCriteria`: `tenantId`, `businessService`,
   `moduleName`, `status`, `fromDate`/`toDate` (epoch millis), `offset`/`limit`, `history`.
3. The response is `ProcessInstanceResponse`:

```json
{
  "ProcessInstances": [
    {
      "id": "pi-001",
      "tenantId": "pb.amritsar",
      "businessService": "NewTL",
      "businessId": "TL-2026-08-000123",
      "action": "APPLY",
      "moduleName": "TL",
      "state": { "state": "APPLIED", "applicationStatus": "APPLIED" },
      "auditDetails": { "createdTime": 1754000000000 }
    }
  ],
  "totalCount": 42
}
```

**"New applications"** = process instances whose `auditDetails.createdTime` falls inside a
look-back window, expressed to the API as `fromDate = now − sinceDays`.

## 2.3 Design decisions (and why)

| Decision | Choice | Rationale |
|---|---|---|
| Where does the logic live? | A new provider file, nothing else | Providers are the designed extension point; handler/engine/executor stay untouched. |
| Provider name | `new-applications` | Kebab-case like the existing seven (`recent-applications`, `due-renewals`, …); this is the string clients put in `requests[].provider`. |
| HTTP client | A **dedicated** workflow client in `main.go` | Circuit breaker state is per client instance. A flaky workflow service must not trip a breaker shared with billing/inbox calls. Mirrors the existing `draftClient` precedent. |
| Auth | Forward the caller's JWT in **both** the `Authorization` header (done automatically by `clients.Client`) and the DIGIT `RequestInfo.authToken` body field (done by the provider) | DIGIT services validate the token from `RequestInfo`; the header keeps gateway/mesh happy. |
| "New" window | `sinceDays` filter, default 7 | Config-free default; clients can widen/narrow per request. |
| Caching | Tenant + full query string as the cache key, TTL from config | Two users of the same tenant asking the same page share a cache entry; different filters never collide. Nil-cache guarded (Redis may be down — fail open to the backend). |
| Response shape | Flattened `WorkflowApplication` DTO, not the raw `ProcessInstance` | BFF principle: return exactly what the dashboard renders; don't leak the workflow engine's internal model to the frontend. |
| Failure behaviour | Return `error` and let the executor classify it | The executor already converts errors into `FAILED`/`TIMEOUT` entries without failing the aggregate call — no extra code needed. |

## 2.4 Files changed — overview

| # | File | Kind | Purpose |
|---|---|---|---|
| 1 | `internal/providers/new_applications.go` | **new** | The provider: query building, RequestInfo body, HTTP call, response mapping, caching |
| 2 | `internal/providers/new_applications_test.go` | **new** | Unit tests with a mock HTTP backend (`httptest`) |
| 3 | `main.go` | modified | Build the dedicated workflow HTTP client; register the provider |
| 4 | `configs/application-dev.yaml` | modified | Per-provider timeout/cacheTTL/retries override |
| 5 | `configs/application-local.yaml` | modified | Same override for local runs |
| 6 | `configs/application-prod.yaml` | modified | Same override, tighter prod timeout |

The `backend.services.workflow` base-URL entry **already existed** in every environment YAML
(e.g. `http://egov-workflow.dev:8080`), so no new backend endpoint configuration was needed —
only per-provider tuning was added.

Notice what did **not** change: `api/router.go`, `api/aggregate_handler.go`, `engine`,
`executor`, `registry`, `dto`, `validator`. That is the architecture working as intended.

## 2.5 File 1 — `internal/providers/new_applications.go` (new)

This is the entire feature's business logic. The full file is in the repository; here it is
broken into its five parts with commentary.

### (a) Constants and the outward-facing DTOs

```go
const newApplicationsProviderName = "new-applications"

const workflowProcessSearchPath = "/egov-workflow-v2/egov-wf/process/_search"

const defaultNewApplicationsWindowDays = 7

// WorkflowApplication is the flattened, dashboard-friendly view of a single
// workflow process instance returned by this provider.
type WorkflowApplication struct {
    ID                string `json:"id"`
    TenantID          string `json:"tenantId"`
    BusinessService   string `json:"businessService"`
    ModuleName        string `json:"moduleName"`
    ApplicationNumber string `json:"applicationNumber"`
    Status            string `json:"status"`
    State             string `json:"state"`
    Action            string `json:"action"`
    CreatedTime       int64  `json:"createdTime"`
}

// NewApplicationsData is the payload returned by the provider.
type NewApplicationsData struct {
    Applications []WorkflowApplication `json:"applications"`
    TotalCount   int                   `json:"totalCount"`
}
```

> **Java notes.** The backtick strings are **struct tags** — Go's annotations, read by the
> JSON marshaller: `json:"tenantId"` ≈ `@JsonProperty("tenantId")`. A capitalised field name
> (`ID`, `TenantID`) means *public*; lowercase means *package-private*. There are no getters or
> setters — direct field access is idiomatic.

### (b) The provider struct and its factory

```go
// NewApplicationsProvider retrieves recently created applications from the
// egov-workflow-v2 process-instance search API.
type NewApplicationsProvider struct {
    BaseProvider
}

func NewNewApplicationsProvider(
    client *clients.Client,
    c *cache.Cache,
    log *logger.Logger,
    m *metrics.Metrics,
    ttl time.Duration,
) *NewApplicationsProvider {
    return &NewApplicationsProvider{
        BaseProvider: NewBaseProvider(newApplicationsProviderName, client, c, log, m, ttl),
    }
}
```

> **Java notes.** `BaseProvider` written *inside* the struct body with no field name is
> **embedding** — Go's composition-instead-of-inheritance. All of `BaseProvider`'s methods
> (`Name()`, `BuildCacheKey()`, `GetCached()`, `SetCached()`) are *promoted* onto
> `NewApplicationsProvider`, so it satisfies the `DataProvider` interface without declaring
> anything (`Name()` comes from the base; `Execute()` is defined below). `NewXxx` factory
> functions are Go's constructors; the doubled `NewNew…` is just the `New` + struct-name
> convention colliding with a struct that itself starts with "New".

### (c) `Execute` — the core method

```go
func (p *NewApplicationsProvider) Execute(
    ctx context.Context,
    request dto.ProviderRequest,
    aggReq dto.AggregateRequest,
) (*dto.ProviderResponse, error) {
    query := p.buildSearchQuery(request, aggReq)
    path := workflowProcessSearchPath + "?" + query

    // 1. Cache lookup — the query string uniquely identifies this search.
    cacheKey := p.BuildCacheKey(aggReq.TenantID, query)
    if p.Cache != nil {
        var cached NewApplicationsData
        hit, err := p.GetCached(ctx, cacheKey, &cached)
        if err != nil {
            p.Log.WithContext(ctx).Warn("cache lookup failed for new-applications", zap.Error(err))
        }
        if hit {
            return &dto.ProviderResponse{
                Status: common.StatusSuccess,
                Cached: true,
                Data:   cached,
            }, nil
        }
    }

    headers := map[string]string{
        common.HeaderTenantID: aggReq.TenantID,
    }

    // 2. egov-workflow-v2 expects a DIGIT RequestInfo envelope in the POST body.
    body := workflowSearchBody{
        RequestInfo: workflowRequestInfo{
            APIID:     "upyog-aggregation-service",
            Ver:       "1.0",
            Ts:        time.Now().UnixMilli(),
            MsgID:     aggReq.RequestID,
            AuthToken: common.AuthToken(ctx),
        },
    }

    // 3. Resilient HTTP call (circuit breaker + retry + JWT/header propagation).
    resp, err := p.Client.Post(ctx, path, body, headers)
    if err != nil {
        return nil, fmt.Errorf("POST %s: %w", path, err)
    }
    if resp.StatusCode != http.StatusOK {
        return nil, fmt.Errorf("POST %s returned status %d", path, resp.StatusCode)
    }

    // 4. Parse and flatten the workflow response.
    var result processInstanceSearchResponse
    if err := json.Unmarshal(resp.Body, &result); err != nil {
        return nil, fmt.Errorf("unmarshal workflow process search response: %w", err)
    }

    data := NewApplicationsData{
        Applications: mapProcessInstances(result.ProcessInstances),
        TotalCount:   result.TotalCount,
    }

    // 5. Populate the cache for the next identical request.
    if p.Cache != nil {
        if cacheErr := p.SetCached(ctx, cacheKey, data, p.CacheTTL); cacheErr != nil {
            p.Log.WithContext(ctx).Warn("failed to cache new-applications", zap.Error(cacheErr))
        }
    }

    return &dto.ProviderResponse{
        Status: common.StatusSuccess,
        Data:   data,
    }, nil
}
```

Point by point:

- **`(p *NewApplicationsProvider)`** is the *receiver* — Go's explicit `this`. Methods hang
  off types via receivers instead of living inside a class body.
- **Errors are values.** `return nil, fmt.Errorf("POST %s: %w", path, err)` returns a wrapped
  error (≈ exception chaining via `new ServiceException(msg, cause)`); `%w` preserves the
  cause for `errors.Is`/`errors.As`. There is no throw/catch anywhere. The executor upstream
  inspects the error and emits `"status": "FAILED"` or `"TIMEOUT"` for this provider only.
- **`common.AuthToken(ctx)`** pulls the caller's raw JWT out of the request context (put
  there by the Authentication middleware) so it can be re-embedded in `RequestInfo.authToken`
  — DIGIT services authenticate from the body envelope. Separately, `clients.Client`
  *also* forwards the same token as an `Authorization: Bearer …` header automatically.
- **Cache-aside pattern with fail-open semantics**: a cache error logs a warning and falls
  through to the backend call; a Redis outage degrades performance, never availability.
  The `p.Cache != nil` guard matters because `main.go` passes a nil cache when Redis is
  unreachable at startup.
- **Timeout handling is free.** `ctx` arrives already wrapped in this provider's timeout
  (`providers.custom.new-applications.timeout`, else `providers.defaultTimeout`) by the
  executor; the HTTP client honours context cancellation between retries.

### (d) Query building — translating the BFF request into workflow criteria

```go
func (p *NewApplicationsProvider) buildSearchQuery(
    request dto.ProviderRequest,
    aggReq dto.AggregateRequest,
) string {
    params := url.Values{}
    params.Set("tenantId", aggReq.TenantID)
    params.Set("history", "false")

    // Look-back window: applications created within the last N days.
    sinceDays := defaultNewApplicationsWindowDays
    if request.Filters != nil {
        // JSON numbers arrive as float64 through the free-form filter map.
        if v, ok := request.Filters["sinceDays"].(float64); ok && v > 0 {
            sinceDays = int(v)
        }
    }
    fromDate := time.Now().AddDate(0, 0, -sinceDays).UnixMilli()
    params.Set("fromDate", strconv.FormatInt(fromDate, 10))

    // Pagination → offset/limit.
    offset, limit := 0, 10
    if request.Pagination != nil {
        offset = request.Pagination.Page * request.Pagination.Size
        limit = request.Pagination.Size
    }
    params.Set("offset", strconv.Itoa(offset))
    params.Set("limit", strconv.Itoa(limit))

    // Optional pass-through filters.
    if request.Filters != nil {
        if v, ok := request.Filters["businessService"].(string); ok && v != "" {
            params.Set("businessService", v)
        }
        if v, ok := request.Filters["moduleName"].(string); ok && v != "" {
            params.Set("moduleName", v)
        }
        if v, ok := request.Filters["status"].(string); ok && v != "" {
            params.Set("status", v)
        }
    }

    return params.Encode()
}
```

- `url.Values` + `Encode()` gives correct percent-encoding and deterministic (sorted) key
  order — which also makes the query string a **stable cache key**. (The existing
  `recent-applications` provider concatenates strings by hand; `url.Values` is the safer
  pattern to copy going forward.)
- **`request.Filters["sinceDays"].(float64)`** is a *type assertion* (≈ `instanceof` +
  cast). The comma-ok form returns `(value, false)` instead of panicking when the type
  doesn't match. All JSON numbers decode to `float64` in a `map[string]interface{}` — the Go
  equivalent of Jackson giving you `Double` inside a `Map<String,Object>`.
- `history=false` asks workflow for only the **current** state row per application rather
  than every historical transition.

### (e) Wire-format structs and the mapper

```go
// workflowRequestInfo mirrors the DIGIT RequestInfo envelope required by
// egov-workflow-v2 POST endpoints.
type workflowRequestInfo struct {
    APIID     string `json:"apiId"`
    Ver       string `json:"ver"`
    Ts        int64  `json:"ts"`
    MsgID     string `json:"msgId"`
    AuthToken string `json:"authToken,omitempty"`
}

type workflowSearchBody struct {
    RequestInfo workflowRequestInfo `json:"RequestInfo"`
}

type processInstanceSearchResponse struct {
    ProcessInstances []processInstance `json:"ProcessInstances"`
    TotalCount       int               `json:"totalCount"`
}

type processInstance struct {
    ID              string          `json:"id"`
    TenantID        string          `json:"tenantId"`
    BusinessService string          `json:"businessService"`
    BusinessID      string          `json:"businessId"`
    Action          string          `json:"action"`
    ModuleName      string          `json:"moduleName"`
    State           *processState   `json:"state"`
    AuditDetails    *wfAuditDetails `json:"auditDetails"`
}

type processState struct {
    State             string `json:"state"`
    ApplicationStatus string `json:"applicationStatus"`
}

type wfAuditDetails struct {
    CreatedTime int64 `json:"createdTime"`
}

func mapProcessInstances(instances []processInstance) []WorkflowApplication {
    apps := make([]WorkflowApplication, 0, len(instances))
    for _, pi := range instances {
        app := WorkflowApplication{
            ID:                pi.ID,
            TenantID:          pi.TenantID,
            BusinessService:   pi.BusinessService,
            ModuleName:        pi.ModuleName,
            ApplicationNumber: pi.BusinessID,
            Action:            pi.Action,
        }
        if pi.State != nil {
            app.Status = pi.State.ApplicationStatus
            app.State = pi.State.State
        }
        if pi.AuditDetails != nil {
            app.CreatedTime = pi.AuditDetails.CreatedTime
        }
        apps = append(apps, app)
    }
    return apps
}
```

- These types are **lowercase = unexported** — private wire-format details of this file,
  invisible even to sibling packages. Only the flattened `WorkflowApplication` is public.
- `State` and `AuditDetails` are **pointers** (`*processState`) precisely because the
  backend may send `"state": null`; a nil pointer models JSON null, and the mapper
  nil-checks before dereferencing (this exact case is unit-tested).
- The struct fields deliberately mirror the Java model
  (`org.egov.wf.web.models.ProcessInstance`) — `businessId` is the human-readable
  application number, renamed to `applicationNumber` for the dashboard.

## 2.6 File 3 — `main.go` (modified)

Two additions, both in the "Registry + providers" wiring block. First, a dedicated client
for the workflow service, configured from the existing `backend.services.workflow` YAML entry:

```go
workflowEndpoint := cfg.Backend.Services["workflow"]
workflowClient := clients.NewClient(clients.ClientConfig{
    ServiceName:      "egov-workflow-v2",
    BaseURL:          workflowEndpoint.BaseURL,
    Timeout:          workflowEndpoint.Timeout,
    MaxConns:         workflowEndpoint.MaxConns,
    CircuitThreshold: workflowEndpoint.CircuitThreshold,
    CircuitTimeout:   workflowEndpoint.CircuitTimeout,
}, log, m)
```

Second, one registration line at the end of the existing block:

```go
reg.Register(providers.NewNewApplicationsProvider(workflowClient, c, log, m, cacheTTL))
```

> **Java analogy:** this is exactly "define a new `@Bean WebClient workflowWebClient()` and a
> new `@Component` provider that Spring's component scan would have picked up" — except here
> the wiring is explicit, in one readable place. `ServiceName: "egov-workflow-v2"` becomes the
> label on every log line, Prometheus metric (`backend_requests_total{service="egov-workflow-v2"}`),
> and tracing span for calls made through this client, and the client's circuit breaker is
> isolated to the workflow service.

## 2.7 Files 4–6 — configuration (modified)

Every environment already had the workflow base URL under `backend.services.workflow`
(that is what `cfg.Backend.Services["workflow"]` reads):

```yaml
backend:
  services:
    workflow:
      baseUrl: "http://egov-workflow.dev:8080"   # local: http://localhost:8084
      timeout: 10s
      maxConns: 30
      circuitThreshold: 5
      circuitTimeout: 30s
```

What was added is per-provider tuning under `providers.custom` (dev/local shown; prod uses a
tighter 6s timeout):

```yaml
providers:
  custom:
    new-applications:
      timeout: 8s      # executor-enforced budget for this provider per request
      cacheTTL: 1m     # "new applications" should feel fresh — short TTL
      retries: 2
```

> ≈ `@ConfigurationProperties`: Viper reads `configs/application-{APP_ENV}.yaml` into the
> `Config` struct via `mapstructure` tags, with `UPYOG_`-prefixed environment variables as
> overrides. `main.go` copies `custom.*.timeout` into the executor's per-provider timeout map,
> so this value caps the provider even when the workflow service itself is slower.

## 2.8 How the pieces interact at runtime

The complete sequence for one dashboard request that asks for `quick-summary` **and**
`new-applications` (the two providers run in parallel; only the new one is expanded):

```
Client            Router+MW        Handler        Engine          Executor            NewApplicationsProvider   workflowClient        Redis      egov-workflow-v2
  │ POST /api/v1/aggregate │           │             │                │                        │                     │                │              │
  ├────────────────────────▶           │             │                │                        │                        │                │              │
  │   JWT validated; token, tenant,    │             │                │                        │                        │                │              │
  │   requestId stored in ctx          │             │                │                        │                        │                │              │
  │                        ├──────────▶│ bind+validate                │                        │                        │                │              │
  │                        │           ├────────────▶│ goroutine #1: quick-summary  ─────▶ (runs in parallel …)         │                │              │
  │                        │           │             ├─ goroutine #2 ▶│ resolve("new-applications")                     │                │              │
  │                        │           │             │                │ ctx ← timeout 8s       │                        │                │              │
  │                        │           │             │                ├───────────────────────▶│ buildSearchQuery()     │                │              │
  │                        │           │             │                │                        ├─ GET agg:pb.amritsar:new-applications:…─▶│              │
  │                        │           │             │                │                        │◀─ miss ────────────────────────────────┤│              │
  │                        │           │             │                │                        ├─ RequestInfo{authToken}│                │              │
  │                        │           │             │                │                        ├───────────────────────▶│ breaker.Allow()│              │
  │                        │           │             │                │                        │                        ├─ POST /egov-workflow-v2/egov-wf/process/_search?tenantId=…&fromDate=…&offset=0&limit=10
  │                        │           │             │                │                        │                        │   Authorization: Bearer …  X-Request-Id: …  ──────────────▶│
  │                        │           │             │                │                        │                        │◀─ 200 {ProcessInstances:[…], totalCount:2} ────────────────┤
  │                        │           │             │                │                        │◀─ retry/5xx handled ──┤│                │              │
  │                        │           │             │                │                        ├─ mapProcessInstances() │                │              │
  │                        │           │             │                │                        ├─ SETEX cacheKey 60s ──────────────────▶│              │
  │                        │           │             │                │◀─ {SUCCESS, data} ─────┤                        │                │              │
  │                        │           │             │◀─ store in sync.Map (both goroutines) ──┤                        │                │              │
  │                        │           │◀─ merged AggregateResponse ──┤                        │                        │                │              │
  │◀─ 200 {responses:{quick-summary:…, new-applications:…}} ──────────┤                        │                        │                │              │
```

Failure modes, all inherited for free:

- **Workflow slow** → the 8s provider timeout fires; the response contains
  `"new-applications": {"status": "TIMEOUT", ...}`; every other provider still succeeds.
- **Workflow returns 5xx** → the client retries with backoff (2 retries configured); repeated
  failures open the workflow client's circuit breaker; while open, calls fail fast with
  `CIRCUIT_OPEN` instead of piling up threads — and only workflow calls are affected.
- **Redis down** → cache lookups are skipped/fail open; the backend is called directly.

## 2.9 File 2 — the unit tests, and how to run everything

`internal/providers/new_applications_test.go` uses Go's built-in test framework (no JUnit
dependency; `go test` is part of the toolchain) plus `net/http/httptest`, the standard-library
equivalent of WireMock — it spins up a real HTTP server on a random port:

- **`TestNewApplicationsProvider_Execute`** — full happy path: asserts the outbound request
  (POST, exact path, `tenantId`/`businessService`/`offset=5&limit=5`/`fromDate` query
  parameters, `Authorization: Bearer test-token` header, `RequestInfo.authToken` and
  `RequestInfo.msgId` in the body) *and* the mapped result (totalCount, field-by-field
  mapping, and that a `"state": null` instance maps cleanly instead of panicking).
- **`TestNewApplicationsProvider_BackendError`** — a 400 from workflow must surface as an
  error (which the executor then reports as a `FAILED` provider entry).
- **`TestNewApplicationsProvider_DefaultQuery`** — no pagination/filters →
  `offset=0&limit=10&history=false` defaults.

Run from `municipal-services/upyog-aggregation-service/`:

```bash
go build ./...      # compile everything            (≈ mvn compile)
go vet ./...        # static analysis               (≈ SpotBugs-lite, built in)
go test ./...       # run all tests                 (≈ mvn test)
go run .            # start the service             (APP_ENV selects the YAML profile)
```

All three commands pass on this change. A live smoke test was also performed: the service was
started with `APP_ENV=local` against a mock workflow backend on `localhost:8084` (the local
YAML's workflow base URL), and the aggregate call returned:

```json
{
  "success": true,
  "requestId": "f58c5b1d-2d53-4f8d-9cb3-1f5d6f0c2b45",
  "responses": {
    "new-applications": {
      "status": "SUCCESS",
      "executionTime": 1,
      "data": {
        "applications": [
          {
            "id": "pi-001",
            "tenantId": "pb.amritsar",
            "businessService": "NewTL",
            "moduleName": "TL",
            "applicationNumber": "TL-2026-08-000123",
            "status": "APPLIED",
            "state": "APPLIED",
            "action": "APPLY",
            "createdTime": 1754000000000
          },
          {
            "id": "pi-002",
            "tenantId": "pb.amritsar",
            "businessService": "PGR.CREATE",
            "moduleName": "PGR",
            "applicationNumber": "PGR-2026-08-000777",
            "status": "PENDING",
            "state": "PENDINGFORASSIGNMENT",
            "action": "APPLY",
            "createdTime": 1754050000000
          }
        ],
        "totalCount": 2
      }
    }
  }
}
```

and the mock backend logged the inbound call exactly as designed:

```
PATH: /egov-workflow-v2/egov-wf/process/_search?fromDate=…&history=false&limit=10&moduleName=TL&offset=0&tenantId=pb.amritsar
BODY: {"RequestInfo":{"apiId":"upyog-aggregation-service","ver":"1.0","ts":…,"msgId":"f58c5b1d-…"}}
```

## 2.10 The generalized recipe — adding *any* future provider

This feature doubles as the template for every future integration:

1. **Create `internal/providers/<name>.go`** — define the provider struct embedding
   `BaseProvider`, a `New<Name>Provider` factory, and `Execute()` that: builds the backend
   request from `dto.ProviderRequest` (pagination/sort/filters), checks the cache (nil-guarded),
   calls `p.Client.Get/Post`, checks the status code, unmarshals, maps to a flat dashboard DTO,
   populates the cache, returns `(*dto.ProviderResponse, error)`.
2. **Wire it in `main.go`** — reuse `defaultClient`, or create a dedicated
   `clients.NewClient(...)` from a `backend.services.<key>` entry when the backend deserves its
   own circuit breaker and connection pool.
3. **Configure** — add `backend.services.<key>` (if new) and an optional
   `providers.custom.<name>` block (timeout/cacheTTL/retries) to each `configs/application-*.yaml`.
4. **Test** — a `_test.go` file next to the provider using `httptest`; then
   `go build ./... && go vet ./... && go test ./...`.
5. **Nothing else.** Router, handler, engine, executor, registry, DTOs, and validators are
   untouched by design.

## 2.11 Gotchas for Java developers (learned the hard way in this codebase)

- **JSON numbers are `float64`.** Anything numeric in `map[string]interface{}` filters must be
  asserted as `float64`, then converted (`int(v)`), never as `int`.
- **Nil is part of your data model.** JSON `null` for an object field needs a pointer type
  (`*processState`), and every dereference needs a nil check — there is no `Optional` wrapper
  and no NPE-with-stacktrace; a nil dereference is a panic.
- **Check every error, immediately.** The `if err != nil { return nil, fmt.Errorf("…: %w", err) }`
  block after every call is not noise; it is the error-handling model. Wrapping with `%w` is
  the exception-chaining you are used to.
- **The cache handle can be nil** (Redis down at startup) — guard it. Method calls on nil
  pointers compile fine in Go and only fail at runtime when a field is touched.
- **Context first, always.** `ctx context.Context` as the first parameter of every function is
  a hard convention; timeouts, cancellation, auth token, and trace ids all ride on it. Dropping
  it breaks timeout propagation silently.
- **`gofmt` is law.** No formatting debates; run `gofmt -w .` (or rely on the IDE) before
  committing — CI's `golangci-lint` also enforces it.
- **DIGIT POST-searches**: body = `RequestInfo` only; criteria = query string. Copy this
  provider when integrating any other DIGIT/eGov service (inbox v2, billing `_search`, etc.).

## 2.12 Integration tests — full stack and live niuatt

Beyond the provider unit tests, the change ships an integration suite under
`test/integration/` (the Makefile's `make test-integration` target was already pointing there;
the folder now exists). The files carry the build tag `//go:build integration`, so they are
excluded from a plain `go test ./...` and run only when requested:

```bash
make test-integration
# equivalent to:
go test ./test/integration/... -tags=integration -v -count=1
```

> **Java analogy:** the build tag plays the role of the Maven failsafe plugin's `*IT.java`
> split — unit tests always run, integration tests only in an explicit phase.

### In-process full-stack tests (`stack_test.go`)

These assemble the **entire service** exactly as `main.go` does — router with all middleware,
handler, engine, executor, registry, real providers, real resilient HTTP clients — but point
the backend base URLs at `httptest` mock servers (≈ `@SpringBootTest(webEnvironment=MOCK)` +
WireMock). One subtlety: the Prometheus metrics object registers collectors into the
process-global default registry, so the suite creates it exactly once via `sync.Once`.

| Test | Scenario proven |
|---|---|
| `TestAggregate_EndToEnd_Success` | Two providers requested at once; both succeed; workflow mock receives the translated criteria (`tenantId`, `moduleName`, `fromDate`, `limit`); response mapping verified through the real HTTP round trip |
| `TestAggregate_EndToEnd_PartialFailure` | Workflow backend returns 500 → `new-applications` comes back `FAILED` with an error code while `recent-applications` still succeeds; the aggregate call stays HTTP 200 |
| `TestAggregate_EndToEnd_ProviderTimeout` | Workflow sleeps past a 200 ms per-provider budget → `TIMEOUT` / `PROVIDER_TIMEOUT` for that provider only; the fast provider is unaffected |
| `TestAggregate_EndToEnd_UnknownProvider` | Requesting an unregistered provider yields `FAILED` / `PROVIDER_NOT_FOUND` inside a 200 response |
| `TestAggregate_EndToEnd_ValidationError` | A non-UUID `requestId` is rejected with HTTP 400 before any provider runs |

All five pass (`ok github.com/upyog/upyog-aggregation-service/test/integration`).

### Live test against niuatt (`niuatt_live_test.go`)

`TestNiuatt_Live_OAuthAndNewApplications` exercises the real NIUA test environment. It is
**skipped automatically** unless credentials are present, so CI never depends on niuatt:

| Env var | Required | Default | Meaning |
|---|---|---|---|
| `NIUATT_USERNAME` | yes | — | niuatt login |
| `NIUATT_PASSWORD` | yes | — | niuatt password |
| `NIUATT_TENANT_ID` | no | `pg.citya` | tenant to search |
| `NIUATT_USER_TYPE` | no | `CITIZEN` | `CITIZEN` or `EMPLOYEE` |
| `NIUATT_BASE_URL` | no | `https://niuatt.niua.in` | gateway base URL |

The test performs the same two steps every UPYOG frontend performs:

1. **OAuth password grant** against `POST /user/oauth/token` with the standard public client
   (`Authorization: Basic ZWdvdi11c2VyLWNsaWVudDo=`, i.e. `egov-user-client:` with an empty
   secret) and form fields `grant_type=password`, `username`, `password`, `tenantId`,
   `userType`. The returned `access_token` is a DIGIT auth token.
2. **Runs the actual `new-applications` provider** (not a copy — the same code that serves
   production requests) against the live gateway, with the token placed in the request context
   the same way the Authentication middleware would, then prints the full mapped JSON response
   and asserts its shape.

```bash
NIUATT_USERNAME='<user>' NIUATT_PASSWORD='<password>' NIUATT_TENANT_ID='pg.citya' \
  go test ./test/integration/... -tags=integration -run Niuatt -v -count=1
```

Connectivity to niuatt was verified from this change (without credentials): the workflow
endpoint answers with the DIGIT authorization error, and the OAuth endpoint with the standard
OAuth error — proving both services are reachable and the request format is understood:

```
POST https://niuatt.niua.in/egov-workflow-v2/egov-wf/process/_search?tenantId=pg.citya&…
→ {"ResponseInfo":null,"Errors":[{"code":"CustomException",
   "message":"You are not authorized to access this resource", …}]}

POST https://niuatt.niua.in/user/oauth/token   (placeholder credentials)
→ {"error_description":"Invalid username or password","error":"invalid_grant"}
```

Once a valid niuatt user is supplied through the environment variables above, the same test
prints the real application list for that tenant.

## 2.13 Deployment — Helm charts per UPYOG DevOps standards (niuatt)

Ready-to-copy deployment files live under `deployments/upyog-devops/`, mirroring the layout of
the **UPYOG-DevOps-niuatt** repository (`config-as-code/helm/...`), in the same style as
existing municipal-services charts such as `adv-services` and `cnd-service`:

```
deployments/upyog-devops/
├─ README.md                                  ← copy instructions + deploy/smoke-test steps
└─ config-as-code/helm/
   ├─ charts/municipal-services/upyog-aggregation-service/
   │  ├─ Chart.yaml                           ← depends on the shared `common` chart (0.0.5)
   │  ├─ values.yaml                          ← the actual deployment definition
   │  └─ templates/{deployment,service,ingress}.yaml   ← one-liners invoking common templates
   └─ environments/qa.yaml.additions          ← two blocks to merge into environments/qa.yaml
```

The chart was **render-verified** against the real `common` library chart pulled from the
devops repo (`helm dependency build` + `helm template` with niuatt-style globals) — it produces
the expected Service, Deployment, and Ingress.

How each UPYOG platform convention is satisfied, and what had to change in the service to be
deployable behind the platform gateway:

| Concern | How it works here |
|---|---|
| Gateway route | `ingress.zuul: true` + `context: upyog-aggregation-service` → the common chart annotates the Kubernetes Service with `zuul/route-path: upyog-aggregation-service`; the gateway's route-discovery job (`utilities/gateway-kubernetes-discovery`) converts that into `Path=/upyog-aggregation-service/** → http://upyog-aggregation-service.egov:8080/`. No manual route registration. |
| Context path | The gateway does **not** strip the route prefix (DIGIT services all serve under their own context path). A new `server.contextPath` config key was added; the router mounts every route under it. The qa profile sets `/upyog-aggregation-service`; probes and Prometheus annotations use the prefixed paths. Local/dev keep the empty default (root paths). |
| Authentication | Enforced by the gateway. DIGIT access tokens are opaque UUIDs — not JWTs — so in-service JWT validation is off in the qa profile. A new always-on `TokenPassthrough` middleware captures the bearer token into the request context (previously only the JWT-validating middleware did this), so downstream forwarding (`Authorization` header + `RequestInfo.authToken`) keeps working. Covered by an integration test. |
| Go, not Java | `appType: ""` skips the common chart's Spring/Tomcat/Kafka env injection; no DB → `initContainers.dbMigration.enabled: false` and no `-db` image. |
| Image build | An entry was added to the monorepo's `build/build-config.yml` (`builds/upyog/municipal-services/upyog-aggregation-service`) so standard UPYOG Jenkins CI builds/pushes the image. The service `Dockerfile`/`Makefile` build path was also fixed (`./cmd/server/` → `.`, where `main.go` actually lives). |
| Environment config | `configs/application-qa.yaml` was rewritten as the niuatt-cluster profile: Redis `redis.backbone:6379`, backends `egov-workflow-v2.egov` / `inbox.egov` / `billing-service.egov` / `tl-services.egov` / `egov-user-event.egov` / `adv-services.egov` / `upyog-draft-service.egov`. The chart additionally pins the deployment-sensitive keys via `UPYOG_*` env vars (Viper maps `UPYOG_A_B_C` → config key `a.b.c`), so the environment file can retune without an image rebuild — this override mechanism was smoke-tested live. |

Deployment steps and a post-deploy smoke test (health + authenticated aggregate call through
`https://niuatt.niua.in/upyog-aggregation-service/...`) are in
`deployments/upyog-devops/README.md`.

---

*Document generated from the actual implementation on branch
`cursor/workflow-new-applications-c3a5`; all code shown compiles, and all described tests pass
(`go build ./... && go vet ./... && go test ./...`).*
