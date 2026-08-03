# upyog-draft-service

Centralized draft storage for UPYOG municipal service applications (TL, SV, ADV, PT).

## Stack

- Java 17
- Spring Boot 3.2.2
- PostgreSQL (JSONB draft payloads)

## API

Base path: `/upyog-draft-service/draft/v1`

| Endpoint | Purpose |
|----------|---------|
| `POST /_save` | Upsert draft (create or update by draftId) |
| `POST /_search` | List drafts for authenticated user |
| `POST /_count` | Count active drafts (login widget) |
| `POST /_delete` | Discard draft |
| `POST /_markSubmitted` | Mark draft submitted after real application create |

## Swagger / OpenAPI

After starting the service locally:

| Resource | URL |
|----------|-----|
| Swagger UI | `http://localhost:8095/upyog-draft-service/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8095/upyog-draft-service/v3/api-docs` |

Uses **SpringDoc OpenAPI 2.3.0** (`springdoc-openapi-starter-webmvc-ui`), consistent with other Spring Boot 3.2 UPYOG services.

## TL UI auto-save

Citizen TL forms should auto-save directly to this service (not via tl-services `_create`):

```
POST /upyog-draft-service/draft/v1/_save
{
  "RequestInfo": { "userInfo": { "uuid": "<citizen-uuid>" } },
  "Draft": {
    "draftId": "<optional-existing-id>",
    "tenantId": "pg.citya",
    "businessService": "TL",
    "draftData": { ... full TL form JSON ... },
    "completionPct": 45.0
  }
}
```

On final submit, the TL UI calls `tl-services/v1/_create` with `"draftId": "<draft-id>"` in the request body. TL marks the draft submitted via `_markSubmitted` after a successful create.

## Scheduled jobs

- Nightly cleanup (2 AM): 90-day TTL for ACTIVE drafts, purge SUBMITTED/DISCARDED after retention period, reconcile orphaned ACTIVE drafts with `module_entity_id`.

## Persister

Writes are async via `egov-persister` using `src/main/resources/upyog-draft-persister.yml`.

| Kafka topic | Operation |
|-------------|-----------|
| `save-upyog-draft` | INSERT new draft |
| `update-upyog-draft` | UPDATE draft data |
| `update-upyog-draft-status` | UPDATE status (submitted/discarded) |
| `delete-upyog-draft` | Hard delete (purge) |

Register the YAML in `egov-persister` alongside other municipal service persister configs.

---

## Why this approach (vs per-module drafts & workflow INITIATED state)

### The alternative: save incomplete applications in the main table with workflow `INITIATE`

In many UPYOG modules, workflow defines an **`INITIATE` → `INITIATED`** start state (e.g. TL `NewTL` workflow). A common pattern is to call domain `/_create` early so the incomplete form lands in the **real application table** with status `INITIATED`, effectively using workflow state as “draft.”

| Aspect | Workflow INITIATED in main table | Centralized `upyog-draft-service` |
|--------|----------------------------------|-----------------------------------|
| **When workflow starts** | On first partial save | Only on final submit |
| **Application number / ID** | Generated early via idgen | Generated only when application is real |
| **Inbox / ES indexing** | Partial apps appear in inbox & reports | Drafts stay out of inbox until submit |
| **Validation & side effects** | Full create path: user creation, calc, MDMS, notifications | Light validation on JSONB save only |
| **Login dashboard (cross-module)** | Still need N inbox/search calls per module | Single `_search` + `_count` |
| **Cleanup on abandon** | Orphan INITIATED rows in production tables | TTL + status lifecycle in one place |
| **Citizen UX** | “My applications” mixes drafts and submitted apps | Clear split: drafts vs submitted |
| **Module coupling** | Each module re-implements partial save differently | One contract for all modules |

**Why draft service is better for UPYOG’s login-screen requirement:**

1. **Cross-module list/count is one query** — INITIATED rows in TL, SV, ADV, PT are still in four different tables/schemas; aggregation would fan out to every service.
2. **No false applications** — INITIATED records look like real applications in inbox, dashboards, and MIS reports until someone cleans them up.
3. **Submit is a deliberate act** — workflow, fees, and notifications run once, when the citizen is ready, not on every auto-save.
4. **Simpler mental model** — `draftId` = in-progress form; `applicationNumber` = submitted application. Two different lifecycles, two different stores.

Use **workflow INITIATED** when a module explicitly needs a workflow instance before the form is complete (rare). Use **draft service** for citizen “save & resume later” and the login widget.

---

## Draft safety: not deleted before data is saved

### Separation of save vs submit (most important rule)

| User action | API to call | Draft status after |
|-------------|-------------|-------------------|
| Auto-save / Save draft | `POST /draft/v1/_save` | Stays **ACTIVE** |
| Discard draft | `POST /draft/v1/_delete` | **DISCARDED** (explicit only) |
| Final submit | Domain `/_create` → then `_markSubmitted` | **SUBMITTED** (only after real app saved) |

