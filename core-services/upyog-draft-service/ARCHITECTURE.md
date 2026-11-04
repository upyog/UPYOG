# UPYOG Centralized Draft Service — Architecture & Justification Document

**Service:** `upyog-draft-service`  
**Audience:** Platform, municipal module, frontend, and DevOps teams  
**Purpose:** Justify a separate centralized draft microservice vs. per-module draft tables  
**Status:** Pilot (TL integrated; SV/ADV/PT migration planned)

---

## 1. Executive Summary

Citizens need to **save in-progress municipal applications** (TL, Street Vending, Advertisement, Property Tax, etc.) and see a **count + list of incomplete applications on the login screen**.

Today, drafts are stored inconsistently across modules and are **not visible on the login dashboard**. The aggregation BFF was querying Inbox V2 with `status=DRAFT`, but per-service draft rows are **never indexed in Elasticsearch/Inbox V2** — so the dashboard always returned empty or zero.

**Recommendation:** Introduce **`upyog-draft-service`** — a standalone Java service with one Postgres table (`ug_draft_detail`) that:

- Stores opaque JSONB form payloads for any module (`businessService`: TL, SV, ADV, PT, …)
- Exposes a single `_search` + `_count` API for the login dashboard
- Lets domain services call `_markSubmitted` after a real application is created
- Uses the standard UPYOG persister pattern (Kafka → `egov-persister` → Postgres)

This aligns with existing DIGIT/UPYOG shared-service patterns (inbox, idgen, MDMS) and avoids N-way fan-out from the aggregation BFF as modules grow.

---

## 2. Problem Statement

| Pain point | Impact |
|------------|--------|
| TL has **no draft support** | Citizens lose partial TL form data on refresh/exit |
| SV, ADV, PT each have **different draft implementations** | Hard-delete vs soft-deactivate, different APIs, different tables |
| Login dashboard reads **Inbox V2 `status=DRAFT`** | Drafts are not in ES → count/list always wrong |
| Inbox V2 has **`/_search` only** — no `/_count` for drafts | Quick-summary draft count silently returns 0 |
| Draft deletion on submit is **conflated with draft save** in some modules | UX bugs when UI sends wrong flag |

**User story driving the design:**

> As a citizen, when I log in, I want to see how many applications I have in progress and resume any of them — across TL, SV, ADV, PT — from one place.

That is inherently a **cross-module, single-query** problem.

---

## 3. Current State (Before)

```mermaid
flowchart TB
    subgraph citizenUI [Citizen UI]
        LoginScreen[Login Dashboard]
        ModuleForms[Module Application Forms]
    end

    subgraph bff [BFF Layer]
        AggSvc[upyog-aggregation-service]
    end

    subgraph inboxLayer [Inbox Layer]
        InboxV2[Inbox V2 / Elasticsearch]
    end

    subgraph domainServices [Domain Services - Per-Service Drafts]
        SV[street-vending<br/>eg_sv_street_vending_draft_detail]
        ADV[advertisement<br/>eg_adv_draft_detail]
        PT[pt-services-v2<br/>eg_pt_drafts_v2]
        TL[tl-services<br/>NO DRAFT]
    end

    LoginScreen --> AggSvc
    AggSvc -->|"status=DRAFT (broken)"| InboxV2
    ModuleForms --> SV
    ModuleForms --> ADV
    ModuleForms --> PT
    ModuleForms --> TL

    SV -.->|"not indexed"| InboxV2
    ADV -.->|"not indexed"| InboxV2
    PT -.->|"not indexed"| InboxV2
```

### Per-module draft landscape today

| Module | Draft table | Save pattern | Cleanup on submit | Dashboard visible? |
|--------|-------------|--------------|-------------------|-------------------|
| Street Vending | `eg_sv_street_vending_draft_detail` | `isDraftApplication=true` on `_create` | Hard delete via Kafka | No |
| Advertisement | `eg_adv_draft_detail` | Same inline flag | Hard delete + timer cleanup | No |
| Property Tax | `eg_pt_drafts_v2` | Dedicated `/drafts/_create` | Soft deactivate (`isActive=false`) | No |
| Trade License | None | N/A | N/A | No |

---

## 4. Proposed Architecture (After)

