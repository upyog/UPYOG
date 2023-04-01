package org.ksmart.birth.correction.repository.rowmapper;

import org.apache.kafka.common.protocol.types.Field;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.common.model.AuditDetails;
import org.ksmart.birth.web.model.correction.CorrectionApplication;
import org.ksmart.birth.web.model.correction.CorrectionDocument;
import org.ksmart.birth.web.model.correction.CorrectionField;
import org.ksmart.birth.web.model.correction.CorrectionFieldValue;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
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

import static org.ksmart.birth.utils.enums.ErrorCodes.BIRTH_DETAILS_REQUIRED;
import static org.ksmart.birth.utils.enums.ErrorCodes.ROW_MAPPER_ERROR;

@Component
public class CorrectionApplicationRowMapper implements ResultSetExtractor<List<CorrectionApplication>>, CorrectionBaseRowMapper,  CorrectionParentAddressRowMapper {

    @Override
    public List<CorrectionApplication> extractData(ResultSet rs) throws SQLException, DataAccessException { //how to handle null
        Map<String, CorrectionApplication> applicationMap = new HashMap<>();
        while (rs.next()) {
            String currentid = rs.getString("ba_id");
            CorrectionApplication currentApplication = applicationMap.get(currentid);
            AuditDetails auditDetails = null;
            if (null == currentApplication) {

                auditDetails = AuditDetails.builder()
                        .createdBy(rs.getString("createdby"))
                        .createdTime(rs.getLong("createdtime"))
                        .lastModifiedBy(rs.getString("lastmodifiedby"))
                        .lastModifiedTime(rs.getLong("lastmodifiedtime")).build();

                currentApplication = CorrectionApplication.builder()
                        .id(rs.getString("ba_id"))
                        .dateOfReport(rs.getLong("ba_dateofreport"))
                        .dateOfBirth(rs.getLong("ba_dateofbirth"))
                        .gender(rs.getString("ba_gender"))
                        .aadharNo(rs.getString("ba_aadharno"))
                        .firstNameEn(rs.getString("ba_firstname_en"))
                        .firstNameMl(rs.getString("ba_firstname_ml"))
                        .middleNameEn(rs.getString("ba_middlename_en"))
                        .middleNameMl(rs.getString("ba_middlename_ml"))
                        .lastNameEn(rs.getString("ba_lastname_en"))
                        .lastNameMl(rs.getString("ba_lastname_ml"))
                        .tenantId(rs.getString("ba_tenantid"))
                        .applicationType(rs.getString("ba_applicationtype"))
                        .businessService(rs.getString("ba_businessservice"))
                        .workFlowCode(rs.getString("ba_workflowcode"))
                        .registrationNo(rs.getString("ba_registrationno"))
                        .applicationNo(rs.getString("ba_applicationno"))
                        .applicationType(rs.getString("ba_applicationtype"))
                        .workFlowCode(rs.getString("ba_workflowcode"))
                        .action(rs.getString("ba_action"))
                        .applicationStatus(rs.getString("ba_status"))
                        .auditDetails(getAuditDetails(rs))
                        .fileNumber(rs.getString("ba_fm_fileno"))
                        .fileDate(rs.getLong("ba_file_date"))
                        .fileStatus(rs.getString("ba_file_status"))
                        .auditDetails(auditDetails)
                        .correctionAddress(getCorrectionParentAddress(rs))
                        .correctionField(new ArrayList<CorrectionField>())
                        .build();
            }
            addChildrenToCorrectionApplication(rs, currentApplication, auditDetails);
            applicationMap.put(currentid, currentApplication);
        }
        return new ArrayList<>(applicationMap.values());
    }

   public void addChildrenToCorrectionApplication(ResultSet rs, CorrectionApplication currentApplication, AuditDetails auditDetails ) {
        setCorrectionField(rs, currentApplication,auditDetails);
    }

