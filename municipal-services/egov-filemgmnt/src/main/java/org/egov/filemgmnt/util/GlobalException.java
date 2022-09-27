package org.egov.filemgmnt.util;

import org.egov.filemgmnt.web.enums.ErrorCodes;
import org.egov.tracer.model.CustomException;

public class GlobalException extends CustomException {

    private static final long serialVersionUID = -5316203007519923839L;

    public GlobalException(ErrorCodes errorCode, String message) {
        super(errorCode.getCode(), message);
    }

}
