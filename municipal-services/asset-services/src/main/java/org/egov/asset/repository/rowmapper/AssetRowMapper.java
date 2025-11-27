package org.egov.asset.repository.rowmapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.egov.asset.web.models.*;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class AssetRowMapper implements ResultSetExtractor<List<Asset>> {

    @Autowired
    private final ObjectMapper objectMapper;

    public AssetRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * extract the data from the resultset and prepare the BPA Object
     *
     * @see org.springframework.jdbc.core.ResultSetExtractor#extractData(java.sql.ResultSet)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List<Asset> extractData(ResultSet rs) throws SQLException, DataAccessException {

        Map<String, Asset> assetMap = new LinkedHashMap<>();

        while (rs.next()) {
            String id = rs.getString("id");
            String tenantId = rs.getString("tenantid");

            Asset currentAsset = assetMap.get(id);
            if (currentAsset == null) {

                currentAsset = Asset.builder()
                        .id(id)
                        .tenantId(tenantId)

                        .assetName(rs.getString("name"))
                        .description(rs.getString("description"))
                        .assetClassification(rs.getString("classification"))
                        .assetParentCategory(rs.getString("parentcategory"))
                        .assetCategory(rs.getString("category"))
                        .assetSubCategory(rs.getString("subcategory"))

                        .applicationNo(rs.getString("applicationno"))
                        .approvalNo(rs.getString("approvalno"))
                        .approvalDate(rs.getLong("approvaldate"))
                        .applicationDate(rs.getLong("applicationdate"))
                        .status(rs.getString("status"))
                        .accountId(rs.getString("accountid"))
                        .remarks(rs.getString("remarks"))

                        .acquisitionCost(rs.getDouble("acquisitioncost"))

                        .purchaseDate(rs.getLong("purchasedate"))
                        .bookRefNo(rs.getString("bookrefno"))

                        .assetType(rs.getString("assettype"))
                        .division(rs.getString("division"))
                        .district(rs.getString("district"))
                        .unitOfMeasurement(rs.getLong("unitofmeasurement"))

                        .build();

                assetMap.put(id, currentAsset);
            }
            addChildrenToProperty(rs, currentAsset);
        }

        return new ArrayList<>(assetMap.values());
    }

    private void addChildrenToProperty(ResultSet rs, Asset asset) throws SQLException {
        Document document = new Document();
        // Mapping AddressDetails
        Address addressDetails = new Address();
        addressDetails.setAddressLine1(rs.getString("addressline1"));
        addressDetails.setAddressLine2(rs.getString("addressline2"));
        addressDetails.setTenantId(rs.getString("tenantid"));
        addressDetails.setDoorNo(rs.getString("doorno"));
        addressDetails.setLatitude(rs.getDouble("latitude"));
        addressDetails.setLongitude(rs.getDouble("longitude"));
        addressDetails.setAddressId(rs.getString("addressid"));
        addressDetails.setAddressNumber(rs.getString("addressnumber"));
        addressDetails.setType(rs.getString("type"));
        addressDetails.setLandmark(rs.getString("landmark"));
        addressDetails.setCity(rs.getString("city"));
        addressDetails.setPincode(rs.getString("pincode"));
        addressDetails.setDetail(rs.getString("detail"));
        addressDetails.setBuildingName(rs.getString("buildingname"));
        addressDetails.setStreet(rs.getString("street"));

        //Mapping locality in addressDetails
        Boundary locality = new Boundary();
        locality.setCode(rs.getString("locality_code"));
        locality.setName(rs.getString("locality_name"));
        locality.setLabel(rs.getString("locality_label"));
        locality.setLatitude(rs.getString("locality_latitude"));
        locality.setLongitude(rs.getString("locality_longitude"));
        addressDetails.setLocality(locality);
        asset.setAddressDetails(addressDetails);

        // Mapping AuditDetails
        AuditDetails auditDetails = new AuditDetails();
        auditDetails.setCreatedBy(rs.getString("createdby"));
        auditDetails.setCreatedTime(rs.getLong("createdtime"));
        auditDetails.setLastModifiedBy(rs.getString("lastmodifiedby"));
        auditDetails.setLastModifiedTime(rs.getLong("lastmodifiedtime"));
        asset.setAuditDetails(auditDetails);

        // Mapping AssignmDetails
        AssetAssignment assetAssignment = new AssetAssignment();
        assetAssignment.setAssignmentId(rs.getString("assignmentid"));
        assetAssignment.setAssignedUserName(rs.getString("assignedusername"));
        assetAssignment.setEmployeeCode(rs.getString("employeecode"));
        assetAssignment.setDesignation(rs.getString("designation"));
        assetAssignment.setDepartment(rs.getString("department"));
        assetAssignment.setAssignedDate(rs.getLong("assigneddate"));
        assetAssignment.setReturnDate(rs.getLong("returndate"));
        assetAssignment.setIsAssigned(rs.getBoolean("isassigned"));
        asset.setAssetAssignment(assetAssignment);

        // Mapping additionalDetails
        PGobject additionalDetails = (PGobject) rs.getObject("additionaldetails");
        if (additionalDetails != null) {
            try {
                JsonNode additionalDetailsNode = objectMapper.readTree(additionalDetails.getValue());
                asset.setAdditionalDetails(additionalDetailsNode);
            } catch (Exception e) {
                // Handle exception
            }
        }

        // Mapping documents
        try {
            // Fetching document related columns from the result set
            String documentId = rs.getString("documentid");
            String documentType = rs.getString("documenttype");
            String fileStoreId = rs.getString("filestoreid");
            String documentUid = rs.getString("documentuid");
            String docDetailsStr = rs.getString("docdetails");

            // Mapping docDetails to Object if available
            Object docDetails = null;
            if (docDetailsStr != null && !docDetailsStr.isEmpty()) {
                docDetails = new Gson().fromJson(docDetailsStr, Object.class);
            }

            // Creating Document object and adding it to the list
            document.setDocumentId(documentId);
            document.setDocumentType(documentType);
            document.setFileStoreId(fileStoreId);
            document.setDocumentUid(documentUid);
            document.setDocDetails(docDetails);

        } catch (Exception e) {
            // Handle exception
            e.printStackTrace();
        }

        if (asset != null) {
            // Process documents
            List<Document> assetDocuments = asset.getDocuments();
            if (assetDocuments == null) {
                assetDocuments = new ArrayList<>();
                asset.setDocuments(assetDocuments);
            }
            asset.getDocuments().add(document);
        }
    }
}