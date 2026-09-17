package org.egov.infra.microservice.models;

import org.egov.infra.validation.SanitizeHtml;

public class FileReq {
    @SanitizeHtml
    private String fileStoreId;
    @SanitizeHtml
    private String tenantId;
    
    public String getFileStoreId() {
        return fileStoreId;
    }
    public void setFileStoreId(String fileStoreId) {
        this.fileStoreId = fileStoreId;
    }
    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}

