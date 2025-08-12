package org.egov.common.entity.edcr;

import java.util.Map;

import org.egov.infra.microservice.contract.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.minidev.json.JSONArray;

public class MdmsResponse {

	@JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;
	
	@JsonProperty("MdmsRes")
    private Map<String, Map<String, JSONArray>> mdmsRes;

    public MdmsResponse() {
    }

    public MdmsResponse(ResponseInfo responseInfo, Map<String, Map<String, JSONArray>> mdmsRes) {
        this.responseInfo = responseInfo;
        this.mdmsRes = mdmsRes;
    }

    
    public ResponseInfo getResponseInfo() {
        return responseInfo;
    }

    public void setResponseInfo(ResponseInfo responseInfo) {
        this.responseInfo = responseInfo;
    }

    public Map<String, Map<String, JSONArray>> getMdmsRes() {
        return mdmsRes;
    }

    public void setMdmsRes(Map<String, Map<String, JSONArray>> mdmsRes) {
        this.mdmsRes = mdmsRes;
    }

    @Override
    public String toString() {
        return "MdmsResponse{" +
                "responseInfo=" + responseInfo +
                ", mdmsRes=" + mdmsRes +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ResponseInfo responseInfo;
        private Map<String, Map<String, JSONArray>> mdmsRes;

        public Builder responseInfo(ResponseInfo responseInfo) {
            this.responseInfo = responseInfo;
            return this;
        }

        public Builder mdmsRes(Map<String, Map<String, JSONArray>> mdmsRes) {
            this.mdmsRes = mdmsRes;
            return this;
        }

        public MdmsResponse build() {
            return new MdmsResponse(responseInfo, mdmsRes);
        }
    }
}
