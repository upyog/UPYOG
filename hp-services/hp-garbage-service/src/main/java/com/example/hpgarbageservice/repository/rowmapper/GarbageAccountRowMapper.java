package com.example.hpgarbageservice.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.example.hpgarbageservice.model.AuditDetails;
import com.example.hpgarbageservice.model.GarbageAccount;
import com.example.hpgarbageservice.model.GarbageBill;
import com.example.hpgarbageservice.model.GrbgAddress;
import com.example.hpgarbageservice.model.GrbgApplication;
import com.example.hpgarbageservice.model.GrbgCollectionUnit;
import com.example.hpgarbageservice.model.GrbgCommercialDetails;
import com.example.hpgarbageservice.model.GrbgDocument;
import com.example.hpgarbageservice.model.GrbgOldDetails;
import com.example.hpgarbageservice.service.GarbageAccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class GarbageAccountRowMapper implements ResultSetExtractor<List<GarbageAccount>> {
	
	@Autowired
	private ObjectMapper objectMapper;
	
    @Override
    public List<GarbageAccount> extractData(ResultSet rs) throws SQLException, DataAccessException {

        Map<Long, GarbageAccount> accountsMap = new LinkedHashMap<>();

        while (rs.next()) {

            Long accountId = rs.getLong("id");
            GarbageAccount garbageAccount = accountsMap.get(accountId);

            if (null == garbageAccount) {

                AuditDetails audit = AuditDetails.builder()
                        .createdBy(rs.getString("created_by"))
                        .createdDate(rs.getLong("created_date"))
                        .lastModifiedBy(rs.getString("last_modified_by"))
                        .lastModifiedDate(rs.getLong("last_modified_date"))
                        .build();

                garbageAccount = GarbageAccount.builder()
                        .id(rs.getLong("id"))
                        .uuid(rs.getString("uuid"))
                        .garbageId(rs.getLong("garbage_id"))
                        .propertyId(rs.getString("property_id"))
                        .type(rs.getString("type"))
                        .name(rs.getString("name"))
                        .mobileNumber(rs.getString("mobile_number"))
                        .gender(rs.getString("gender"))
                        .emailId(rs.getString("email_id"))
                        .isOwner(rs.getBoolean("is_owner"))
                        .userUuid(rs.getString("user_uuid"))
                        .declarationUuid(rs.getString("declaration_uuid"))
//                        .grbgCollectionAddressUuid(rs.getString("grbg_coll_address_uuid"))
                        .status(rs.getString("status"))
                        .additionalDetail(getAdditionalDetail(rs, "additional_detail"))
                        .tenantId(rs.getString("tenant_id"))
//                        .parentId(rs.getLong("parent_id"))
                        .documents(new ArrayList<>())
                        .garbageBills(new ArrayList<>())
                        .childGarbageAccounts(new ArrayList<>())
                        .grbgCollectionUnits(new ArrayList<>())
                        .addresses(new ArrayList<>())
                        .auditDetails(audit)
                        .build();

                accountsMap.put(accountId, garbageAccount);
            }

            
            if (null != rs.getString("app_uuid")
            		&& null == garbageAccount.getGrbgApplication()) {
                	GrbgApplication garbageApplication = populateGarbageApplication(rs, "app_");
                    garbageAccount.setGrbgApplication(garbageApplication);
            }

            if (null != rs.getString("comm_uuid")
            		&& null == garbageAccount.getGrbgCommercialDetails()) {
            	GrbgCommercialDetails garbageCommDetails = populateGrbgCommercialDetails(rs, "comm_");
                    garbageAccount.setGrbgCommercialDetails(garbageCommDetails);
            }

            if (null != rs.getString("old_dtl_uuid")
            		&& null == garbageAccount.getGrbgOldDetails()) {
            	GrbgOldDetails grbgOldDetails = populateGrbgOldDetails(rs, "old_dtl_");
                    garbageAccount.setGrbgOldDetails(grbgOldDetails);
            }
            
            
            if (null != rs.getString("unit_uuid")) {
                String unitUuid = rs.getString("unit_uuid");
                GrbgCollectionUnit grbgCollectionUnit = findUnitByUuid(garbageAccount.getGrbgCollectionUnits(), unitUuid);
                if (null == grbgCollectionUnit) {
                	GrbgCollectionUnit GrbgCollectionUnit1 = populateGarbageUnit(rs, "unit_");
                    garbageAccount.getGrbgCollectionUnits().add(GrbgCollectionUnit1);
                }
            }
            
            
            if (null != rs.getString("address_uuid")) {
                String addressUuid = rs.getString("address_uuid");
                GrbgAddress grbgAddress = findAddressByUuid(garbageAccount.getAddresses(), addressUuid);
                if (null == grbgAddress) {
                	GrbgAddress grbgAddress1 = populateAddress(rs, "address_");
                    garbageAccount.getAddresses().add(grbgAddress1);
                }
            }
            
            
            if (null != rs.getString("bill_id")) {
                String billId = rs.getString("bill_id");
                GarbageBill garbageBill = findBillByUuid(garbageAccount.getGarbageBills(), billId);
                if (null == garbageBill) {
                    GarbageBill garbageBill1 = populateGarbageBill(rs, "bill_");
                    garbageAccount.getGarbageBills().add(garbageBill1);
                }
            }

            
            if (null != rs.getString("doc_uuid")) {
                String docUuid = rs.getString("doc_uuid");
                GrbgDocument garbageDocument = findDocumentByUuid(garbageAccount.getDocuments(), docUuid);
                if (null == garbageDocument) {
                	GrbgDocument garbageDocument1 = populateGarbageDocument(rs, "doc_");
                    garbageAccount.getDocuments().add(garbageDocument1);
                }
            }

            
            if (BooleanUtils.isTrue(garbageAccount.getIsOwner())
            		&& null != rs.getString("sub_acc_id")
            		&& BooleanUtils.isNotTrue(rs.getBoolean("sub_acc_is_owner"))) {
                Long subAccId = rs.getLong("sub_acc_id");
                GarbageAccount subGarbageAccount = findSubAccById(garbageAccount.getChildGarbageAccounts(), subAccId);
                if (null == subGarbageAccount) {
                    subGarbageAccount = populateGarbageAccount(rs, "sub_acc_");
                    garbageAccount.getChildGarbageAccounts().add(subGarbageAccount);
                }

                if (null != rs.getString("sub_app_uuid")
                		&& null == subGarbageAccount.getGrbgApplication()) {
                    	GrbgApplication subGarbageApplication = populateGarbageApplication(rs, "sub_app_");
                    	subGarbageAccount.setGrbgApplication(subGarbageApplication);
                }
                
                if (null != rs.getString("sub_comm_uuid")
                		&& null == subGarbageAccount.getGrbgCommercialDetails()) {
                	GrbgCommercialDetails garbageCommDetails = populateGrbgCommercialDetails(rs, "sub_comm_");
                	subGarbageAccount.setGrbgCommercialDetails(garbageCommDetails);
                }

                if (null != rs.getString("sub_old_dtl_uuid")
                		&& null == subGarbageAccount.getGrbgOldDetails()) {
                	GrbgOldDetails grbgOldDetails = populateGrbgOldDetails(rs, "sub_old_dtl_");
                	subGarbageAccount.setGrbgOldDetails(grbgOldDetails);
                }
                
                
                if (null != rs.getString("sub_unit_uuid")) {
                    String unitUuid = rs.getString("sub_unit_uuid");
                    GrbgCollectionUnit grbgCollectionUnit = findUnitByUuid(garbageAccount.getGrbgCollectionUnits(), unitUuid);
                    if (null == grbgCollectionUnit) {
                    	GrbgCollectionUnit GrbgCollectionUnit1 = populateGarbageUnit(rs, "sub_unit_");
                    	subGarbageAccount.getGrbgCollectionUnits().add(GrbgCollectionUnit1);
                    }
                }
                
                if (null != rs.getString("sub_doc_uuid")) {
                    String subDocUuid = rs.getString("sub_doc_uuid");
                    GrbgDocument subGarbageDocument = findDocumentByUuid(subGarbageAccount.getDocuments(), subDocUuid);
                    if (null == subGarbageDocument) {
                    	GrbgDocument subGarbageDocument1 = populateGarbageDocument(rs, "sub_doc_");
                    	subGarbageAccount.getDocuments().add(subGarbageDocument1);
                    }
                }
                
                if (null != rs.getString("sub_acc_bill_id")) {
                    String subAccBillId = rs.getString("sub_acc_bill_id");
                    GarbageBill subAccGarbageBill = findBillByUuid(subGarbageAccount.getGarbageBills(), subAccBillId);
                    if (null == subAccGarbageBill) {
                        GarbageBill subAccGarbageBill1 = populateGarbageBill(rs, "sub_acc_bill_");
                        subGarbageAccount.getGarbageBills().add(subAccGarbageBill1);
                    }
                }

                
                if (null != rs.getString("sub_address_uuid")) {
                    String addressUuid = rs.getString("sub_address_uuid");
                    GrbgAddress grbgAddress = findAddressByUuid(garbageAccount.getAddresses(), addressUuid);
                    if (null == grbgAddress) {
                    	GrbgAddress grbgAddress1 = populateAddress(rs, "sub_address_");
                    	subGarbageAccount.getAddresses().add(grbgAddress1);
                    }
                }
            }
        }

        return new ArrayList<>(accountsMap.values());
    }

    private GrbgAddress populateAddress(ResultSet rs, String prefix) throws SQLException {
    	GrbgAddress grbgAddress = GrbgAddress.builder()
					.uuid(rs.getString(prefix+"uuid"))
			        .garbageId(rs.getLong(prefix+"garbage_id"))
			        .addressType(rs.getString(prefix+"address_type"))
			        .address1(rs.getString(prefix+"address1"))
			        .address2(rs.getString(prefix+"address2"))
			        .city(rs.getString(prefix+"city"))
			        .state(rs.getString(prefix+"state"))
			        .pincode(rs.getString(prefix+"pincode"))
			        .isActive(rs.getBoolean(prefix+"is_active"))
			        .zone(rs.getString(prefix+"zone"))
			        .ulbName(rs.getString(prefix+"ulb_name"))
			        .ulbType(rs.getString(prefix+"ulb_type"))
			        .wardName(rs.getString(prefix+"ward_name"))
			        .additionalDetail(getAdditionalDetail(rs, prefix + "additional_detail"))
			        .build();
		return grbgAddress;
	}

	private GrbgAddress findAddressByUuid(List<GrbgAddress> addresses, String addressUuid) {

        if (!CollectionUtils.isEmpty(addresses)) {
            return addresses.stream()
                    .filter(address -> address.getUuid().toString().equals(addressUuid))
                    .findFirst()
                    .orElse(null);
        }
        return null;
	}

	private GrbgCollectionUnit populateGarbageUnit(ResultSet rs, String prefix) throws SQLException {
    	GrbgCollectionUnit grbgCollectionUnit = GrbgCollectionUnit.builder()
    			.uuid(rs.getString(prefix+"uuid"))
    	        .unitName(rs.getString(prefix+"unit_name"))
    	        .unitWard(rs.getString(prefix+"unit_ward"))
    	        .ulbName(rs.getString(prefix+"ulb_name"))
    	        .typeOfUlb(rs.getString(prefix+"type_of_ulb"))
    	        .garbageId(rs.getLong(prefix+"garbage_id"))
    	        .unitType(rs.getString(prefix+"unit_type"))
    	        .category(rs.getString(prefix+"category"))
    	        .subCategory(rs.getString(prefix+"sub_category"))
    	        .subCategoryType(rs.getString(prefix+"sub_category_type"))
    	        .isActive(rs.getBoolean(prefix+"is_active"))
    			.build();
		return grbgCollectionUnit;
	}

	private GrbgCollectionUnit findUnitByUuid(List<GrbgCollectionUnit> grbgCollectionUnits, String unitUuid) {

        if (!CollectionUtils.isEmpty(grbgCollectionUnits)) {
            return grbgCollectionUnits.stream()
                    .filter(unit -> unit.getUuid().toString().equals(unitUuid))
                    .findFirst()
                    .orElse(null);
        }
        return null;
	}

	private GrbgOldDetails populateGrbgOldDetails(ResultSet rs, String prefix) throws SQLException {
		GrbgOldDetails grbgOldDetails = GrbgOldDetails.builder()
						.uuid(rs.getString(prefix+"uuid"))
						.garbageId(rs.getLong(prefix+"garbage_id"))
						.oldGarbageId(rs.getString(prefix+"old_garbage_id"))
						.build();
		return grbgOldDetails;
	}

	private JsonNode getAdditionalDetail(ResultSet rs, String columnLabel) {
    	JsonNode jsonNode = null;
    	try {
    		String jsonString = rs.getString(columnLabel);
            if (jsonString != null) {
                jsonNode = objectMapper.readTree(jsonString);
            }
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jsonNode;
	}

	private GrbgDocument populateGarbageDocument(ResultSet rs, String prefix) throws SQLException {
		
    	GrbgDocument garbageDocument = GrbgDocument.builder()
    			.uuid(rs.getString(prefix+"uuid"))
    			.docRefId(rs.getString(prefix+"doc_ref_id"))
    			.docName(rs.getString(prefix+"doc_name"))
    			.docType(rs.getString(prefix+"doc_type"))
    			.docCategory(rs.getString(prefix+"doc_category"))
    			.tblRefUuid(rs.getString(prefix+"tbl_ref_uuid"))
    			.build();
		return garbageDocument;
	}

	private GrbgDocument findDocumentByUuid(List<GrbgDocument> documents, String docUuid) {
    	if (!CollectionUtils.isEmpty(documents)) {
            return documents.stream()
                    .filter(doc -> StringUtils.equals(doc.getUuid(), docUuid))
                    .findFirst()
                    .orElse(null);
        }
        return null;
	}

	private GrbgCommercialDetails populateGrbgCommercialDetails(ResultSet rs, String prefix) throws SQLException {
		GrbgCommercialDetails grbgCommercialDetails = GrbgCommercialDetails.builder()
				.uuid(rs.getString(prefix+"uuid"))
				.garbageId(rs.getLong(prefix+"garbage_id"))
				.businessName(rs.getString(prefix+"business_name"))
				.businessType(rs.getString(prefix+"business_type"))
				.ownerUserUuid(rs.getString(prefix+"owner_user_uuid"))
				.build();
		return grbgCommercialDetails;
	}

	private GrbgApplication populateGarbageApplication(ResultSet rs, String prefix) throws SQLException {
    	GrbgApplication grbgApplication = GrbgApplication.builder()
    			.uuid(rs.getString(prefix+"uuid"))
    			.applicationNo(rs.getString(prefix+"application_no"))
    			.status(rs.getString(prefix+"status"))
    			.garbageId(rs.getLong(prefix+"garbage_id"))
    			.build();
		return grbgApplication;
	}

	private GarbageAccount populateGarbageAccount(ResultSet rs, String prefix) throws SQLException {

        GarbageAccount garbageAccount = GarbageAccount.builder()
                .id(rs.getLong(prefix + "id"))
                .uuid(rs.getString(prefix + "uuid"))
                .garbageId(rs.getLong(prefix + "garbage_id"))
                .propertyId(rs.getString(prefix + "property_id"))
                .type(rs.getString(prefix + "type"))
                .name(rs.getString(prefix + "name"))
                .mobileNumber(rs.getString(prefix + "mobile_number"))
                .gender(rs.getString(prefix + "gender"))
                .emailId(rs.getString(prefix + "email_id"))
                .isOwner(rs.getBoolean(prefix + "is_owner"))
                .userUuid(rs.getString(prefix + "user_uuid"))
                .declarationUuid(rs.getString(prefix + "declaration_uuid"))
//                .grbgCollectionAddressUuid(rs.getString(prefix + "grbg_coll_address_uuid"))
                .status(rs.getString(prefix + "status"))
                .additionalDetail(getAdditionalDetail(rs, prefix + "additional_detail"))
                .tenantId(rs.getString(prefix + "tenant_id"))
//                .parentId(rs.getLong(prefix + "parent_id"))
                .documents(new ArrayList<>())
                .garbageBills(new ArrayList<>())
                .grbgCollectionUnits(new ArrayList<>())
                .addresses(new ArrayList<>())
                .auditDetails(AuditDetails.builder()
                        .createdBy(rs.getString(prefix + "created_by"))
                        .createdDate(rs.getLong(prefix + "created_date"))
                        .lastModifiedBy(rs.getString(prefix + "last_modified_by"))
                        .lastModifiedDate(rs.getLong(prefix + "last_modified_date"))
                        .build())
                .build();

        return garbageAccount;
    }

    private GarbageAccount findSubAccById(List<GarbageAccount> subGarbageAccounts, Long subAccId) {

        if (!CollectionUtils.isEmpty(subGarbageAccounts)) {
            return subGarbageAccounts.stream()
                    .filter(acc -> acc.getId().equals(subAccId))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private GarbageBill findBillByUuid(List<GarbageBill> garbageBills, String bill_id) {

        if (!CollectionUtils.isEmpty(garbageBills)) {
            return garbageBills.stream()
                    .filter(bill -> bill.getId().toString().equals(bill_id)) // Adjusted to compare as string
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private GarbageBill populateGarbageBill(ResultSet rs, String prefix) throws SQLException {

        GarbageBill bill = GarbageBill.builder()
                .id(rs.getLong(prefix + "id"))
                .billRefNo(rs.getString(prefix + "bill_ref_no"))
                .garbageId(rs.getLong(prefix + "garbage_id"))
                .billAmount(rs.getDouble(prefix + "bill_amount"))
                .arrearAmount(rs.getDouble(prefix + "arrear_amount"))
                .paneltyAmount(rs.getDouble(prefix + "panelty_amount"))
                .discountAmount(rs.getDouble(prefix + "discount_amount"))
                .totalBillAmount(rs.getDouble(prefix + "total_bill_amount"))
                .totalBillAmountAfterDueDate(rs.getDouble(prefix + "total_bill_amount_after_due_date"))
                .billGeneratedBy(rs.getString(prefix + "bill_generated_by"))
                .billGeneratedDate(rs.getLong(prefix + "bill_generated_date"))
                .billDueDate(rs.getLong(prefix + "bill_due_date"))
                .billPeriod(rs.getString(prefix + "bill_period"))
                .bankDiscountAmount(rs.getDouble(prefix + "bank_discount_amount"))
                .paymentId(rs.getString(prefix + "payment_id"))
                .paymentStatus(rs.getString(prefix + "payment_status"))
                .auditDetails(AuditDetails.builder()
                        .createdBy(rs.getString(prefix + "created_by"))
                        .createdDate(rs.getLong(prefix + "created_date"))
                        .lastModifiedBy(rs.getString(prefix + "last_modified_by"))
                        .lastModifiedDate(rs.getLong(prefix + "last_modified_date"))
                        .build())
                .build();

        return bill;
    }
}
