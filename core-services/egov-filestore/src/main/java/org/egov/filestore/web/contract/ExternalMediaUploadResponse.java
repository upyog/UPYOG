package org.egov.filestore.web.contract;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response returned after successfully uploading an audio/video file to the configured
 * cloud store. Use the {@code fileStoreId} with {@code GET /v1/files/url} to obtain
 * a presigned download URL.
 */
public class ExternalMediaUploadResponse {

    @JsonProperty("fileStoreId")
    private String fileStoreId;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("contentType")
    private String contentType;

    @JsonProperty("fileSize")
    private String fileSize;

    @JsonProperty("module")
    private String module;

    @JsonProperty("tag")
    private String tag;

    public ExternalMediaUploadResponse() {
    }

    public ExternalMediaUploadResponse(String fileStoreId, String tenantId, String contentType,
                                       String fileSize, String module, String tag) {
        this.fileStoreId = fileStoreId;
        this.tenantId = tenantId;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.module = module;
        this.tag = tag;
    }

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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String fileStoreId;
        private String tenantId;
        private String contentType;
        private String fileSize;
        private String module;
        private String tag;

        public Builder fileStoreId(String fileStoreId) {
            this.fileStoreId = fileStoreId;
            return this;
        }

        public Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder fileSize(String fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public Builder module(String module) {
            this.module = module;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public ExternalMediaUploadResponse build() {
            return new ExternalMediaUploadResponse(fileStoreId, tenantId, contentType, fileSize, module, tag);
        }
    }
}
