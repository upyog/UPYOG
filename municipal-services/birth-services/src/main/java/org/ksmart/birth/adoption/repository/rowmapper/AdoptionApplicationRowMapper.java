package org.ksmart.birth.adoption.repository.rowmapper;

import org.egov.tracer.model.CustomException;
import org.ksmart.birth.web.model.DocumentDetails;
import org.ksmart.birth.web.model.adoption.AdoptionApplication;
import org.ksmart.birth.web.model.birthnac.NacApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Component
public class AdoptionApplicationRowMapper implements ResultSetExtractor<List<AdoptionApplication>>, AdoptionBaseRowMapper, AdoptionParentDetailRowMapper, AdoptionInformatDetailsRowMapper, AdoptionParentAddressRowMapper ,AdoptInitiatorDetailsRowMapper{

    @Override
    public List<AdoptionApplication> extractData(ResultSet rs) throws SQLException, DataAccessException { //how to handle null
        List<AdoptionApplication> result = new ArrayList<>();
        Map<String, AdoptionApplication> AdptnMap = new HashMap<>();   

        while (rs.next()) {
        	 String currentid = rs.getString("ba_id");
        	 AdoptionApplication currentAdoption = AdptnMap.get(currentid);
        	  if (null == currentAdoption) {
        		  currentAdoption = AdoptionApplication.builder()
                    .id(rs.getString("ba_id"))
                    .dateOfReport(rs.getLong("ba_dateofreport"))
                    .dateOfBirth(rs.getLong("ba_dateofbirth"))
                    .timeOfBirth(rs.getLong("ba_timeofbirth"))
                    .gender(rs.getString("ba_gender"))
                    .aadharNo(rs.getString("ba_aadharno"))
                    .isChildName(isChildNameEntered(rs.getString("ba_firstname_en").trim()))
                    .firstNameEn(rs.getString("ba_firstname_en"))
                    .firstNameMl(rs.getString("ba_firstname_ml"))
                    .middleNameEn(rs.getString("ba_middlename_en"))
                    .middleNameMl(rs.getString("ba_middlename_ml"))
                    .lastNameEn(rs.getString("ba_lastname_en"))
                    .lastNameMl(rs.getString("ba_lastname_ml"))
                    .placeofBirthId(rs.getString("pla_placeofbirthid"))
                    .institutionTypeId(rs.getString("pla_institution_type_id"))
                    .institutionNameCode(rs.getString("pla_institution_id"))
                    .hospitalId(rs.getString("pla_hospitalid"))
                    .wardId(rs.getString("pla_ward_id"))
                    .adrsPincode(rs.getString("pla_ho_pinno"))
                    .adrsPostOffice(rs.getString("pla_ho_poid"))
                    .adrsHouseNameEn(rs.getString("pla_ho_housename_en"))
                    .adrsHouseNameMl(rs.getString("pla_ho_housename_ml"))
                    .adrsLocalityNameEn(rs.getString("pla_ho_locality_en"))
                    .adrsLocalityNameMl(rs.getString("pla_ho_locality_ml"))
                    .adrsStreetNameEn(rs.getString("pla_ho_street_name_en"))
                    .adrsStreetNameMl(rs.getString("pla_ho_street_name_ml"))
                    .vehicleHaltplace(rs.getString("pla_vehicle_haltplace_en"))
                    .vehicleHaltPlaceMl(rs.getString("pla_vehicle_haltplace_en"))
                    .vehicleFromEn(rs.getString("pla_vehicle_from_en"))
                    .vehicleFromMl(rs.getString("pla_vehicle_from_ml"))
                    .vehicleTypeid(rs.getString("pla_vehicletypeid"))
                    .vehicleDesDetailsEn(rs.getString("pla_vehicle_desc"))
                    .setadmittedHospitalEn(rs.getString("pla_vehicle_hospitalid"))
                    .vehicleToEn(rs.getString("pla_vehicle_to_en"))
                    .vehicleToMl(rs.getString("pla_vehicle_to_ml"))
                    .vehicleRegistrationNo(rs.getString("pla_vehicle_registration_no"))
                    .vehicleDesDetailsEn(rs.getString("pla_vehicle_desc"))
//                    .setadmittedHospitalEn(rs.getString("pla_vehicle_admit_hospital_en"))
                    .publicPlaceDecpEn(rs.getString("pla_public_place_desc"))
                    .publicPlaceType(rs.getString("pla_public_place_id"))
                    .localityNameEn(rs.getString("pla_public_locality_en"))
                    .localityNameMl(rs.getString("pla_public_locality_ml"))
                    .streetNameEn(rs.getString("pla_public_street_name_en"))
                    .streetNameMl(rs.getString("pla_public_street_name_ml"))
                    .birthWeight(rs.getDouble("stat_weight_of_child"))
                    .pregnancyDuration(rs.getInt("stat_duration_of_pregnancy_in_week"))
                    .medicalAttensionSub(rs.getString("stat_nature_of_medical_attention"))
                    .deliveryMethods(rs.getString("stat_delivery_method"))
                    .esignUserDesigCode(rs.getString("ba_esign_user_desig_code"))
                    .tenantId(rs.getString("ba_tenantid"))
                    .applicationType(rs.getString("ba_applicationtype"))
                    .businessService(rs.getString("ba_businessservice"))
                    .workFlowCode(rs.getString("ba_workflowcode"))
                    .registrationNo(rs.getString("ba_registrationno"))
                    .ampm(rs.getString("ba_am_pm"))
                    .remarksEn(rs.getString("ba_remarks_en"))
                    .remarksMl(rs.getString("ba_aadharno"))
                    .applicationNo(rs.getString("ba_applicationno"))
                    .applicationType(rs.getString("ba_applicationtype"))
                    .workFlowCode(rs.getString("ba_workflowcode"))
                    .action(rs.getString("ba_action"))
                    .applicationStatus(rs.getString("ba_status"))
                    .isAdopted(rs.getBoolean("ba_is_adopted"))
                    .adoptDeedOrderNo(rs.getString("ba_adopt_deed_order_no"))
                    .adoptDateoforderDeed(rs.getLong("ba_adopt_dateoforder_deed"))
                    .adoptIssuingAuththority(rs.getString("ba_adopt_issuing_auththority"))
                    .adoptHasAgency(rs.getBoolean("ba_adopt_has_agency"))
                    .adoptAgencyName(rs.getString("ba_adopt_agency_name"))
                    .adoptAgencyAddress(rs.getString("ba_adopt_agency_address"))
                    .adoptDecreeOrderNo(rs.getString("ba_adopt_decree_order_no"))
                    .adoptDateoforderDecree(rs.getLong("ba_adopt_dateoforder_decree"))
                    .adoptAgencyContactPerson(rs.getString("ba_adopt_agency_contact_person"))
                    .adoptAgencyContactPersonMobileno(rs.getString("ba_adopt_agency_contact_person_mobileno"))                    
                    .auditDetails(getAuditDetails(rs))
                    .parentsDetails(KsmartBirthParentDetail(rs))
                    .birthStatisticsUuid(rs.getString("stat_id"))
                    .birthPlaceUuid(rs.getString("pla_id"))
                    .fileNumber(rs.getString("ba_fm_fileno"))
                    .fileDate(rs.getLong("ba_file_date"))
                    .fileStatus(rs.getString("ba_file_status"))
                    .informatDetail(getInformantDetail(rs))
                    .documentDetails(new ArrayList<DocumentDetails>())
                    .parentAddress(getKsmartBirthParentAddress(rs))
                    .build();
        	  }
        	  addChildrenToAdoption(rs,currentAdoption);
        	  AdptnMap.put(currentid, currentAdoption);
        }
        
        return new ArrayList<>(AdptnMap.values());
    }
    public void addChildrenToAdoption(ResultSet rs, AdoptionApplication adoption) {
       
        setDocuments(rs, adoption);
    }
    private Boolean isChildNameEntered(String name) {
        if (name==null) return true;
        else return false;
    }
    public void setDocuments(ResultSet rs, AdoptionApplication currentAdoption) {
        try {
            List<DocumentDetails> Documents = new ArrayList<>();
            if (CollectionUtils.isEmpty(currentAdoption.getDocumentDetails()))
            	Documents = new ArrayList<DocumentDetails>();
            else
            	Documents = currentAdoption.getDocumentDetails();

            List<String> ids = Documents.stream().map(DocumentDetails::getId).collect(Collectors.toList());
            if (!StringUtils.isEmpty(rs.getString("ebad_id")) && !ids.contains(rs.getString("ebad_id"))) {
               
            	DocumentDetails document = DocumentDetails.builder()
            			.id(rs.getString("ebad_id"))
                        .documentName(rs.getString("ebad_document_name"))
                        .documentDescription(rs.getString("ebad_document_description"))
                        .active(rs.getBoolean("ebad_active"))
                        .documentType(rs.getString("ebad_document_type"))
                        .fileStoreId(rs.getString("ebad_filestoreid"))
                        .documentLink(rs.getString("ebad_document_link"))
                        .fileType(rs.getString("ebad_file_type"))
                        .fileSize(rs.getString("ebad_file_size"))
                        .birthdtlid(rs.getString("ebad_birthdtlid"))
                        .auditDetails(getAuditDetails(rs))
                        .build();

            	Documents.add(document);
            }
            currentAdoption.setDocumentDetails(Documents);
        } catch (Exception e) {
            
            throw new CustomException("ROWMAPPER_ERROR", "Error in row mapper while mapping document");

        }
    }
}


