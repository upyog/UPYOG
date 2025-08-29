package org.egov.wscalculation.web.models;

public class CancelDemandReq {

    private String id;
    private String tenantId;
    private String consumerCode;
    private String businessService;

    public CancelDemandReq() {}

    public CancelDemandReq(String id, String tenantId, String consumerCode, String businessService) {
        this.id = id;
        this.tenantId = tenantId;
        this.consumerCode = consumerCode;
        this.businessService = businessService;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getConsumerCode() {
        return consumerCode;
    }

    public void setConsumerCode(String consumerCode) {
        this.consumerCode = consumerCode;
    }

    public String getBusinessService() {
        return businessService;
    }

    public void setBusinessService(String businessService) {
        this.businessService = businessService;
    }
}
