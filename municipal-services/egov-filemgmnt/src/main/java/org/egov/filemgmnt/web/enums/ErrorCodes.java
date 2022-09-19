package org.egov.filemgmnt.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodes {

    // Applicant Personal
    APPLICANT_PERSONAL_REQUIRED("REQUIRED", "Atleast one applicant personal is required."),
    APPLICANT_PERSONAL_NOT_FOUND("NOT_FOUND", "Applicant Personal not found."),
    APPLICANT_PERSONAL_INVALID_SEARCH_CRITERIA("INVALID_CRITERIA", "Invalid search criteria."),
    APPLICANT_PERSONAL_INVALID_UPDATE("INVALID_UPDATE", "Invalid data.");

    private String code;
    private String message;
}
