package org.egov.pqm.error;

import java.util.Map;

public interface IError {
    CustomException.CustomExceptionBuilder getBuilder(String... paramArray);
}
