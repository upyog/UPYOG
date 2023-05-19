package org.ksmart.birth.correction.enrichment;

import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.common.model.AuditDetails;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.enums.UpdateRegisterColumn;
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
                                    if(correction.getSpecificCondition() == null){
                                        correction.setSpecificCondition(BirthConstants.NOT_APPLICABLE);
                                    }
                                    correction.getCorrectionFieldValue().forEach(
                                            column -> {
                                                column.setAuditDetails(auditDetails);
                                                if(column.getColumn().contains(UpdateRegisterColumn.REG_CHILD_DOB.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    correction.setSpecificCondition(correction.getConditionCode());
                                                    column.setTableName(UpdateRegisterColumn.REG_CHILD_DOB.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_CHILD_DOB.getRegTableColumn());
                                                    birth.setDateOfBirth(Long.parseLong(column.getNewValue()));
                                                    column.setOldValue(registerBirthDetails.get(0).getDateOfBirth().toString());
                                                } else if(column.getColumn().contains(UpdateRegisterColumn.REG_CHILD_AADHAAR.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName(UpdateRegisterColumn.REG_CHILD_AADHAAR.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_CHILD_AADHAAR.getRegTableColumn());
                                                    birth.setFirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getAadharNo());
                                                }
                                                else if(column.getColumn().contains(UpdateRegisterColumn.REG_CHILD_SEX.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName(UpdateRegisterColumn.REG_CHILD_SEX.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_CHILD_SEX.getRegTableColumn());
                                                    birth.setFirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getGender());
                                                } else if(column.getColumn().contains(UpdateRegisterColumn.REG_CHILD_F_NAME_EN.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName(UpdateRegisterColumn.REG_CHILD_F_NAME_EN.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_CHILD_F_NAME_EN.getRegTableColumn());
                                                    birth.setFirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getFirstNameEn());
                                                } else if(column.getColumn().contains(UpdateRegisterColumn.REG_CHILD_F_NAME_ML.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName(UpdateRegisterColumn.REG_CHILD_F_NAME_ML.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_CHILD_F_NAME_ML.getRegTableColumn());
                                                    birth.setFirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getFirstNameMl());
                                                } else if(column.getColumn().contains(UpdateRegisterColumn.REG_CHILD_M_NAME_EN.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName(UpdateRegisterColumn.REG_CHILD_M_NAME_EN.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_CHILD_M_NAME_EN.getRegTableColumn());
                                                    birth.setFirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getMiddleNameEn());
                                                } else if(column.getColumn().contains(UpdateRegisterColumn.REG_CHILD_M_NAME_ML.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName(UpdateRegisterColumn.REG_CHILD_M_NAME_ML.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_CHILD_M_NAME_ML.getRegTableColumn());
                                                    birth.setFirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getMiddleNameMl());
                                                } else if(column.getColumn().contains(UpdateRegisterColumn.REG_CHILD_L_NAME_EN.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName(UpdateRegisterColumn.REG_CHILD_L_NAME_EN.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_CHILD_L_NAME_EN.getRegTableColumn());
                                                    birth.setFirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getLastNameEn());
                                                }else if(column.getColumn().contains(UpdateRegisterColumn.REG_CHILD_L_NAME_ML.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName(UpdateRegisterColumn.REG_CHILD_L_NAME_ML.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_CHILD_L_NAME_ML.getRegTableColumn());
                                                    birth.setFirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getLastNameMl());
                                                } else if(column.getColumn().contains(UpdateRegisterColumn.REG_FATHER_NAME_EN.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName(UpdateRegisterColumn.REG_FATHER_NAME_EN.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_FATHER_NAME_EN.getRegTableColumn());
                                                    birth.setFatherFirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getRegisterBirthFather().getFirstNameEn());
                                                } else if(column.getColumn().contains(UpdateRegisterColumn.REG_FATHER_NAME_ML.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName(UpdateRegisterColumn.REG_FATHER_NAME_ML.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_FATHER_NAME_ML.getRegTableColumn());
                                                    birth.setFatherFirstNameMl(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getRegisterBirthFather().getFirstNameMl());
                                                } else if(column.getColumn().contains(UpdateRegisterColumn.REG_FATHER_AADHAAR.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName(UpdateRegisterColumn.REG_FATHER_AADHAAR.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_FATHER_AADHAAR.getRegTableColumn());
                                                    birth.setFatherFirstNameMl(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getRegisterBirthFather().getAadharNo());
                                                } else if(column.getColumn().contains(UpdateRegisterColumn.REG_MOTHER_NAME_EN.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName(UpdateRegisterColumn.REG_MOTHER_NAME_EN.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_MOTHER_NAME_EN.getRegTableColumn());
                                                    birth.setMotherfirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getRegisterBirthMother().getFirstNameEn());
                                                } else if(column.getColumn().contains(UpdateRegisterColumn.REG_MOTHER_NAME_ML.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName(UpdateRegisterColumn.REG_MOTHER_NAME_ML.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_MOTHER_NAME_ML.getRegTableColumn());
                                                    birth.setMotherfirstNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getRegisterBirthMother().getFirstNameMl());
                                                } else if(column.getColumn().contains(UpdateRegisterColumn.REG_MOTHER_AADHAAR.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName(UpdateRegisterColumn.REG_MOTHER_AADHAAR.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_MOTHER_AADHAAR.getRegTableColumn());
                                                    birth.setMotherAadhar(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getRegisterBirthMother().getAadharNo());
                                                } else if(column.getColumn().contains(UpdateRegisterColumn.REG_ADDRESS_PERMANENT_HN_EN.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName(UpdateRegisterColumn.REG_ADDRESS_PERMANENT_HN_EN.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_ADDRESS_PERMANENT_HN_EN.getRegTableColumn());
                                                    birth.getCorrectionAddress().setPermanentHouseNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getRegisterBirthPermanent().getHouseNameEn());
                                                } else if(column.getColumn().contains(UpdateRegisterColumn.REG_ADDRESS_PERMANENT_HN_ML.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName(UpdateRegisterColumn.REG_ADDRESS_PERMANENT_HN_ML.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_ADDRESS_PERMANENT_HN_ML.getRegTableColumn());
                                                    birth.getCorrectionAddress().setPermanentHouseNameMl(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getRegisterBirthPermanent().getHouseNameMl());
                                                } else if(column.getColumn().contains(UpdateRegisterColumn.REG_ADDRESS_PERMANENT_LO_EN.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName(UpdateRegisterColumn.REG_ADDRESS_PERMANENT_LO_EN.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_ADDRESS_PERMANENT_LO_EN.getRegTableColumn());
                                                    birth.getCorrectionAddress().setPermanentLocalityNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getRegisterBirthPermanent().getLocalityEn());
                                                } else if(column.getColumn().contains(UpdateRegisterColumn.REG_ADDRESS_PERMANENT_LO_ML.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName(UpdateRegisterColumn.REG_ADDRESS_PERMANENT_LO_ML.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_ADDRESS_PERMANENT_LO_ML.getRegTableColumn());
                                                    birth.getCorrectionAddress().setPermanentLocalityNameMl(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getRegisterBirthPermanent().getLocalityMl());
                                                } else if(column.getColumn().contains(UpdateRegisterColumn.REG_ADDRESS_PERMANENT_STR_EN.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName(UpdateRegisterColumn.REG_ADDRESS_PERMANENT_STR_EN.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_ADDRESS_PERMANENT_STR_EN.getRegTableColumn());
                                                    birth.getCorrectionAddress().setPermanentStreetNameEn(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getRegisterBirthPermanent().getStreetNameEn());
                                                } else if(column.getColumn().contains(UpdateRegisterColumn.REG_ADDRESS_PERMANENT_STR_ML.getUiColoumn())) {
                                                    column.setId(UUID.randomUUID().toString());
                                                    column.setBirthId(birth.getId());
                                                    column.setCorrectionId(correction.getId());
                                                    column.setCorrectionFieldName(correction.getCorrectionFieldName());
                                                    column.setTableName(UpdateRegisterColumn.REG_ADDRESS_PERMANENT_STR_ML.getRegTable());
                                                    column.setColumnName(UpdateRegisterColumn.REG_ADDRESS_PERMANENT_STR_ML.getRegTableColumn());
                                                    birth.getCorrectionAddress().setPermanentStreetNameMl(column.getNewValue());
                                                    column.setOldValue(registerBirthDetails.get(0).getRegisterBirthPermanent().getStreetNameMl());
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
