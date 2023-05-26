package org.ksmart.birth.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchCriteriaCodes {
    SEARCH_TYPE_MYAPP("MYAPP"),
    SEARCH_TYPE_PAY("PAY");

    private String code;
}
