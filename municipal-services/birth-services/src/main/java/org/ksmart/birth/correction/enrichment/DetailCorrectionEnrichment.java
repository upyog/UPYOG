package org.ksmart.birth.correction.enrichment;

import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.common.model.AuditDetails;
import org.ksmart.birth.web.model.correction.CorrectionRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class DetailCorrectionEnrichment {
    public void correctionField(CorrectionRequest request, List<RegisterBirthDetail> registerBirthDetails, AuditDetails auditDetails) {
        request.getCorrectionDetails()
                .forEach(birth -> {
                    birth.setFatherUuid(UUID.randomUUID().toString());
                    birth.setMotherUuid(UUID.randomUUID().toString());
                    if(birth.getCorrectionField().size() > 0){
                        birth.getCorrectionField().forEach(
                                correction -> {
                                    correction.setId(UUID.randomUUID().toString());
                                    correction.setBirthId(birth.getId());
                                    correction.setAuditDetails(auditDetails);
                                    correction.getCorrectionFieldValue().forEach(
                                            column -> {
                                                column.setAuditDetails(auditDetails);
                                                if(column.getColumn().contains("dob")) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    correction.setSpecificCondition(correction.getConditionCode());
                                                    column.setTableName("eg_register_birth_details");
                                                    column.setColumnName("dateofbirth");
                                                    birth.setDateOfBirth(Long.parseLong(column.getNewValue()));
                                                    column.setOldValue(registerBirthDetails.get(0).getDateOfBirth().toString());
                                                } else if(column.getColumn().contains("childaadhaar")) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName("eg_register_birth_details");
                                                    column.setColumnName("aadharno");
                                                    birth.setFirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getAadharNo());
                                                } else if(column.getColumn().contains("childgender")) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName("eg_register_birth_details");
                                                    column.setColumnName("gender");
                                                    birth.setFirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getGender());
                                                } else if(column.getColumn().contains("childfnen")) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName("eg_register_birth_details");
                                                    column.setColumnName("firstname_en");
                                                    birth.setFirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getFirstNameEn());
                                                } else if(column.getColumn().contains("childfnml")) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName("eg_register_birth_details");
                                                    column.setColumnName("firstname_ml");
                                                    birth.setFirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getFirstNameMl());
                                                } else if(column.getColumn().contains("childmnen")) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName("eg_register_birth_details");
                                                    column.setColumnName("middlename_en");
                                                    birth.setFirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getMiddleNameEn());
                                                } else if(column.getColumn().contains("childmnml")) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName("eg_register_birth_details");
                                                    column.setColumnName("middlename_ml");
                                                    birth.setFirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getMiddleNameMl());
                                                } else if(column.getColumn().contains("childlnen")) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName("eg_register_birth_details");
                                                    column.setColumnName("lastname_en");
                                                    birth.setFirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getLastNameEn());
                                                }else if(column.getColumn().contains("childlnml")) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName("eg_register_birth_details");
                                                    column.setColumnName("lastname_ml");
                                                    birth.setFirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getLastNameMl());
                                                } else if(column.getColumn().contains("fatherfnen")) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName("eg_register_birth_father_information");
                                                    column.setColumnName("firstname_en");
                                                    birth.setFatherFirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getRegisterBirthFather().getFirstNameEn());
                                                } else if(column.getColumn().contains("fatherfnml")) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName("eg_register_birth_father_information");
                                                    column.setColumnName("firstname_ml");
                                                    birth.setFatherFirstNameMl(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getRegisterBirthFather().getFirstNameMl());
                                                } else if(column.getColumn().contains("fatheraadhar")) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName("eg_register_birth_father_information");
                                                    column.setColumnName("aadharno");
                                                    birth.setFatherFirstNameMl(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getRegisterBirthFather().getAadharNo());
                                                } else if(column.getColumn().contains("motherfnen")) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName("eg_register_birth_mother_information");
                                                    column.setColumnName("firstname_en");
                                                    birth.setMotherfirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getRegisterBirthMother().getFirstNameEn());
                                                } else if(column.getColumn().contains("motherfnml")) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName("eg_register_birth_mother_information");
                                                    column.setColumnName("firstname_ml");
                                                    birth.setMotherfirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getRegisterBirthMother().getFirstNameMl());
                                                } else if(column.getColumn().contains("motheraadhar")) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName("eg_register_birth_mother_information");
                                                    column.setColumnName("aadharno");
                                                    birth.setMotherAadhar(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getRegisterBirthMother().getAadharNo());
                                                } else{ }
                                            }
                                    );
                                    correction.getCorrectionDocument().forEach(
                                            document -> {
                                                document.setId(UUID.randomUUID().toString());
                                                document.setCorrectionId(correction.getId());
                                                document.setBirthId(birth.getId());
                                                document.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                document.setAuditDetails(auditDetails);
                                            }
                                    );

                                }

                        );

                    }
                });
    }
}
