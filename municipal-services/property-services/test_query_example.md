# Assessment Plain Search with Date Range Filtering

## Updated Query Structure

With the new fromDate and toDate filtering capability, the assessment plain search query now supports date range filtering on the `createdtime` field.

## Full Query Example:

```sql
SELECT 
  asmt.id as ass_assessmentid, 
  asmt.financialyear as ass_financialyear, 
  asmt.tenantId as ass_tenantid, 
  asmt.assessmentNumber as ass_assessmentnumber,
  asmt.status as ass_status, 
  asmt.propertyId as ass_propertyid, 
  asmt.source as ass_source, 
  asmt.assessmentDate as ass_assessmentdate,
  asmt.additionalDetails as ass_additionaldetails, 
  asmt.createdby as ass_createdby, 
  asmt.createdtime as ass_createdtime, 
  asmt.lastmodifiedby as ass_lastmodifiedby,
  asmt.lastmodifiedtime as ass_lastmodifiedtime, 
  us.tenantId as us_tenantid, 
  us.unitId as us_unitid, 
  us.id as us_id, 
  us.assessmentId as us_assessmentid,
  us.usageCategory as us_usagecategory, 
  us.occupancyType as us_occupancytype,
  us.occupancyDate as us_occupancydate, 
  us.active as us_active, 
  us.createdby as us_createdby,
  us.createdtime as us_createdtime, 
  us.lastmodifiedby as us_lastmodifiedby, 
  us.lastmodifiedtime as us_lastmodifiedtime,
  doc.id as doc_id, 
  doc.entityid as doc_entityid, 
  doc.documentType as doc_documenttype, 
  doc.fileStoreId as doc_filestoreid, 
  doc.documentuid as doc_documentuid,
  doc.status as doc_status, 
  doc.tenantid as doc_tenantid,
  doc.createdby as doc_createdby, 
  doc.createdtime as doc_createdtime, 
  doc.lastmodifiedby as doc_lastmodifiedby, 
  doc.lastmodifiedtime as doc_lastmodifiedtime
FROM eg_pt_asmt_assessment asmt 
LEFT OUTER JOIN eg_pt_asmt_unitusage us ON asmt.id = us.assessmentId 
LEFT OUTER JOIN eg_pt_asmt_document doc ON asmt.id = doc.entityid
WHERE asmt.tenantid = :tenantid
AND asmt.createdtime BETWEEN :fromdate AND :todate
ORDER BY asmt.createdtime DESC
```

## Plain Search Assessment Numbers Query:

For cases where specific assessment numbers, IDs, or property IDs are not provided:

```sql
SELECT assessmentnumber 
FROM eg_pt_asmt_assessment
WHERE tenantid = :tenantid
AND createdtime BETWEEN :fromdate AND :todate
ORDER BY createdtime, id 
OFFSET :offset LIMIT :limit
```

## API Usage Example:

```
POST /property-assessments/_plainsearch

{
  "RequestInfo": {...},
  "tenantId": "pg.citya",
  "fromDate": 1640995200000,  // Jan 1, 2022 00:00:00 GMT
  "toDate": 1672531199000,    // Dec 31, 2022 23:59:59 GMT
  "limit": 10,
  "offset": 0
}
```

## Changes Made:

1. **AssessmentSearchCriteria.java**: Added `fromDate` and `toDate` fields
2. **AssessmentQueryBuilder.java**: Added date range filtering logic in `addWhereClause()` method
3. **AssessmentRepository.java**: Updated `fetchAssessmentNumbers()` to support date filtering
4. **AssessmentService.java**: Updated `getAssessmenPlainSearch()` to pass through date criteria

## Key Features:

- **Date Field**: Filters on `asmt.createdtime` (assessment creation time)
- **Default Behavior**: If only `fromDate` is provided, `toDate` defaults to current timestamp
- **Validation**: If only `toDate` is provided without `fromDate`, throws "From Date should be mentioned first" error
- **Table Used**: `eg_pt_asmt_assessment` table's `createdtime` column for date filtering