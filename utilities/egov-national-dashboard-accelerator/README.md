# egov-national-dashboard-accelerator

An Apache Airflow-based ETL pipeline that extracts civic service metrics from Elasticsearch, transforms them, and ingests them into the UPYOG National Dashboard via the ingest API.

---

## Folder Structure

```
egov-national-dashboard-accelerator/
├── dags/
│   ├── queries/                          # Per-module ES query definitions + payload templates
│   │   ├── pt.py                         # Property Tax queries & empty payload
│   │   ├── tl.py                         # Trade License
│   │   ├── pgr.py                        # Public Grievance Redressal
│   │   ├── ws.py                         # Water & Sewerage
│   │   ├── firenoc.py                    # Fire NOC
│   │   ├── mcollect.py                   # Miscellaneous Collections
│   │   ├── obps.py                       # Online Building Plan Scrutiny
│   │   └── common.py                     # Cross-module ULB liveness metrics
│   ├── utils/
│   │   └── utils.py                      # Kibana logging helper
│   ├── national_dashboard_template_latest.py     # Manual-trigger DAG (PT only, active)
│   └── national_dashboard_template_scheduled.py  # Scheduled DAG (all modules, daily 02:00 UTC)
└── plugins/
    └── hooks/
        └── elastic_hook.py               # Custom Airflow hook wrapping Elasticsearch HTTP calls
```

---

## Architecture Overview

```
Elasticsearch  ──►  dump_kibana()  ──►  XCom  ──►  load()  ──►  UPYOG Ingest API
   (extract)         (transform)                   (load)
```

Each module follows a strict 3-task pipeline per DAG run:

```
extract_{module}  >>  transform_{module}  >>  load_{module}
```

| Task | Function | What it does |
|---|---|---|
| `extract_{module}` | `dump_kibana()` | Runs ES queries, transforms aggregations into ward-level payloads, pushes to XCom |
| `transform_{module}` | `transform()` | Placeholder — add custom transformations here |
| `load_{module}` | `load()` | Pulls XCom payload, authenticates with UPYOG, calls ingest API in batches of 50 |

---

## The Two DAGs

### 1. `national_dashboard_template_manual` — Manual Trigger DAG

**File:** `dags/national_dashboard_template_latest.py`

- `schedule=None` — never runs automatically, only on explicit trigger
- Active modules: **PT only** (others commented out)
- Accepts an optional `date` in `dag_run.conf`; falls back to **yesterday (IST)** if not provided

**How to trigger manually:**

Via Airflow UI:
1. Go to DAGs → `national_dashboard_template_manual`
2. Click **Trigger DAG ▶**
3. Optionally provide JSON config:
```json
{ "date": "15-01-2025" }
```
4. If no date is passed, it defaults to yesterday in `Asia/Kolkata` timezone

Via Airflow CLI:
```bash
# Trigger for a specific date
airflow dags trigger national_dashboard_template_manual --conf '{"date": "15-01-2025"}'

# Trigger with no conf (uses yesterday)
airflow dags trigger national_dashboard_template_manual
```

Via REST API:
```bash
curl -X POST "http://<airflow-host>/api/v1/dags/national_dashboard_template_manual/dagRuns" \
  -H "Content-Type: application/json" \
  -u admin:admin \
  -d '{"conf": {"date": "15-01-2025"}}'
```

---

### 2. `national_dashboard_template_scheduled` — Scheduled DAG

**File:** `dags/national_dashboard_template_scheduled.py`

- `schedule='0 2 * * *'` — runs automatically every day at **02:00 UTC**
- `catchup=False` — skips missed runs on startup
- Active modules: **TL, PGR, WS, PT, FIRENOC, MCOLLECT**
- Always processes **yesterday's date** (hardcoded via `date.today() - timedelta(days=1)`)
- Does NOT accept a `date` conf — date is always computed at runtime

**Cron schedule:** `0 2 * * *`
| Field | Value | Meaning |
|---|---|---|
| Minute | 0 | at minute 0 |
| Hour | 2 | at 02:00 UTC |
| Day | * | every day |
| Month | * | every month |
| Weekday | * | every weekday |

