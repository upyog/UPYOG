package org.egov.infra.microservice.models;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

import org.egov.infra.validation.SanitizeHtml;

public class TaxAndPayment {
    @SanitizeHtml
    @NotNull
    private String businessService;
    
    private BigDecimal taxAmount;
    
    @NotNull
    private BigDecimal amountPaid;

    public String getBusinessService() {
        return businessService;
    }

    public void setBusinessService(String businessService) {
        this.businessService = businessService;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }
    
    
}