**Draft is never deleted on `_save`.** Deletion or SUBMITTED status happens only through `_delete`, `_markSubmitted`, TTL job, or purge — never through the save endpoint.

TL integration enforces submit ordering:

```text
tl-services/_create  →  repository.save (Kafka → main TL tables)  →  draft _markSubmitted (best-effort)
```

If TL create fails, `_markSubmitted` is **not** called — draft remains ACTIVE for resume.

### Edge cases and mitigations

| Edge case | What happens | Mitigation |
|-----------|--------------|------------|
| UI calls `_markSubmitted` on save by mistake | Draft marked SUBMITTED while citizen still editing | **UI contract:** only domain service calls `_markSubmitted` after successful `_create`; never on auto-save |
| UI calls domain `_create` with `isDraftApplication=true` (SV/ADV pattern) while also using draft service | Double storage / wrong cleanup | Migration: turn off inline draft flag; use draft service for save, domain `_create` only on submit |
| Draft service down during submit | Application still created; draft stays ACTIVE | Best-effort `_markSubmitted`; nightly reconciliation marks orphans SUBMITTED |
| Draft service down during auto-save | Save fails; citizen sees error | Best-effort save in UI (retry/backoff); must not block navigation; optional localStorage backup of `draftId` + form |
| Persister lag (Kafka async write) | API returns `draftId` before row visible in DB | UI keeps `draftId` in client state; retry `_save` with same id; reads may lag ms–seconds — acceptable for drafts |
| Citizen submits without `draftId` | No draft cleanup | Allowed — `_markSubmitted` skipped; no orphan issue |
| Duplicate submit (double-click) | Two applications if domain allows | Domain idempotency / UI disable submit; draft `_markSubmitted` is idempotent |
| `_markSubmitted` fails after successful create | ACTIVE draft remains | Reconciliation job; manual compare by user + module + time window |
| 90-day TTL job | Old ACTIVE → DISCARDED | Only `lastmodifiedtime` > 90 days; active editors refreshed by auto-save |
| Citizen deletes draft then submits old form | Stale UI | UI clears local state on `_delete`; validate draft exists on resume |

### Async persister write flow

```text
_save API  →  returns draftId immediately  →  Kafka  →  egov-persister  →  Postgres
```

Recommendations for teams:

- **Frontend:** persist `draftId` in component state / sessionStorage as soon as `_save` returns; send same `draftId` on every subsequent save.
- **Backend:** do not call `_markSubmitted` until domain persist succeeds (TL: after `repository.save`).
- **Ops:** monitor persister consumer lag on `save-upyog-draft` / `update-upyog-draft` topics.

---

## Draft ID vs application ID (no collision)

Draft and application identifiers are ** intentionally separate namespaces**:

| Identifier | Generated by | When | Used for |
|------------|--------------|------|----------|
| **`draftId`** | `upyog-draft-service` (UUID on first `_save`) | First auto-save | Resume form, `_save` updates, `_markSubmitted` |
| **`applicationNumber`** | Domain service (idgen) | Final `_create` | Receipts, workflow, inbox, payments |
| **`module_entity_id`** (optional) | Domain service | Partial create if ever needed | Link draft row to domain PK — optional |

**Rules:**

1. **First `_save` without `draftId`** → service assigns UUID → returned in response → UI stores it.
2. **Every later `_save`** → UI sends same `draftId` → `update-upyog-draft` topic (same row, no new id).
3. **Final submit** → UI sends `draftId` inside `TradeLicenseRequest` (or equivalent) **and** domain service creates **new** `applicationNumber` — unrelated to `draftId`.
4. **`draftId` is never reused** as application number and **applicationNumber is never used** as `draftId`.

Example TL flow:

```text
Save 1:  _save {}           → draftId = "a1b2c3..."
Save 2:  _save {draftId}    → update same row
Submit:  _create {draftId}  → applicationNumber = "TL-2026-000123"
         _markSubmitted {draftId} → draft status SUBMITTED (cleanup)
```

Citizen resume link uses `draftId`; citizen tracking after submit uses `applicationNumber`.

---

## Summary: opting for centralized draft service

| Requirement | How draft service addresses it |
|-------------|--------------------------------|
| Login screen count + list across modules | Single `_count` / `_search` |
| Don’t lose partial form data | `_save` with stable `draftId` |
| Don’t delete draft before data saved | Save ≠ submit; `_markSubmitted` only after domain persist |
| Don’t pollute inbox/reports with partial apps | Drafts outside workflow & inbox |
| Don’t start SLA / notifications on auto-save | No workflow until final `_create` |
| Consistent cleanup | One TTL + reconciliation policy |
| TL / SV / ADV / PT alignment | Same API; module owns JSON in `draft_data` |

For full architecture diagrams and team discussion material, see [ARCHITECTURE.md](./ARCHITECTURE.md).

