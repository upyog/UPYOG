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
import org.upyog.cdwm.web.models.*;

import lombok.extern.slf4j.Slf4j;
import org.upyog.cdwm.web.models.user.enums.AddressType;

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
                            .locality(rs.getString("locality"))
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
                            .createdByUserType(rs.getString("created_by_usertype"))
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
                    if(currentApplication.getApplicantDetailId() == null) {
                        // Create and populate Applicant Detail
                        CNDApplicantDetail applicantDetail = null;
                        if (applicationId != null) {
                            applicantDetail = new CNDApplicantDetail();
                            applicantDetail.setApplicationId(applicationId);
                            applicantDetail.setNameOfApplicant(rs.getString("name_of_applicant"));
                            applicantDetail.setMobileNumber(rs.getString("mobile_number"));
                            applicantDetail.setEmailId(rs.getString("email_id"));
                            applicantDetail.setAlternateMobileNumber(rs.getString("alternate_mobile_number"));
                        }

                        // Create and populate Address Detail
                        CNDAddressDetail addressDetail = null;
                        if (applicationId != null) {
                            addressDetail = new CNDAddressDetail();
                            addressDetail.setApplicationId(applicationId);
                            addressDetail.setHouseNumber(rs.getString("house_number"));
                            addressDetail.setAddressLine1(rs.getString("address_line_1"));
                            addressDetail.setAddressLine2(rs.getString("address_line_2"));
                            addressDetail.setFloorNumber(rs.getString("floor_number"));
                            addressDetail.setAddressType(AddressType.fromValue(rs.getString("address_type")));
                            addressDetail.setLandmark(rs.getString("landmark"));
                            addressDetail.setCity(rs.getString("city"));
                            addressDetail.setLocality(rs.getString("locality"));
                            addressDetail.setPinCode(rs.getString("pincode"));
                        }
                        // Add applicant and address details into the application
                        currentApplication.setApplicantDetail(applicantDetail);
                        currentApplication.getApplicantDetail().setAuditDetails(CNDServiceUtil.getAuditDetails(rs));
                        currentApplication.setAddressDetail(addressDetail);
                    }

                    applicationDetailMap.put(applicationId, currentApplication);
                    
                }
                          
                catch (Exception e) {
                    log.error("Error processing applicationId {}: {}", applicationId, e.getMessage(), e);
                    continue; 
                }
               
            }
           
        }

        applicationDetails.addAll(applicationDetailMap.values());
        return applicationDetails;
    }
    
}