```mermaid
flowchart TB
    subgraph citizenUI [Citizen UI]
        LoginScreen[Login Dashboard]
        TLForm[TL Form]
        SVForm[SV Form]
        ADVForm[ADV Form]
        PTForm[PT Form]
    end

    subgraph bff [BFF]
        AggSvc[upyog-aggregation-service]
    end

    subgraph draftLayer [Shared Draft Layer]
        DraftSvc[upyog-draft-service]
        DraftDB[(ug_draft_detail)]
        Persister[egov-persister]
        Kafka[(Kafka)]
    end

    subgraph domainServices [Domain Services]
        TL[tl-services]
        SV[street-vending]
        ADV[advertisement]
        PT[pt-services-v2]
    end

    subgraph workflowLayer [Submitted Apps - Unchanged]
        InboxV2[Inbox V2 / ES]
    end

    LoginScreen --> AggSvc
    AggSvc -->|"POST /draft/v1/_search + _count"| DraftSvc
    DraftSvc -->|"reads"| DraftDB

    TLForm -->|"auto-save"| DraftSvc
    SVForm -->|"auto-save"| DraftSvc
    ADVForm -->|"auto-save"| DraftSvc
    PTForm -->|"auto-save"| DraftSvc

    TLForm -->|"final submit"| TL
    SVForm -->|"final submit"| SV
    ADVForm -->|"final submit"| ADV
    PTForm -->|"final submit"| PT

    TL -->|"best-effort _markSubmitted"| DraftSvc
    SV -->|"best-effort _markSubmitted"| DraftSvc
    ADV -->|"best-effort _markSubmitted"| DraftSvc
    PT -->|"best-effort _markSubmitted"| DraftSvc

    DraftSvc -->|"writes via Kafka"| Kafka
    Kafka --> Persister
    Persister --> DraftDB

    TL --> InboxV2
    SV --> InboxV2
    ADV --> InboxV2
    PT --> InboxV2
```

**Design principle:** Inbox V2 remains for **submitted / workflow-tracked** applications. Draft service handles **in-progress forms only**. Do not index JSONB draft blobs into Elasticsearch.

---

## 5. High-Level Data Flow

```mermaid
flowchart LR
    subgraph writePath [Write Path - Async]
        UI1[Citizen UI] -->|POST _save| DS[upyog-draft-service]
        DS -->|Kafka| K[save-upyog-draft / update-upyog-draft]
        K --> P[egov-persister]
        P --> DB[(ug_draft_detail)]
    end

    subgraph readPath [Read Path - Sync JDBC]
        UI2[Login Dashboard] --> BFF[aggregation-service]
        BFF -->|POST _search / _count| DS2[upyog-draft-service]
        DS2 -->|JDBC SELECT| DB
    end

    subgraph submitPath [Submit Path]
        UI3[Citizen UI] --> MOD[tl-services / sv / adv / pt]
        MOD -->|persist real app| MODDB[(module DB + workflow)]
        MOD -->|best-effort POST _markSubmitted| DS3[upyog-draft-service]
        DS3 -->|Kafka| K2[update-upyog-draft-status]
        K2 --> P2[egov-persister]
        P2 --> DB
    end
```

### Why async writes + sync reads?

This matches the established UPYOG pattern used by PT drafts, SV, TL, and others:

- **Writes:** Service → Kafka → `egov-persister` → Postgres (decoupled, retryable)
- **Reads:** Service → JDBC (low latency for dashboard list/count)

---

## 6. Sequence Diagrams

### 6.1 Auto-save draft (any module)

```mermaid
sequenceDiagram
    actor Citizen
    participant UI as Citizen UI
    participant Draft as upyog-draft-service
    participant Kafka
    participant Persister as egov-persister
    participant DB as Postgres ug_draft_detail

    Citizen->>UI: Fills form (partial)
    UI->>UI: Debounce 30s
    UI->>Draft: POST /draft/v1/_save<br/>{businessService, draftData, draftId?}
    Draft->>Draft: Validate user from RequestInfo
    alt New draft
        Draft->>Kafka: save-upyog-draft
    else Existing draft
        Draft->>Kafka: update-upyog-draft
    end
    Draft-->>UI: 200 {draftId, completionPct}
    Kafka->>Persister: consume message
    Persister->>DB: INSERT / UPDATE

    Note over Draft,DB: Save is best-effort.<br/>Failure must NOT block citizen.
```

