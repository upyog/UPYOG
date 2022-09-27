package org.egov.filemgmnt.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodes {

    // Applicant Personal
    APPLICANT_PERSONAL_REQUIRED("REQUIRED"),
    APPLICANT_PERSONAL_NOT_FOUND("NOT_FOUND"),
    APPLICANT_PERSONAL_INVALID_SEARCH_CRITERIA("INVALID_CRITERIA"),
    APPLICANT_PERSONAL_INVALID_UPDATE("INVALID_UPDATE");

    private String code;
}
