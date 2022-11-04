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

}