### 6.2 Final application submit (TL pilot)

```mermaid
sequenceDiagram
    actor Citizen
    participant UI as Citizen UI
    participant TL as tl-services
    participant WF as egov-workflow-v2
    participant Kafka as Kafka persister
    participant Draft as upyog-draft-service
    participant DB as ug_draft_detail

    Citizen->>UI: Click Submit
    UI->>TL: POST /v1/_create<br/>{Licenses, draftId}
    TL->>TL: Validate, enrich, calculate
    TL->>WF: Start workflow (if enabled)
    TL->>Kafka: save-tl-tradelicense
    TL->>Draft: POST /draft/v1/_markSubmitted<br/>{draftId} (best-effort)
    TL-->>UI: 200 Application created

    Draft->>Draft: Kafka: update-upyog-draft-status
    Note over Draft,DB: status = SUBMITTED

    alt Draft service unavailable
        TL->>TL: Submit still succeeds
        Note over Draft: Orphan ACTIVE draft remains;<br/>nightly reconciliation job fixes it
    end
```

### 6.3 Login dashboard — draft count + list

```mermaid
sequenceDiagram
    actor Citizen
    participant UI as Login Dashboard
    participant BFF as upyog-aggregation-service
    participant Draft as upyog-draft-service
    participant Inbox as Inbox V2
    participant DB as ug_draft_detail

    Citizen->>UI: Opens home after login
    UI->>BFF: POST /api/v1/aggregate<br/>{draft-applications, quick-summary}

    par Draft list
        BFF->>Draft: POST /draft/v1/_search<br/>status=ACTIVE, userUuid
        Draft->>DB: JDBC SELECT
        DB-->>Draft: rows
        Draft-->>BFF: {items: [{id, businessService, ...}]}
    and Draft count
        BFF->>Draft: POST /draft/v1/_count<br/>status=ACTIVE
        Draft->>DB: JDBC COUNT
        DB-->>Draft: count
        Draft-->>BFF: {count: N}
    and Submitted apps (unchanged)
        BFF->>Inbox: GET /inbox/v2/_count?status=ALL
        Inbox-->>BFF: applicationCount
    end

    BFF-->>UI: Merged response
    UI-->>Citizen: "3 drafts in progress" + list
```

### 6.4 Draft lifecycle & cleanup

```mermaid
stateDiagram-v2
    [*] --> ACTIVE: Citizen saves draft
    ACTIVE --> ACTIVE: Auto-save / update
    ACTIVE --> SUBMITTED: Domain service _markSubmitted
    ACTIVE --> DISCARDED: Citizen deletes draft
    SUBMITTED --> [*]: Hard delete after 7 days (purge job)
    DISCARDED --> [*]: Hard delete after 7 days (purge job)

    note right of ACTIVE
        90-day TTL:
        ACTIVE older than 90 days
        → marked DISCARDED
    end note
```

```mermaid
sequenceDiagram
    participant Scheduler as DraftCleanupScheduler
    participant Draft as upyog-draft-service
    participant Kafka
    participant Persister as egov-persister
    participant DB as ug_draft_detail

    Note over Scheduler: Nightly 2 AM (ShedLock)

    Scheduler->>Draft: purgeActiveOlderThan(90 days)
    Draft->>Kafka: update-upyog-draft-status (DISCARDED)
    Kafka->>Persister->>DB: UPDATE

    Scheduler->>Draft: purgeByStatusOlderThan(SUBMITTED, 7 days)
    Draft->>Kafka: delete-upyog-draft
    Kafka->>Persister->>DB: DELETE

    Scheduler->>Draft: reconcileOrphanedDrafts()
    Note over Draft: ACTIVE drafts with module_entity_id<br/>→ mark SUBMITTED
```

---

## 7. Component Responsibilities

