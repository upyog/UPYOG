package org.ksmart.birth.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodes {

    // Birth Details
    BIRTH_DETAILS_REQUIRED("REQUIRED"),
    BIRTH_DETAILS_NOT_FOUND("NOT_FOUND"),
    BIRTH_DETAILS_INVALID_SEARCH_CRITERIA("INVALID_CRITERIA"),
    BIRTH_DETAILS_INVALID_UPDATE("INVALID_UPDATE"),

    // Idgen Service
    IDGEN_ERROR("IDGEN_ERROR");
    private String code;
}
