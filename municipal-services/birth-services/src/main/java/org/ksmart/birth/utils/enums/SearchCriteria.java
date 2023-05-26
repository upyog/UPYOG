package org.ksmart.birth.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchCriteria {
    SEARCH_TYPE_MYAPP("myApplication"),
    SEARCH_TYPE_PAY("PAY");

    private String code;
}