```mermaid
C4Context
    title Container diagram - Draft service ecosystem

    Person(citizen, "Citizen", "Saves and resumes applications")
    System_Boundary(upyog, "UPYOG Platform") {
        Container(ui, "Citizen UI", "React", "Forms + login dashboard")
        Container(agg, "upyog-aggregation-service", "Go", "BFF - fan-in for dashboard")
        Container(draft, "upyog-draft-service", "Java 17 / Spring Boot 3.2.2", "Draft CRUD + lifecycle")
        Container(tl, "tl-services", "Java", "Trade license domain logic")
        Container(persister, "egov-persister", "Java", "Async DB writes from Kafka")
        ContainerDb(draftdb, "ug_draft_detail", "PostgreSQL", "JSONB draft payloads")
        ContainerQueue(kafka, "Kafka", "Event bus")
    }

    Rel(citizen, ui, "Uses")
    Rel(ui, draft, "Auto-save drafts")
    Rel(ui, tl, "Submit application")
    Rel(ui, agg, "Load dashboard")
    Rel(agg, draft, "List + count drafts")
    Rel(tl, draft, "_markSubmitted")
    Rel(draft, kafka, "Publish writes")
    Rel(kafka, persister, "Consume")
    Rel(persister, draftdb, "Persist")
    Rel(draft, draftdb, "Read search/count")
```

| Component | Responsibility | Must NOT do |
|-----------|----------------|-------------|
| **upyog-draft-service** | Store/retrieve drafts; lifecycle (ACTIVE/SUBMITTED/DISCARDED); TTL purge | Run module validation, workflow, or fee calculation |
| **Domain services (TL, SV, …)** | Validate & persist real applications; call `_markSubmitted` | Own per-module draft tables long-term |
| **upyog-aggregation-service** | Single `_search` + `_count` for dashboard | Fan out to 10+ module draft endpoints |
| **Citizen UI** | Auto-save to draft service; pass `draftId` on submit | Route draft saves through domain `_create` |
| **egov-persister** | Apply SQL from `upyog-draft-persister.yml` | Business logic |

---

## 8. Data Model

### Table: `ug_draft_detail`

```sql
CREATE TABLE ug_draft_detail (
    draft_id           VARCHAR(64)  PRIMARY KEY,
    tenant_id          VARCHAR(64)  NOT NULL,
    business_service   VARCHAR(64)  NOT NULL,   -- TL, SV, ADV, PT
    module_name        VARCHAR(64),             -- TL, WS, BPA, etc.
    module_entity_id   VARCHAR(64),             -- optional link after partial create
    creator_type       VARCHAR(32)  NOT NULL DEFAULT 'USER', -- USER or EMPLOYEE
    draft_data         JSONB        NOT NULL,   -- opaque module form JSON
    completion_pct     NUMERIC(5,2) DEFAULT 0,
    status             VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    createdby          VARCHAR(64)  NOT NULL,
    lastmodifiedby     VARCHAR(64),
    createdtime        BIGINT       NOT NULL,
    lastmodifiedtime   BIGINT
);
```

**Key design choice:** `draft_data` is **opaque JSONB**. The draft service does not parse TL vs PT schemas. Each module owns the shape of its payload; the draft service only stores and returns it.

---

## 9. API Contract

**Base path:** `/upyog-draft-service/draft/v1`

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/_save` | POST | Create or update draft (upsert by `draftId`) |
| `/_search` | POST | List drafts for authenticated user |
| `/_count` | POST | Count active drafts (login widget) |
| `/_delete` | POST | Citizen discards draft |
| `/_markSubmitted` | POST | Domain service calls after successful `_create` |

**Auth:** All operations scoped to `RequestInfo.userInfo.uuid` from JWT. Cross-user access rejected.

**Dashboard response shape (aggregation mapping):**

```json
{
  "items": [{
    "id": "uuid",
    "businessService": "TL",
    "applicationNumber": "uuid",
    "lastModifiedTime": 1720000000000,
    "completionPercentage": 45.0
  }]
}
```

---

## 10. Persister Integration

**Config file:** `upyog-draft-persister.yml`

| Kafka topic | SQL operation |
|-------------|-----------------|
| `save-upyog-draft` | INSERT |
| `update-upyog-draft` | UPDATE draft_data |
| `update-upyog-draft-status` | UPDATE status |
| `delete-upyog-draft` | DELETE (purge) |

Register this YAML in **egov-persister** deployment alongside `pt-drafts.yml`, `advertisement-service-persister.yml`, etc.

---

## 11. Why NOT Per-Service Draft Tables? (Options Comparison)

```mermaid
flowchart TB
    subgraph optionA [Option A - Centralized upyog-draft-service]
        A1[1 search API] --> A2[1 count API]
        A2 --> A3[1 cleanup policy]
    end

    subgraph optionB [Option B - Per-Service Draft Tables]
        B1[SV /_search?isDraft=true] --> B4[Merge in BFF]
        B2[ADV /_search?isDraft=true] --> B4
        B3[PT /drafts/_search] --> B4
        B5[TL new draft API] --> B4
        B4 --> B6[N concurrent calls + N mappers]
    end