To change the schedule, edit the `schedule` parameter in the DAG definition:
```python
# Every day at 07:30 UTC
schedule='30 7 * * *'

# Every Monday at midnight
schedule='0 0 * * 1'
```

---

## PT Module — End-to-End Example

### What PT collects

The PT (Property Tax) module runs **8 Elasticsearch queries** against two indices:

| Query | ES Index | Metric Collected |
|---|---|---|
| `pt_closed_applications` | `property-services` | `todaysClosedApplications` |
| `pt_total_applications` | `property-services` | `todaysTotalApplications` |
| `pt_collection_transactions_by_usage` | `dss-collection_v2` | `todaysCollection`, `transactions` (by usage category) |
| `pt_collection_taxes` | `dss-collection_v2` | `propertyTax`, `rebate`, `penalty`, `interest` (by usage category) |
| `pt_collection_cess` | `dss-collection_v2` | `cess` (FireCess + CancerCess) |
| `pt_assessed_properties` | `property-services` | `assessedProperties` (by usage category) |
| `pt_properties_registered_by_year` | `property-assessments` | `propertiesRegistered` (by financial year) |
| `pt_properties_assessments` | `property-assessments` | `assessments` |

### Step-by-step flow for PT on 15-Jan-2025

**Step 1 — `extract_pt` task calls `dump_kibana(module='PT')`**

Computes time window:
```
date = "15-01-2025"
start = 1736899200000  (2025-01-15 00:00:00 IST in epoch ms)
end   = 1736985599000  (2025-01-15 23:59:59 IST in epoch ms)
```

Runs each of the 8 PT queries with `{0}=start, {1}=end` substituted into the ES query template.

Example ES query sent for `pt_total_applications`:
```json
{
  "size": 0,
  "query": {
    "bool": {
      "must_not": [{ "term": { "Data.tenantId.keyword": "pb.testing" } }],
      "must": [{
        "range": {
          "Data.@timestamp": { "gte": 1736899200000, "lte": 1736985599000, "format": "epoch_millis" }
        }
      }]
    }
  },
  "aggs": {
    "ward": {
      "terms": { "field": "Data.ward.name.keyword", "size": 10000 },
      "aggs": {
        "ulb": {
          "terms": { "field": "Data.tenantId.keyword", "size": 10000 },
          "aggs": {
            "region": {
              "terms": { "field": "Data.tenantData.city.name.keyword", "size": 10000 },
              "aggs": {
                "todaysTotalApplications": { "value_count": { "field": "Data.propertyId.keyword" } }
              }
            }
          }
        }
      }
    }
  }
}
```

**Step 2 — `transform_response_sample()` builds ward-level payloads**

For each query result, iterates over `ward → ulb → region` buckets and calls the query's `lambda` function to populate metrics. Uses `empty_pt_payload()` as the base structure for each unique `ward|ulb` key.

Example output payload for one ward:
```json
{
  "date": "15-01-2025",
  "module": "PT",
  "ward": "Ward 10",
  "ulb": "pb.amritsar",
  "region": "Amritsar",
  "state": "Punjab",
  "metrics": {
    "assessments": 42,
    "todaysTotalApplications": 15,
    "todaysClosedApplications": 8,
    "propertiesRegistered": [
      { "groupBy": "financialYear", "buckets": [
        { "name": "2022-23", "value": 120 },
        { "name": "2023-24", "value": 95 }
      ]}
    ],
    "assessedProperties": [
      { "groupBy": "usageCategory", "buckets": [
        { "name": "RESIDENTIAL", "value": 300 },
        { "name": "COMMERCIAL", "value": 45 }
      ]}
    ],
    "transactions": [
      { "groupBy": "usageCategory", "buckets": [
        { "name": "RESIDENTIAL", "value": 10 },
        { "name": "COMMERCIAL", "value": 3 }
      ]}
    ],
    "todaysCollection": [
      { "groupBy": "usageCategory", "buckets": [
        { "name": "RESIDENTIAL", "value": 52000.0 },
        { "name": "COMMERCIAL", "value": 18000.0 }
      ]}
    ],
    "propertyTax": [{ "groupBy": "usageCategory", "buckets": [{ "name": "RESIDENTIAL", "value": 48000.0 }] }],
    "cess":    [{ "groupBy": "usageCategory", "buckets": [{ "name": "RESIDENTIAL", "value": 2400.0 }] }],
    "rebate":  [{ "groupBy": "usageCategory", "buckets": [{ "name": "RESIDENTIAL", "value": 1200.0 }] }],
    "penalty": [{ "groupBy": "usageCategory", "buckets": [{ "name": "RESIDENTIAL", "value": 600.0 }] }],
    "interest":[{ "groupBy": "usageCategory", "buckets": [{ "name": "RESIDENTIAL", "value": 300.0 }] }]
  }
}
```

