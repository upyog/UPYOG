package org.egov.echallan.repository.rowmapper;


import org.egov.echallan.model.*;
import org.egov.echallan.model.Challan.StatusEnum;
import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;



@Component
@Slf4j
@SuppressWarnings("java:S2638")
public class ChallanRowMapper implements ResultSetExtractor<List<Challan>> {

    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    private final ObjectMapper mapper;

    public ChallanRowMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<Challan> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, Challan> challanMap = new LinkedHashMap<>();
        while (rs.next()) {
            String id = rs.getString("challan_id_alias");
            Challan currentChallan = challanMap.computeIfAbsent(id, key -> buildChallanUnchecked(rs, key));
            addAddressToChallan(rs, currentChallan);
            addDocumentToChallan(rs, currentChallan);

        }

        return new ArrayList<>(challanMap.values());

    }

    private Challan buildChallanUnchecked(ResultSet rs, String id) {
        try {
            return buildChallan(rs, id);
        } catch (SQLException e) {
            throw new CustomException("DB_ERROR", "Error while building challan from result set");
        }
    }

    private Challan buildChallan(ResultSet rs, String id) throws SQLException {
        Long lastModifiedTime = rs.getLong("challan_lastModifiedTime");
        if(rs.wasNull()){lastModifiedTime = null;}

        Long taxPeriodFrom = (Long) rs.getObject("taxperiodfrom");
        Long taxPeriodto = (Long) rs.getObject("taxperiodto");
        PGobject pgObj = (PGobject) rs.getObject("additionaldetail");
        AuditDetails auditdetails = AuditDetails.builder()
                .createdBy(rs.getString("challan_createdBy"))
                .createdTime(rs.getLong("challan_createdTime"))
                .lastModifiedBy(rs.getString("challan_lastModifiedBy"))
                .lastModifiedTime(lastModifiedTime)
                .build();
        try {
            BigDecimal challanAmount = rs.getBigDecimal("challan_amount");
            if(rs.wasNull()) { challanAmount = null; }

            Challan currentChallan = Challan.builder().auditDetails(auditdetails)
                    .accountId(rs.getString("uuid"))
                    .challanNo(rs.getString("challanno"))
                    .businessService(rs.getString("businessservice"))
                    .tenantId(rs.getString("tenantid"))
                    .referenceId(rs.getString("referenceid"))
                    .taxPeriodFrom(taxPeriodFrom)
                    .taxPeriodTo(taxPeriodto)
                    .description(rs.getString("description"))
                    .applicationStatus(StatusEnum.valueOf(rs.getString("applicationstatus")))
                    .challanStatus(rs.getString("challanStatus"))
                    .receiptNumber(rs.getString("receiptnumber"))
                    .filestoreid(rs.getString("filestoreid"))
                    .challanAmount(challanAmount)
                    .offenceTypeName(rs.getString("offence_type_name"))
                    .offenceCategoryName(rs.getString("offence_category_name"))
                    .offenceSubCategoryName(rs.getString("offence_subcategory_name"))
                    .id(id)
                    .build();
            if(pgObj != null){
                parseAdditionalDetail(pgObj, currentChallan, id);
            }
            return currentChallan;
        }
        catch (IOException e){
            throw new CustomException("PARSING ERROR","Error while parsing additionalDetail json");
        }
    }

    private void parseAdditionalDetail(PGobject pgObj, Challan currentChallan, String id) throws IOException {
        JsonNode additionalDetail = mapper.readTree(pgObj.getValue());
        currentChallan.setAdditionalDetail(additionalDetail);

        if(additionalDetail == null) {
            return;
        }
        parseAmountFromAdditionalDetail(additionalDetail, currentChallan, id);
        parseFeeWaiverFromAdditionalDetail(additionalDetail, currentChallan, id);
        parseCalculationFromAdditionalDetail(additionalDetail, currentChallan, id);
    }

    private void parseAmountFromAdditionalDetail(JsonNode additionalDetail, Challan currentChallan, String id) {
        if(!additionalDetail.has("amount")) {
            return;
        }
        JsonNode amountNode = additionalDetail.get("amount");
        if(amountNode == null || !amountNode.isArray()) {
            return;
        }
        try {
            List<Amount> amountList = mapper.convertValue(amountNode,
                mapper.getTypeFactory().constructCollectionType(List.class, Amount.class));
            currentChallan.setAmount(amountList);
        } catch (Exception e) {
            log.warn("Failed to parse amount array from additionalDetail for challan {}: {}", id, e.getMessage());
        }
    }

    private void parseFeeWaiverFromAdditionalDetail(JsonNode additionalDetail, Challan currentChallan, String id) {
        if(!additionalDetail.has("feeWaiver")) {
            return;
        }
        JsonNode feeWaiverNode = additionalDetail.get("feeWaiver");
        if(feeWaiverNode == null || feeWaiverNode.isNull()) {
            return;
        }
        try {
            BigDecimal feeWaiver = feeWaiverNode.decimalValue();
            currentChallan.setFeeWaiver(feeWaiver);
        } catch (Exception e) {
            log.warn("Failed to parse feeWaiver from additionalDetail for challan {}: {}", id, e.getMessage());
        }
    }

    private void parseCalculationFromAdditionalDetail(JsonNode additionalDetail, Challan currentChallan, String id) {
        if(!additionalDetail.has("calculation")) {
            return;
        }
        JsonNode calculationNode = additionalDetail.get("calculation");
        if(calculationNode == null || calculationNode.isNull()) {
            return;
        }
        try {
            org.egov.echallan.web.models.calculation.Calculation calculation =
                mapper.convertValue(calculationNode, org.egov.echallan.web.models.calculation.Calculation.class);
            currentChallan.setCalculation(calculation);
        } catch (Exception e) {
            log.warn("Failed to parse calculation from additionalDetail for challan {}: {}", id, e.getMessage());
        }
    }



    private void addAddressToChallan(ResultSet rs, Challan challan) throws SQLException {

        String tenantId = challan.getTenantId();

            Boundary locality = Boundary.builder().code(rs.getString("locality"))
                    .build();

            Double latitude = (Double) rs.getObject(LATITUDE);
            Double longitude = (Double) rs.getObject(LONGITUDE);

            Address address = Address.builder()
                    .buildingName(rs.getString("buildingName"))
                    .city(rs.getString("city"))
                    .detail(rs.getString("detail"))
                    .id(rs.getString("chaladdr_id"))
                    .landmark(rs.getString("landmark"))
                    .latitude(latitude)
                    .locality(locality)
                    .longitude(longitude)
                    .pincode(rs.getString("pincode"))
                    .doorNo(rs.getString("doorno"))
                    .street(rs.getString("street"))
                    .addressId(rs.getString("addressid"))
                    .addressNumber(rs.getString("addressnumber"))
                    .type(rs.getString("type"))
                    .addressLine1(rs.getString("addressline1"))
                    .addressLine2(rs.getString("addressline2"))
                    .tenantId(tenantId)
                    .build();

            challan.setAddress(address);

    }


    private void addDocumentToChallan(ResultSet rs, Challan challan) throws SQLException {
        String documentDetailId = rs.getString("document_detail_id");
        if(documentDetailId != null && !documentDetailId.isEmpty()) {
            Long docCreatedTime = rs.getLong("doc_createdtime");
            if(rs.wasNull()) { docCreatedTime = null; }
            Long docLastModifiedTime = rs.getLong("doc_lastmodifiedtime");
            if(rs.wasNull()) { docLastModifiedTime = null; }

            AuditDetails auditdetails = AuditDetails.builder()
                    .createdBy(rs.getString("doc_createdby"))
                    .createdTime(docCreatedTime)
                    .lastModifiedBy(rs.getString("doc_lastmodifiedby"))
                    .lastModifiedTime(docLastModifiedTime)
                    .build();

            List<DocumentDetail> existingDocs = challan.getUploadedDocumentDetails();
            if(existingDocs == null) {
                existingDocs = new ArrayList<>();
            }

            boolean exists = existingDocs.stream()
                .anyMatch(doc -> documentDetailId.equals(doc.getDocumentDetailId()));

            if(!exists) {
                DocumentDetail details = DocumentDetail.builder()
                        .documentDetailId(documentDetailId)
                        .challanId(rs.getString("challan_id"))
                        .documentType(rs.getString("document_type"))
                        .fileStoreId(rs.getString("filestore_id"))
                        .auditDetails(auditdetails)
                        .build();

                populateLocationFromAdditionalDetail(challan, details);

                existingDocs.add(details);
                challan.setUploadedDocumentDetails(existingDocs);
            } else {
                DocumentDetail existingDoc = existingDocs.stream()
                    .filter(doc -> documentDetailId.equals(doc.getDocumentDetailId()))
                    .findFirst()
                    .orElse(null);
                if(existingDoc != null) {
                    populateLocationFromAdditionalDetail(challan, existingDoc);
                }
            }
        }
    }

    /**
     * Populates latitude and longitude in document from challan's additionalDetail
     *
     * @param challan The challan object containing additionalDetail
     * @param document The document to populate with location data
     */
    private void populateLocationFromAdditionalDetail(Challan challan, DocumentDetail document) {
        if (challan.getAdditionalDetail() == null || document == null) {
            return;
        }

        try {
            if (challan.getAdditionalDetail() instanceof JsonNode additionalDetail) {
                populateLocationFromJsonNode(additionalDetail, document);
            } else if (challan.getAdditionalDetail() instanceof Map<?, ?> additionalDetailMap) {
                populateLocationFromMap(additionalDetailMap, document);
            }
        } catch (Exception e) {
            log.warn("Failed to populate location from additionalDetail for document {}: {}",
                document.getDocumentDetailId(), e.getMessage());
        }
    }

    private void populateLocationFromJsonNode(JsonNode additionalDetail, DocumentDetail document) {
        if (!additionalDetail.has(LATITUDE) || !additionalDetail.has(LONGITUDE)) {
            return;
        }
        if (document.getLatitude() == null) {
            document.setLatitude(additionalDetail.get(LATITUDE).asDouble());
        }
        if (document.getLongitude() == null) {
            document.setLongitude(additionalDetail.get(LONGITUDE).asDouble());
        }
    }

    private void populateLocationFromMap(Map<?, ?> additionalDetailMap, DocumentDetail document) {
        if (!additionalDetailMap.containsKey(LATITUDE) || !additionalDetailMap.containsKey(LONGITUDE)) {
            return;
        }
        if (document.getLatitude() == null && additionalDetailMap.get(LATITUDE) != null) {
            Object latObj = additionalDetailMap.get(LATITUDE);
            if (latObj instanceof Number number) {
                document.setLatitude(number.doubleValue());
            }
        }
        if (document.getLongitude() == null && additionalDetailMap.get(LONGITUDE) != null) {
            Object lngObj = additionalDetailMap.get(LONGITUDE);
            if (lngObj instanceof Number number) {
                document.setLongitude(number.doubleValue());
            }
        }
    }

}