```

| Dimension | Centralized `upyog-draft-service` | Per-service draft tables |
|-----------|-----------------------------------|--------------------------|
| Login dashboard query | **1 search + 1 count** | Fan-out to N services, merge/sort client-side |
| New module onboarding | Add `businessService` enum + client call | New table, migration, persister YAML, search API, BFF provider update |
| Consistency | One lifecycle, one cleanup policy | Drifts per team (SV hard-delete vs PT soft-deactivate) |
| Operational complexity | One more service to deploy | No new service, but N draft implementations |
| Failure isolation | Draft save is best-effort; submit never blocked | Same achievable, but duplicated in every module |
| State fork risk | Low — config-driven | High — each state may fork draft tables |
| Aligns with DIGIT principles | Yes — shared logic pooled | No — repeats per-module pattern |
| Migration effort | Medium — dual-write then cutover | Low for SV/ADV; still need TL + dashboard fix |

**Deciding factor:** The login-screen requirement is a **single-query cross-module problem**. That is the primary justification for a shared service.

---

## 12. Integration Pattern for Module Teams

### What each module team needs to do

```mermaid
flowchart LR
    A[1. UI auto-save to draft service] --> B[2. UI submit to domain _create with draftId]
    B --> C[3. Domain service calls _markSubmitted after save]
    C --> D[4. Optional dual-write during migration]
```

| Step | Owner | Action |
|------|-------|--------|
| 1 | **Frontend** | Debounced `POST /draft/v1/_save` with `{businessService, draftData}` |
| 2 | **Frontend** | On submit, call domain `/_create` with `draftId` in request body |
| 3 | **Backend (domain)** | After successful persist, best-effort `POST /draft/v1/_markSubmitted` (2s timeout, fire-and-forget) |
| 4 | **Backend (migration)** | Dual-write to old table + draft service behind feature flag until cutover |

### TL pilot (implemented)

- UI saves drafts directly to `upyog-draft-service`
- `TradeLicenseRequest.draftId` optional on submit
- `DraftServiceClient.markSubmitted()` called after `repository.save()` in `TradeLicenseService.create()`

### Critical rule for all modules

> **Draft operations are best-effort.** A draft-service outage must **never** block or roll back a real application submission.

---

## 13. Migration Roadmap

```mermaid
gantt
    title Draft Service Rollout
    dateFormat YYYY-MM-DD
    section Foundation
        Draft service + persister YAML     :done, a1, 2026-07-01, 2w
        Aggregation BFF wiring             :done, a2, after a1, 1w
    section Pilot
        TL integration                     :done, b1, after a2, 2w
        Login dashboard E2E                :b2, after b1, 1w
    section Rollout
        SV dual-write                      :c1, after b2, 2w
        ADV dual-write                     :c2, after c1, 1w
        PT dual-write                      :c3, after c2, 1w
    section Deprecation
        Migrate historical drafts          :d1, after c3, 1w
        Remove per-service draft tables    :d2, after d1, 2w
