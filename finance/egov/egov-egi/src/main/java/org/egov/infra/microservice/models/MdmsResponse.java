package org.egov.infra.microservice.models;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MdmsResponse {
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("MdmsRes")
    private Map<String, Map<String, List<Object>>> mdmsRes;
    public MdmsResponse(ResponseInfo responseInfo, Map<String, Map<String, List<Object>>> mdmsRes) {
        this.responseInfo = responseInfo;
        this.mdmsRes = mdmsRes;
    }
    public MdmsResponse() {
    }
    public ResponseInfo getResponseInfo() {
        return responseInfo;
    }
    public void setResponseInfo(ResponseInfo responseInfo) {
        this.responseInfo = responseInfo;
    }
    public Map<String, Map<String, List<Object>>> getMdmsRes() {
        return mdmsRes;
    }
    public void setMdmsRes(Map<String, Map<String, List<Object>>> mdmsRes) {
        this.mdmsRes = mdmsRes;
    }


}