package org.upyog.tp.enums;

import javax.validation.constraints.NotBlank;

public enum TreePruningStatus {
    BOOKING_CREATED;

    String status;

    public String getStatus() {
        return status;
    }
}
