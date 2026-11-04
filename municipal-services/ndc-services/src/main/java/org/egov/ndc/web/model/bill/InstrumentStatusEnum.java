package org.egov.ndc.web.model.bill;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashSet;
import java.util.Set;

public enum InstrumentStatusEnum {

    APPROVED("APPROVED", Category.OPEN),
    APPROVAL_PENDING("APPROVAL_PENDING", Category.OPEN),
    TO_BE_SUBMITTED("TO_BE_SUBMITTED", Category.OPEN),
    REMITTED("REMITTED", Category.OPEN),
    REJECTED("REJECTED", Category.CLOSED),
    CANCELLED("CANCELLED", Category.CLOSED),
    DISHONOURED("DISHONOURED", Category.CLOSED);


    private String value;

    private Category category;

    InstrumentStatusEnum(String value, Category category) {
        this.value = value;
        this.category = category;
    }

    public boolean isCategory(Category category) {
        return this.category == category;
    }

    public static Set<String> statusesByCategory(Category category) {
        Set<String> statuses = new HashSet<>();
        for (InstrumentStatusEnum b : InstrumentStatusEnum.values()) {
            if (b.category == category) {
                statuses.add(b.value);
            }
        }

        return statuses;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static InstrumentStatusEnum fromValue(String text) {
        for (InstrumentStatusEnum b : InstrumentStatusEnum.values()) {
            if (String.valueOf(b.value).equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

    public enum Category {
        OPEN,
        CLOSED;
    }

}
