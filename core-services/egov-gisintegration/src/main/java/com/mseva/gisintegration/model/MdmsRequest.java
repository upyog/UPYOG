package com.mseva.gisintegration.model;

import lombok.Data;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;

@Data
public class MdmsRequest {

    public RequestInfo RequestInfo;
    public MdmsCriteria MdmsCriteria;

    @Data
    public static class MdmsCriteria {
        public String tenantId;
        public List<ModuleDetail> moduleDetails;
    }

    @Data
    public static class ModuleDetail {
        public String moduleName;
        public List<MasterDetail> masterDetails;
    }

    @Data
    public static class MasterDetail {
        public String name;
    }
}