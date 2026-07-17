# Adapter Service

The `adapter-service` is a core utility module designed to normalize, validate, and load daily municipal metrics from UPYOG business services into the National Dashboard.

## Architecture

The pipeline processes data as follows:
```
Raw Business Data -> ModuleTransformer -> DashboardPayload -> CommonValidator -> Loader
```

### Components
1. **Model**:
   * `DashboardData`: Represents the metrics block for a single module, date, and ULB.
   * `DashboardPayload`: A list wrapper of `DashboardData` objects.
   * `NationalDashboardIngestRequest`: The outer HTTP request envelope sent to the National Dashboard API.
2. **Validator**:
   * `CommonValidator`: Performs basic context verification (checks that tenant ID, module, date, ward, ulb, region, and state are present).
   * `ModuleValidator` / `PTValidator`: Executes module-specific checks (e.g. validating all 16 Property Tax metric keys exist).
3. **Loader**:
   * `HttpLoader`: Handles OAuth2 token retrieval and uploads the serialized JSON payloads to the National Dashboard.

## Jackson Serialization & Case Handling

The National Dashboard API contract requires capitalized keys (e.g. `"RequestInfo"`, `"Data"`). Standard Java camelCase fields combined with Lombok's `@Data` can cause Jackson to serialize duplicate lowercase keys if the field and JavaBean conventions conflict.

To prevent duplicate fields, all model properties are defined in lowercase camelCase (e.g., `requestInfo`, `data`), and mapped using `@JsonProperty`:
```java
@JsonProperty("RequestInfo")
private RequestInfo requestInfo;

@JsonProperty("Data")
private List<DashboardData> data;
```
This forces Jackson to serialize only the uppercase keys.
