package org.upyog.cdwm.web.models;

import digit.models.coremodels.AuditDetails;

import java.math.BigDecimal;

public class WasteTypeDetail {

    private String applicationId;

    private String wasteTypeId;

    private String enteredByUserType;

    private BigDecimal quantity;

    private String metrics;

    private AuditDetails auditDetails;
}
