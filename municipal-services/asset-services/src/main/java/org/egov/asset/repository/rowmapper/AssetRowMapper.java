package org.egov.asset.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.asset.web.models.Address;
import org.egov.asset.web.models.Asset;
import org.egov.asset.web.models.AssetAssignment;
import org.egov.asset.web.models.AuditDetails;
import org.egov.asset.web.models.Boundary;
import org.egov.asset.web.models.Document;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Component
public class AssetRowMapper implements ResultSetExtractor<List<Asset>> {

	 @Autowired
		private ObjectMapper objectMapper;

    public AssetRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

	/**
	 * extract the data from the resultset and prepare the BPA Object
	 * @see org.springframework.jdbc.core.ResultSetExtractor#extractData(java.sql.ResultSet)
	 */
    @SuppressWarnings("rawtypes")
	@Override
	public List<Asset> extractData(ResultSet rs) throws SQLException, DataAccessException {
    	
        Map<String, Asset> assetMap = new LinkedHashMap<>();

        while (rs.next()) {
            String id = rs.getString("id");
            String tenantId = rs.getString("tenantId");
            String financialYear = rs.getString("financialYear");
            String sourceOfFinance = rs.getString("sourceOfFinance");
            Asset currentAsset = assetMap.get(id);
            if (currentAsset == null) {
                
                currentAsset = Asset.builder()
                	    .id(id)
                	    .tenantId(tenantId)
                	    .financialYear(financialYear)
                	    .sourceOfFinance(sourceOfFinance)
                	    .assetBookRefNo(rs.getString("bookRefNo"))
                	    .assetName(rs.getString("name"))
                	    .description(rs.getString("description"))
                	    .assetClassification(rs.getString("classification"))
                	    .assetParentCategory(rs.getString("parentCategory"))
                	    .assetCategory(rs.getString("category"))
                	    .assetSubCategory(rs.getString("subCategory"))
                	    .department(rs.getString("department"))
                	    .applicationNo(rs.getString("applicationNo"))
                	    .approvalNo(rs.getString("approvalNo"))
                	    .approvalDate(rs.getLong("approvalDate"))
                	    .applicationDate(rs.getLong("applicationDate"))
                	    .status(rs.getString("status"))
                	    .accountId(rs.getString("accountId"))
                	    .assetCategory(rs.getString("category"))
                	    .assetSubCategory(rs.getString("subCategory"))
                	    .remarks(rs.getString("remarks"))
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
	        addressDetails.setAddressLine1(rs.getString("addressLine1"));
	        addressDetails.setAddressLine1(rs.getString("addressLine2"));
	        addressDetails.setTenantId(rs.getString("tenantId"));
	        addressDetails.setDoorNo(rs.getString("doorNo"));
	        addressDetails.setLatitude(rs.getDouble("latitude"));
	        addressDetails.setLongitude(rs.getDouble("longitude"));
	        addressDetails.setAddressId(rs.getString("addressId"));
	        addressDetails.setAddressNumber(rs.getString("addressNumber"));
	        addressDetails.setType(rs.getString("type"));
	        addressDetails.setAddressLine1(rs.getString("addressLine1"));
	        addressDetails.setAddressLine2(rs.getString("addressLine2"));
	        addressDetails.setLandmark(rs.getString("landmark"));
	        addressDetails.setCity(rs.getString("city"));
	        addressDetails.setPincode(rs.getString("pincode"));
	        addressDetails.setDetail(rs.getString("detail"));
	        addressDetails.setBuildingName(rs.getString("buildingName"));
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
	        auditDetails.setCreatedBy(rs.getString("createdBy"));
	        auditDetails.setCreatedTime(rs.getLong("createdTime"));
	        auditDetails.setLastModifiedBy(rs.getString("lastModifiedBy"));
	        auditDetails.setLastModifiedTime(rs.getLong("lastModifiedTime"));
	        asset.setAuditDetails(auditDetails);
	        
	    // Mapping AssignmDetails
	    AssetAssignment assetAssignment = new AssetAssignment();
	    	assetAssignment.setAssignmentId(rs.getString("assignmentId"));
	    	assetAssignment.setAssignedUserName(rs.getString("assignedUserName"));
	    	assetAssignment.setAssignedDate(rs.getLong("assignedDate"));
	    	assetAssignment.setReturnDate(rs.getLong("returnDate"));
	    	assetAssignment.setIsAssigned(rs.getBoolean("isAssigned"));
	    	asset.setAssetAssignment(assetAssignment);

        // Mapping additionalDetails
        PGobject additionalDetails = (PGobject) rs.getObject("additionalDetails");
        if (additionalDetails != null) {
            try {
                JsonNode additionalDetailsNode = objectMapper.readTree(additionalDetails.getValue());
                asset.setAdditionalDetails(additionalDetailsNode);
            } catch (Exception e) {
                // Handle exception
            }
        }

        // Mapping documents
        //List<Document> documents = new ArrayList<>();
        try {
            // Fetching document related columns from the result set
            String documentId = rs.getString("documentId");
            String documentType = rs.getString("documentType");
            String fileStoreId = rs.getString("fileStoreId");
            String documentUid = rs.getString("documentUid");
            String docDetailsStr = rs.getString("docDetails");

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
            //documents.add(document);
            
        } catch (Exception e) {
            // Handle exception
        	e.printStackTrace();
        }
        //asset.setDocuments(documents);
        //asset.getDocuments().add(document);
        if (asset != null ) {
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
