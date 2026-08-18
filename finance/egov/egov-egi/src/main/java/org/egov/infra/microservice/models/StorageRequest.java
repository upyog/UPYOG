package org.egov.infra.microservice.models;

import java.util.List;

import org.egov.infra.validation.SanitizeHtml;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class StorageRequest {
    @SanitizeHtml
    private String tenantId;
    @SanitizeHtml
    private String module;
    @SanitizeHtml
    private String tag;
    @JsonIgnore
    private List<MultipartFile> files;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }

    public String getModule() {
        return module;
    }

    public void setModule(final String module) {
        this.module = module;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }

    public List<MultipartFile> getFiles() {
        return files;
    }

    public void setFiles(final List<MultipartFile> files) {
        this.files = files;
    }

}
