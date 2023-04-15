package org.ksmart.birth.utils.enums;

import lombok.Getter;

@Getter
public enum UpdateRegisterColumn {
    //Child details
    REG_CHILD_DOB("CR_DATE_OF_BIRTH_TIME","eg_register_birth_details","dateofbirth"),
    REG_CHILD_AADHAAR("CR_AADHAR","eg_register_birth_details","aadharno"),
    REG_CHILD_SEX("CR_GENDER","eg_register_birth_details","gender"),
    REG_CHILD_F_NAME_EN("CR_FIRST_NAME_EN","eg_register_birth_details","firstname_en"),
    REG_CHILD_F_NAME_ML("CR_FIRST_NAME_ML","eg_register_birth_details","firstname_ml"),
    REG_CHILD_M_NAME_EN("CR_MIDDLE_NAME_EN","eg_register_birth_details","middlename_en"),
    REG_CHILD_M_NAME_ML("CR_MIDDLE_NAME_ML","eg_register_birth_details","middlename_ml"),
    REG_CHILD_L_NAME_EN("CR_LAST_NAME_EN","eg_register_birth_details","lastname_en"),
    REG_CHILD_L_NAME_ML("CR_LAST_NAME_ML","eg_register_birth_details","lastname_ml"),

    //Mother Details
    REG_MOTHER_NAME_EN("CR_MOTHER_NAME_EN","eg_register_birth_mother_information","firstname_en"),
    REG_MOTHER_NAME_ML("CR_MOTHER_NAME_ML","eg_register_birth_mother_information","firstname_ml"),
    REG_MOTHER_AADHAAR("CR_MOTHER_AADHAR","eg_register_birth_mother_information","aadharno"),

    //Father Details
    REG_FATHER_NAME_EN("CR_FATHER_NAME_EN","eg_register_birth_father_information","firstname_en"),
    REG_FATHER_NAME_ML("CR_FATHER_NAME_ML","eg_register_birth_father_information","firstname_ml"),
    REG_FATHER_AADHAAR("CR_FATHER_AADHAR","eg_register_birth_father_information","aadharno"),

    //Permanent Address Details
    REG_ADDRESS_PERMANENT_HN_EN("CR_HOUSE_NO_AND_NAME_EN","eg_register_birth_permanent_address","housename_no_en"),
    REG_ADDRESS_PERMANENT_HN_ML("CR_HOUSE_NO_AND_NAME_ML","eg_register_birth_permanent_address","housename_no_ml"),
    REG_ADDRESS_PERMANENT_LO_EN("CR_LOCALITY_EN","eg_register_birth_permanent_address","locality_en"),
    REG_ADDRESS_PERMANENT_LO_ML("CR_LOCALITY_ML","eg_register_birth_permanent_address","locality_ml"),
    REG_ADDRESS_PERMANENT_STR_EN("CR_STREET_EN","eg_register_birth_permanent_address","street_name_en"),
    REG_ADDRESS_PERMANENT_STR_ML("CR_STREET_ML","eg_register_birth_permanent_address","street_name_ml");

    private final String uiColoumn;
    private final String regTable;
    private final String regTableColumn;
    UpdateRegisterColumn(String uiColoumn, String regTable,  String regTableColumn) {
        this.uiColoumn = uiColoumn;
        this.regTable = regTable;
        this.regTableColumn = regTableColumn;
    }
}