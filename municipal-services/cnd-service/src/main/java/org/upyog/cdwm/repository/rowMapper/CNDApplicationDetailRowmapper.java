package org.upyog.cdwm.repository.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.cdwm.util.CNDServiceUtil;
import org.upyog.cdwm.web.models.CNDApplicationDetail;
import org.upyog.cdwm.web.models.DocumentDetail;
import org.upyog.cdwm.web.models.FacilityCenterDetail;
import org.upyog.cdwm.web.models.WasteTypeDetail;

import lombok.extern.slf4j.Slf4j;

/**
 * RowMapper class to extract data from the ResultSet and map it to a list of
 * CNDApplicationDetail objects.
 */

@Component
@Slf4j
public class CNDApplicationDetailRowmapper implements ResultSetExtractor<List<CNDApplicationDetail>> {

	 /**
     * Extracts data from the provided ResultSet and maps it to a list of
     * CNDApplicationDetail objects.
     * 
     * @param rs the ResultSet containing the database query results
     * @return a list of CNDApplicationDetail objects
     * @throws SQLException         if a database access error occurs
     * @throws DataAccessException if there is an issue accessing the data
     */
	
    @Override
    public List<CNDApplicationDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
        log.info("Fetching CND application details...");
        Map<String, CNDApplicationDetail> applicationDetailMap = new LinkedHashMap<>();
        List<CNDApplicationDetail> applicationDetails = new ArrayList<>();

        while (rs.next()) {
            String applicationId = rs.getString("application_id");
            log.info("Processing applicationId: {}", applicationId);

            if (applicationId == null) {
                log.warn("Skipping record due to null applicationId");
                continue;
            }

            CNDApplicationDetail currentApplication = applicationDetailMap.get(applicationId);

            if (currentApplication == null) {
                try {
                    String applicationTypeStr = rs.getString("application_type");
                    log.info("application_type value: {}", applicationTypeStr);

                    CNDApplicationDetail.ApplicationType applicationType = applicationTypeStr != null
                            ? CNDApplicationDetail.ApplicationType.valueOf(applicationTypeStr.toUpperCase())
                            : null;

                    currentApplication = CNDApplicationDetail.builder()
                            .applicationId(applicationId)
                            .tenantId(rs.getString("tenant_id"))
                            .applicationNumber(rs.getString("application_number"))
                            .applicationType(applicationType)
                            .typeOfConstruction(rs.getString("type_of_construction"))
                            .depositCentreDetails(rs.getString("deposit_centre_details"))
                            .applicantDetailId(rs.getString("applicant_detail_id"))
                            .requestedPickupDate(rs.getDate("requested_pickup_date") != null
                                    ? rs.getDate("requested_pickup_date").toLocalDate()
                                    : null)
                            .addressDetailId(rs.getString("address_detail_id"))
                            .applicationStatus(rs.getString("application_status"))
                            .additionalDetails(rs.getString("additional_details"))
                            .houseArea(rs.getObject("house_area") != null ? rs.getLong("house_area") : 0)
                            .constructionFromDate(rs.getDate("construction_from_date") != null
                                    ? rs.getDate("construction_from_date").toLocalDate()
                                    : null)
                            .constructionToDate(rs.getDate("construction_to_date") != null
                                    ? rs.getDate("construction_to_date").toLocalDate()
                                    : null)
                            .propertyType(rs.getString("property_type"))
                            .totalWasteQuantity(rs.getBigDecimal("total_waste_quantity"))
                            .noOfTrips(rs.getObject("no_of_trips") != null ? rs.getInt("no_of_trips") : 0)
                            .vehicleId(rs.getString("vehicle_id"))
                            .vehicleType(rs.getString("vehicle_type"))
                            .vendorId(rs.getString("vendor_id"))
                            .pickupDate(rs.getDate("pickup_date") != null
                                    ? rs.getDate("pickup_date").toLocalDate()
                                    : null)
                            .completedOn(rs.getTimestamp("completed_on") != null
                                    ? rs.getTimestamp("completed_on").toLocalDateTime()
                                    : null)
                            .applicantMobileNumber(rs.getString("applicant_mobile_number"))
                            .wasteTypeDetails(new ArrayList<>()) 
                            .documentDetails(new ArrayList<>())
                            .auditDetails(CNDServiceUtil.getAuditDetails(rs))
                            .build();

                    applicationDetailMap.put(applicationId, currentApplication);
                } catch (Exception e) {
                    log.error("Error processing applicationId {}: {}", applicationId, e.getMessage(), e);
                    continue; 
                }
            }

            // Add WasteTypeDetail
            WasteTypeDetail wasteTypeDetail = addWasteTypeDetail(rs);
            if (wasteTypeDetail != null) {
                log.info("Adding WasteTypeDetail for applicationId: {}", applicationId);
                currentApplication.getWasteTypeDetails().add(wasteTypeDetail);
            } else {
                log.warn("WasteTypeDetail is NULL for applicationId: {}", applicationId);
            }

            // Add DocumentDetail
            DocumentDetail documentDetail = addDocumentDetail(rs);
            if (documentDetail != null) {
                log.info("Adding DocumentDetail for applicationId: {}", applicationId);
                currentApplication.getDocumentDetails().add(documentDetail);
            }
            
            // Add DepositCentreDetail
            FacilityCenterDetail depositCentreDetail = addDepositCentreDetail(rs);
            if (depositCentreDetail != null) {
                log.info("Adding DepositCentreDetail for applicationId: {}", applicationId);
                currentApplication.setFacilityCenterDetail(depositCentreDetail);
            }


        }

