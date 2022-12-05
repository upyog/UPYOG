package org.bel.birthdeath.crdeath.web.enums;

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
     // Idgen Service
    IDGEN_ERROR("IDGEN_ERROR");

     private String code;
}