    public void setCorrectionField(ResultSet rs, CorrectionApplication currentApplication, AuditDetails auditDetails) {
        try {
            List<CorrectionField> correctionFields = new ArrayList<>();
            List<CorrectionApplication> applications = new ArrayList<>();
            applications.add(currentApplication);
            if (CollectionUtils.isEmpty(currentApplication.getCorrectionField()))
                correctionFields = new ArrayList<CorrectionField>();
            else
                correctionFields = currentApplication.getCorrectionField();

            List<String> ids = correctionFields.stream().map(CorrectionField::getId).collect(Collectors.toList());

            if (!StringUtils.isEmpty(rs.getString("co_id")) && (!ids.contains(rs.getString("co_id")))) {
                CorrectionField correctionField = CorrectionField.builder()
                                                                .id(rs.getString("co_id"))
                                                                .birthId(rs.getString("co_birthdtlid"))
                                                                .correctionFieldName(rs.getString("co_correction_field_name"))
                                                                .conditionCode(rs.getString("co_condition_code"))
                                                                .specificCondition(rs.getString("co_specific_condition_code"))
                                                                .correctionFieldValue(new ArrayList<CorrectionFieldValue>())
                                                                .correctionDocument(new ArrayList<CorrectionDocument>())
                                                                .auditDetails(auditDetails).build();
                correctionFields.add(correctionField);

            }

            currentApplication.setCorrectionField(correctionFields);
            currentApplication.getCorrectionField().forEach(child -> {
                try {
                    if (child.getId().contains(rs.getString("co_id"))) {
                        addFieldValueToCorrectionField(rs, child, child.getId());
                        addDocumentToCorrectionField(rs, child, child.getId());
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new CustomException(ROW_MAPPER_ERROR.getCode(),
                    "Error in row mapper while mapping CorrectionField.");
        }
    }

    public void addFieldValueToCorrectionField(ResultSet rs, CorrectionField correctionField, String id) {
        setCorrectionFieldValue(rs, correctionField, id);
    }

    public void setCorrectionFieldValue(ResultSet rs, CorrectionField correctionField, String id) {

        List<CorrectionFieldValue> correctionFieldValues = new ArrayList<>();

        if (CollectionUtils.isEmpty(correctionField.getCorrectionFieldValue()))
            correctionFieldValues = new ArrayList<CorrectionFieldValue>();
        else
            correctionFieldValues = correctionField.getCorrectionFieldValue();

        List<String> idChilds = correctionFieldValues.stream().map(CorrectionFieldValue::getId).collect(Collectors.toList());
        try {
            if(id.contains(rs.getString("ch_correction_id"))) {
                if (!StringUtils.isEmpty(rs.getString("ch_id")) && !idChilds.contains(rs.getString("ch_id"))) {
                    CorrectionFieldValue fv = CorrectionFieldValue.builder()
                            .id(rs.getString("ch_id"))
                            .birthId(rs.getString("ch_birthdtlid"))
                            .correctionId(rs.getString("ch_correction_id"))
                            .column(rs.getString("ch_local_column"))
                            .correctionFieldName(rs.getString("ch_correction_field_name"))
                            .tableName(rs.getString("ch_register_table_name"))
                            .columnName(rs.getString("ch_register_column_name"))
                            .newValue(rs.getString("ch_new_value"))
                            .oldValue(rs.getString("ch_new_value"))
                            .build();
                    correctionFieldValues.add(fv);
                }
            }
        } catch (SQLException e) {
            throw new CustomException(ROW_MAPPER_ERROR.getCode(),
                    "Error in row mapper while mapping CorrectionFieldValue.");
        }
        correctionField.setCorrectionFieldValue(correctionFieldValues);
    }

    public void addDocumentToCorrectionField(ResultSet rs, CorrectionField correctionField, String id) {
        setCorrectionDocument(rs, correctionField, id);
    }
    public void setCorrectionDocument(ResultSet rs, CorrectionField correctionField, String id) {

        List<CorrectionDocument> correctionDocuments = new ArrayList<>();

        if (CollectionUtils.isEmpty(correctionField.getCorrectionDocument()))
            correctionDocuments = new ArrayList<CorrectionDocument>();
        else
            correctionDocuments = correctionField.getCorrectionDocument();

        List<String> idChilds = correctionDocuments.stream().map(CorrectionDocument::getId).collect(Collectors.toList());
        try {
            if(id.contains(rs.getString("do_correction_id"))) {
                if (!StringUtils.isEmpty(rs.getString("do_id")) && !idChilds.contains(rs.getString("do_id"))) {
                    CorrectionDocument ch = CorrectionDocument.builder()
                            .id(rs.getString("do_id"))
                            .birthId(rs.getString("do_birthdtlid"))
                            .correctionId(rs.getString("do_correction_id"))
                            .correctionFieldName(rs.getString("do_correction_field_name"))
                            .documentType(rs.getString("do_document_type"))
                            .fileStoreId(rs.getString("do_filestoreid"))
                            .active(rs.getBoolean("do_active")).build();
                    correctionDocuments.add(ch);
                }
            }
        } catch (SQLException e) {
            throw new CustomException(ROW_MAPPER_ERROR.getCode(),
                    "Error in row mapper while mapping CorrectionDocument.");
        }
        correctionField.setCorrectionDocument(correctionDocuments);
    }
}


