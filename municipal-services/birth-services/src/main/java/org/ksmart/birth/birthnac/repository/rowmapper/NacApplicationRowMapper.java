package org.ksmart.birth.birthnac.repository.rowmapper;

 
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.web.model.DocumentDetails;
import org.ksmart.birth.web.model.birthnac.NacApplication;
import org.ksmart.birth.web.model.birthnac.NacOtherChildren;
import org.ksmart.birth.web.model.correction.CorrectionField;
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
public class NacApplicationRowMapper implements ResultSetExtractor<List<NacApplication>>, NacBaseRowMapper, NacParentDetailRowMapper, NacInformatDetailsRowMapper, NacParentAddressRowMapper,NacApplicantDetailsRowMapper, NacOtherChildrenRowMapper ,NacInitiatorDetailsRowMapper {

    @Override
    public List<NacApplication> extractData(ResultSet rs) throws SQLException, DataAccessException { //how to handle null         
        Map<String, NacApplication> nacMap = new HashMap<>();    

        while (rs.next()) {
        	  String currentid = rs.getString("ba_id");
        	  NacApplication currentNac = nacMap.get(currentid);
              if (null == currentNac) {
        	     currentNac = NacApplication.builder()
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
                    .setadmittedHospitalEn(rs.getString("pla_vehicle_admit_hospital_en"))
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
                    .auditDetails(getAuditDetails(rs))
                    .parentsDetails(KsmartBirthParentDetail(rs))
//                    .birthStatisticsUuid(rs.getString("stat_id"))
                    .birthPlaceUuid(rs.getString("pla_id"))
                    .fileNumber(rs.getString("ba_fm_fileno"))
                    .fileDate(rs.getLong("ba_file_date"))
                    .fileStatus(rs.getString("ba_file_status"))
                    .applicantDetails(getApplicant(rs))
                    .otherChildrenDetails(new ArrayList<NacOtherChildren>())
                    .documentDetails(new ArrayList<DocumentDetails>())
                    .parentAddress(getKsmartBirthParentAddress(rs))
                    .nacorderofChildren(rs.getInt("ba_nac_order_of_child"))
                    .build();
        	  }
        	  
        	   addChildrenToNac(rs,currentNac);
        	   nacMap.put(currentid, currentNac);
        	   
//       result.add(currentNac);
       }
        return new ArrayList<>(nacMap.values());
//        return result;
    }
    public void addChildrenToNac(ResultSet rs, NacApplication nac) {
        setOtherChildrens(rs, nac);
        setDocuments(rs, nac);
    }
    private Boolean isChildNameEntered(String name) {
        if(name == null) return true;
        else return false;
    }
    
    /**
     * Maps Other children inside a ResultSet to the NacAPplication POJO  .
     *
     * @param rs
     * @param currentNac
     */
    public void setOtherChildrens(ResultSet rs, NacApplication currentNac) {
        try {
            List<NacOtherChildren> otherChildrens = new ArrayList<>();
            if (CollectionUtils.isEmpty(currentNac.getOtherChildrenDetails()))
            	otherChildrens = new ArrayList<NacOtherChildren>();
            else
            	otherChildrens = currentNac.getOtherChildrenDetails();

            List<String> ids = otherChildrens.stream().map(NacOtherChildren::getId).collect(Collectors.toList());
            if (!StringUtils.isEmpty(rs.getString("ebcb_id")) && !ids.contains(rs.getString("ebcb_id"))) {
               
            	NacOtherChildren otherChildren = NacOtherChildren.builder()
            			.id(rs.getString("ebcb_id"))
                        .childNameEn(rs.getString("ebcb_child_name_en"))
                        .childNameMl(rs.getString("ebcb_child_name_ml"))
                        .sex(rs.getString("ebcb_sex"))
                        .orderOfBirth(rs.getInt("ebcb_order_of_birth"))
                        .isAlive(rs.getBoolean("ebcb_is_alive"))
                        .dob(rs.getLong("ebcb_dob"))
                        .auditDetails(getAuditDetails(rs))
                        .build();

            	otherChildrens.add(otherChildren);
            }
            currentNac.setOtherChildrenDetails(otherChildrens);
        } catch (Exception e) {
            
            throw new CustomException("ROWMAPPER_ERROR", "Error in row mapper while mapping document");

        }
    }
    
    /**
     * Maps Documents inside a ResultSet to the NacAPplication POJO  .
     *
     * @param rs
     * @param currentNac
     */
    public void setDocuments(ResultSet rs, NacApplication currentNac) {
        try {
            List<DocumentDetails> Documents = new ArrayList<>();
            if (CollectionUtils.isEmpty(currentNac.getDocumentDetails()))
            	Documents = new ArrayList<DocumentDetails>();
            else
            	Documents = currentNac.getDocumentDetails();

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
            currentNac.setDocumentDetails(Documents);
        } catch (Exception e) {
            
            throw new CustomException("ROWMAPPER_ERROR", "Error in row mapper while mapping document");

        }
    }
    


}


