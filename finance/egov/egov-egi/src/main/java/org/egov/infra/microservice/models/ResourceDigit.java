package org.egov.infra.microservice.models;

import org.egov.infra.validation.SanitizeHtml;

public class ResourceDigit {
    @SanitizeHtml
    private String contentType;
    @SanitizeHtml
    private String fileName;
    private org.springframework.core.io.Resource resource;
    @SanitizeHtml
    private String tenantId;
    @SanitizeHtml
    private String fileSize;
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public org.springframework.core.io.Resource getResource() {
        return resource;
    }
    public void setResource(org.springframework.core.io.Resource resource) {
        this.resource = resource;
    }
    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    public String getFileSize() {
        return fileSize;
    }
    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

}