        applicationDetails.addAll(applicationDetailMap.values());
        return applicationDetails;
    }

    /**
     * Method to extract and return WasteTypeDetail from ResultSet
     */
    private WasteTypeDetail addWasteTypeDetail(ResultSet rs) {
        try {
            return WasteTypeDetail.builder()
                    .wasteTypeId(rs.getString("waste_type_id"))
                    .applicationId(rs.getString("application_id"))
                    .enteredByUserType(rs.getString("entered_by_user_type"))
                    .wasteType(rs.getString("waste_type"))
                    .quantity(rs.getBigDecimal("quantity"))
                    .metrics(rs.getString("metrics"))
                    .build();
        } catch (SQLException e) {
            log.error("Error extracting waste type detail", e);
            return null;
        }
    }

    private DocumentDetail addDocumentDetail(ResultSet rs) {
        try {
            return DocumentDetail.builder()
                    .documentDetailId(rs.getString("document_detail_id"))
                    .applicationId(rs.getString("application_id"))
                    .documentType(rs.getString("document_type"))
                    .uploadedByUserType(rs.getString("uploaded_by_user_type"))
                    .fileStoreId(rs.getString("file_store_id"))
                    .build();
        } catch (SQLException e) {
            log.error("Error extracting document detail", e);
            return null;
        }
    }
    
    private FacilityCenterDetail addDepositCentreDetail(ResultSet rs) {
        try {
            return FacilityCenterDetail.builder()
                    .disposalId(rs.getString("disposal_id"))
                    .applicationId(rs.getString("application_id"))
                    .vehicleId(rs.getString("vehicle_id"))
                    .vehicleDepotNo(rs.getString("vehicle_depot_no"))
                    .netWeight(rs.getBigDecimal("net_weight"))
                    .grossWeight(rs.getBigDecimal("gross_weight"))
                    .dumpingStationName(rs.getString("dumping_station_name"))
                    .disposalDate(rs.getTimestamp("disposal_date") != null
                            ? rs.getTimestamp("disposal_date").toLocalDateTime()
                            : null)
                    .disposalType(rs.getString("disposal_type"))
                    .nameOfDisposalSite(rs.getString("name_of_disposal_site"))
                    .build();
        } catch (SQLException e) {
            log.error("Error extracting deposit centre detail", e);
            return null;
        }
    }
    
    
}
