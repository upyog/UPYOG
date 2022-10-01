package org.egov.filemgmnt.util;

import org.egov.filemgmnt.web.enums.ErrorCodes;
import org.egov.tracer.model.CustomException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CoreUtils {

    public static void ignore(Throwable t) {
        // Ignore error
    }

    public static CustomException newException(ErrorCodes errorCode, String message) {
        return new CustomException(errorCode.getCode(), message);
    }
}
