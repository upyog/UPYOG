package org.ksmart.death.crdeath.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
     * Creates Error codes 
     * Rakhi S IKM
     * on 04/12/2022
     */

@Getter
@AllArgsConstructor
public enum ErrorCodes {

     // Applicant Personal
    DEATH_DETAILS_REQUIRED("REQUIRED"),
    DEATH_DETAILS_NOT_FOUND("NOT_FOUND"),
    DEATH_DETAILS_INVALID_CREATE("INVALID_CREATE"),

     //Updare
     DEATH_REG_NOT_FOUND("NOT_FOUND"),
     DEATH_REG_INVALID_UPDATE("INVALID_UPDATE"),
     DEATH_REG_REQUIRED("REQUIRED"),

     // Idgen Service
    IDGEN_ERROR("IDGEN_ERROR");

     private String code;
}