```

| Phase | Scope |
|-------|-------|
| **Phase 1** | Deploy draft service; point aggregation providers to it; TL pilot |
| **Phase 2** | SV/ADV/PT dual-write (old table + draft service, feature flag per tenant) |
| **Phase 3** | Dashboard reads from draft service only; stop writing to old tables |
| **Phase 4** | One-time historical migration script; drop deprecated tables after 2 release cycles |

---

## 14. Non-Functional Requirements

| Requirement | Implementation |
|-------------|----------------|
| **Auth** | Draft search/count scoped to JWT `user_uuid`; no cross-user access |
| **PII** | JSONB at rest; modules may pre-encrypt sensitive fields before storing (match module approach) |
| **TTL** | ACTIVE drafts > 90 days → DISCARDED; SUBMITTED/DISCARDED purged after 7 days |
| **Rate limiting** | UI debounce ~30s; server-side max 1 save/5s per `draftId` (recommended) |
| **Observability** | Metrics: save latency, submit-cleanup success rate, orphaned draft count |
| **Distributed scheduler** | ShedLock on `draftCleanupJob` — only one pod runs nightly purge |

---

## 15. Risks & Mitigations

| Risk | Mitigation |
|------|------------|
| Draft service down during submit | Submit succeeds; orphan draft reconciled by nightly job |
| Draft deleted when user meant "Save Draft" | UI must call draft service `_save`, not domain `_create` with wrong flag |
| Eventual consistency (Kafka lag) | Dashboard may briefly show stale count; acceptable for drafts; reads are JDBC so lag is write-side only |
| Module teams resist migration | Phased dual-write; no big-bang cutover |
| JSONB payload size | Set max request body; UI should not attach file blobs (use fileStoreId references) |
| State-level forks | One national service + config vs 15+ bespoke draft tables per state |

---

## 16. FAQ for Other Teams

**Q: Why not put drafts in Inbox V2 / Elasticsearch?**  
A: Drafts are high-churn, schema-less JSON blobs with no workflow state. Indexing them in ES adds cost and complexity with no benefit for a simple list/count on login. Inbox V2 stays for submitted applications.

**Q: Why not add draft APIs to `upyog-aggregation-service`?**  
A: Aggregation is a read-optimized Go BFF (Redis cache, circuit breakers). Citizen draft data is a write path requiring Postgres, persister, and lifecycle jobs — not a BFF concern.

**Q: Why not use PT's `/drafts` pattern in every module?**  
A: PT's approach works for PT alone. The login dashboard needs **one query across all modules**. N per-module endpoints means N network calls, N failure modes, and N response mappers in the BFF.

**Q: Does TL need its own draft table?**  
A: **No.** TL form JSON lives in `ug_draft_detail.draft_data`. TL only calls `_markSubmitted` after real create.

**Q: What happens to existing SV/ADV/PT draft tables?**  
A: Dual-write during migration, then deprecate after historical data is migrated and two release cycles pass.

**Q: Is draft save synchronous for the citizen?**  
A: The API returns immediately after Kafka publish. Persister writes async (standard UPYOG pattern). Reads for resume may need a short delay if user saves and immediately navigates away — mitigated by UI debounce and optimistic `draftId` in client state.

---

## 17. Decision Summary

| Question | Answer |
|----------|--------|
| One service or per-service APIs? | **One centralized `upyog-draft-service`** |
| Where does login dashboard read from? | Draft service `_search` + `_count` via aggregation BFF |
| What happens to draft on submit? | Domain service calls `_markSubmitted`; draft service marks SUBMITTED then purges |
| What about existing SV schema? | Migrate via dual-write; deprecate `eg_sv_street_vending_draft_detail` |
| Does TL need its own draft table? | **No** |
| Where do writes go? | Kafka → `egov-persister` → `ug_draft_detail` |
| Where do reads go? | Direct JDBC in draft service (sync, low latency) |

---

## 18. References (Codebase)

| Artifact | Path |
|----------|------|
| Draft service | `municipal-services/upyog-draft-service/` |
| Persister YAML | `municipal-services/upyog-draft-service/src/main/resources/upyog-draft-persister.yml` |
| Aggregation draft list | `upyog-aggregation-service/internal/providers/draft_applications.go` |
| Aggregation draft count | `upyog-aggregation-service/internal/providers/quick_summary.go` |
| TL markSubmitted client | `tl-services/src/main/java/org/egov/tl/repository/DraftServiceClient.java` |
| PT drafts (prior art) | `pt-services-v2/src/main/resources/pt-drafts.yml` |
| SV drafts (prior art) | `street-vending` — `isDraftApplication` on `_create` |

---

## 19. Discussion Agenda (Suggested Meeting)

1. **Problem recap** — login dashboard draft count/list (5 min)
2. **Architecture walkthrough** — diagrams §4–§6 (15 min)
3. **Options comparison** — §11 (10 min)
4. **Module integration contract** — §12 (10 min)
5. **Migration plan & timeline** — §13 (10 min)
6. **Open questions** — per-team concerns (10 min)

**Expected outcome:** Agreement on centralized draft service as platform standard; module teams commit to dual-write migration windows.