This list of ward payloads is pushed to **XCom** under key `payload_PT`.

**Step 3 — `transform_pt` task**

Currently a no-op placeholder. Add any post-processing logic here.

**Step 4 — `load_pt` task calls `load(module='PT')`**

1. Fetches OAuth token from UPYOG (`user/oauth/token`) using Airflow connection `digit-auth`
2. Pulls the ward payload list from XCom (`payload_PT`)
3. Sends to `national-dashboard/metric/_ingest` in batches of 50 records
4. (Manual DAG only) Logs each response to `adaptor_logs` index in Elasticsearch

---

## Airflow Connections & Variables Required

### Connections (set in Airflow UI → Admin → Connections)

| Connection ID | Type | Purpose |
|---|---|---|
| `es_conn` | HTTP | Elasticsearch host for querying indices |
| `digit-auth` | HTTP | UPYOG platform host for OAuth + ingest API |

### Variables (set in Airflow UI → Admin → Variables)

| Variable | Purpose |
|---|---|
| `username` | UPYOG login username |
| `password` | UPYOG login password |
| `tenantid` | UPYOG tenant ID (e.g. `pb`) |
| `usertype` | UPYOG user type (e.g. `EMPLOYEE`) |
| `token` | Base64 Basic Auth token for OAuth endpoint |
| `totalulb_url` | URL to fetch total ULB count JSON |

---

## Adding a New Module

1. Create `dags/queries/mymodule.py` with:
   - ES query dicts (each with `path`, `name`, `lambda`, `query`)
   - `mymodule_queries` list
   - `empty_mymodule_payload(region, ulb, ward, date)` function

2. Add to `module_map` in the DAG file:
```python
'MYMODULE': (mymodule_queries, empty_mymodule_payload)
```

3. Add the 3 tasks and chain them:
```python
extract_mymodule = PythonOperator(task_id='elastic_search_extract_mymodule', python_callable=dump_kibana, op_kwargs={'module': 'MYMODULE'}, dag=dag)
transform_mymodule = PythonOperator(task_id='nudb_transform_mymodule', python_callable=transform, dag=dag)
load_mymodule = PythonOperator(task_id='nudb_ingest_load_mymodule', python_callable=load, op_kwargs={'module': 'MYMODULE'}, dag=dag)

extract_mymodule >> transform_mymodule >> load_mymodule
```

---

## Key Design Decisions

- **XCom for inter-task data passing** — the extract task serializes the full ward payload list as JSON and pushes it; the load task pulls it back. Keep payload sizes reasonable (batch_size=50 guards the ingest API).
- **Timezone handling** — all date windows are computed in `Asia/Kolkata` (IST) and converted to epoch milliseconds for ES queries.
- **`pb.testing` exclusion** — all ES queries explicitly exclude the test tenant to avoid polluting dashboard metrics.
- **Manual DAG date flexibility** — pass `{"date": "DD-MM-YYYY"}` in conf to backfill any specific date; omit it to default to yesterday.
- **Scheduled DAG always uses yesterday** — no conf accepted; date is always `date.today() - timedelta(days=1)` at runtime.



## Development Note

Any changes made to the DAG files or query modules under `dags/` are picked up by Airflow **directly** — no build or deployment step is required. Airflow reads the DAG files from the filesystem on each scheduler cycle, so edits take effect on the next DAG run automatically.
