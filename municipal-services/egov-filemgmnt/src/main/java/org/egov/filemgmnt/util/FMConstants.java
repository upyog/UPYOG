package org.egov.filemgmnt.util;

import org.springframework.stereotype.Component;

@Component
public class FMConstants {

    // MDMS

    public static final String FILEMANAGEMENT_MODULE = "FileManagement";

    public static final String FILEMANAGEMENT_MODULE_CODE = "FM";

    // mdms master names

    public static final String COMMON_MASTERS_MODULE = "common-masters";

    // error constants

    public static final String FILE_CATEGORY = "FileCategory";

    public static final String FILE_ARISING_MODE = "FileArisingmode";

    // mdms path codes

    public static final String FM_MDMS_JSONPATH = "$.MdmsRes.FileManagement";

    public static final String COMMON_MASTER_JSONPATH_CODE = "$.MdmsRes.common-masters";

    // mdms master names

    public static final String FM_MDMS_FILE_SERVICE_SUBTYPE = "FileServiceSubtype";

    public static final String FM_MDMS_FILE_SERVICE_SUBTYPE_CODE_JSONPATH = "$.MdmsRes.FileManagement.FileServiceSubtype[*].code";

    // error constants

    public static final String INVALID_TENANT_ID_MDMS_KEY = "INVALID TENANTID";

    public static final String INVALID_TENANT_ID_MDMS_MSG = "No data found for this tenentID";

    // workflow integrator

    public static final String businessService_FM = "NewDFM";

    public static final String TENANTIDKEY = "tenantId";

    public static final String BUSINESSSERVICEKEY = "businessService";

    public static final String ACTIONKEY = "action";

    public static final String COMMENTKEY = "comment";

    public static final String MODULENAMEKEY = "moduleName";

    public static final String BUSINESSIDKEY = "businessId";

    public static final String DOCUMENTSKEY = "documents";

    public static final String ASSIGNEEKEY = "assignes";

    public static final String UUIDKEY = "uuid";

    public static final String FMMODULENAMEVALUE = "FM";

    public static final String WORKFLOWREQUESTARRAYKEY = "ProcessInstances";

    public static final String REQUESTINFOKEY = "RequestInfo";

    public static final String PROCESSINSTANCESJOSNKEY = "$.ProcessInstances";

    public static final String BUSINESSIDJOSNKEY = "$.businessId";

    public static final String STATUSJSONKEY = "$.state.applicationStatus";

    public static final String TRIGGER_NOWORKFLOW = "NOWORKFLOW";

}
