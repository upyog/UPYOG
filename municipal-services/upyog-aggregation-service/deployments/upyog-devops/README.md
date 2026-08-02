# Deploying upyog-aggregation-service via UPYOG-DevOps (niuatt)

This folder mirrors the layout of the **UPYOG-DevOps-niuatt** repository so the
files can be copied across mechanically. It follows the same conventions as the
existing municipal-services charts (e.g. `adv-services`, `cnd-service`): a thin
`Chart.yaml` + `values.yaml` that render through the shared `common` library
chart (`config-as-code/helm/charts/common`, v0.0.5).

## What to copy where

| From (this folder) | To (UPYOG-DevOps-niuatt repo) |
|---|---|
| `config-as-code/helm/charts/municipal-services/upyog-aggregation-service/` | same path (new folder) |
| `config-as-code/helm/environments/qa.yaml.additions` | merge its two blocks into `config-as-code/helm/environments/qa.yaml` |

```bash
# from the UPYOG monorepo root
cp -r municipal-services/upyog-aggregation-service/deployments/upyog-devops/config-as-code/helm/charts/municipal-services/upyog-aggregation-service \
      <UPYOG-DevOps-niuatt>/config-as-code/helm/charts/municipal-services/

# then hand-merge qa.yaml.additions into:
#   <UPYOG-DevOps-niuatt>/config-as-code/helm/environments/qa.yaml
```

## Container image

The monorepo's `build/build-config.yml` now contains a build entry
(`builds/upyog/municipal-services/upyog-aggregation-service`), so the standard
UPYOG Jenkins CI builds and pushes `<registry>/upyog-aggregation-service:<tag>`
(registry `nudmcdg` per qa.yaml `global.containerRegistry`) once the branch is
built. For a manual build:

```bash
cd municipal-services/upyog-aggregation-service
docker build -t nudmcdg/upyog-aggregation-service:<tag> .
docker push nudmcdg/upyog-aggregation-service:<tag>
```

## Deploy

Use the repo's standard deployer with the qa (niuatt) environment, the same way
other municipal services are deployed, e.g.:

```bash
cd <UPYOG-DevOps-niuatt>/deploy-as-code/deployer
go run main.go deploy -e qa "upyog-aggregation-service:<tag>"
```

## How the pieces fit (UPYOG standards)

- **Gateway routing** — `values.yaml` sets `ingress.zuul: true` +
  `context: upyog-aggregation-service`. The common chart then annotates the
  Kubernetes Service with `zuul/route-path: upyog-aggregation-service`; the
  gateway's route-discovery job (utilities/gateway-kubernetes-discovery) turns
  that into the route `Path=/upyog-aggregation-service/** →
  http://upyog-aggregation-service.egov:8080/`. The nginx ingress for the
  context sends public traffic to the gateway (`ingress.gateway: true` is the
  common-chart default).
- **Context path** — the gateway does *not* strip the route prefix, so the
  service serves every route under `/upyog-aggregation-service` (config key
  `server.contextPath`, also forced by the `UPYOG_SERVER_CONTEXTPATH` env in
  values.yaml). Probes and Prometheus annotations use the prefixed paths.
- **Authentication** — enforced by the gateway. DIGIT access tokens are opaque
  UUIDs (not JWTs), so in-service JWT validation is disabled in the qa profile;
  the service still captures the bearer token (TokenPassthrough middleware) and
  forwards it to every backend call (Authorization header +
  `RequestInfo.authToken`). Do **not** expose this service through a direct
  ingress (`ingress.gateway: false`) — that would bypass authentication.
- **Go, not Java** — `appType: ""` skips the common chart's Spring/Tomcat/Kafka
  env injection; there is no DB, so `initContainers.dbMigration.enabled: false`
  and no `-db` image exists.
- **Configuration** — the image bakes `configs/application-qa.yaml` (selected
  by `APP_ENV=qa`), which carries the niuatt in-cluster values: Redis at
  `redis.backbone:6379`, backends `egov-workflow-v2.egov`, `inbox.egov`,
  `billing-service.egov`, `tl-services.egov`, `egov-user-event.egov`,
  `adv-services.egov`, `upyog-draft-service.egov`. Any key can be overridden
  per environment with `UPYOG_*` env vars (Viper maps `UPYOG_A_B_C` → `a.b.c`),
  which is what `values.yaml` does for the deployment-sensitive ones.

## Smoke test after deploy

```bash
# health through the gateway/ingress
curl https://niuatt.niua.in/upyog-aggregation-service/health

# aggregate call (requires a valid DIGIT auth token)
curl -X POST https://niuatt.niua.in/upyog-aggregation-service/api/v1/aggregate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <access_token>" \
  -d '{
        "requestId": "f58c5b1d-2d53-4f8d-9cb3-1f5d6f0c2b45",
        "page": "citizen-home",
        "tenantId": "pg.citya",
        "requests": [
          { "provider": "new-applications",
            "pagination": { "page": 0, "size": 10 },
            "filters": { "sinceDays": 30 } }
        ]
      }'
```
