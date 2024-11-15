package org.egov.pqm.error;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CustomException extends org.egov.tracer.model.CustomException {
    @Builder
    public CustomException(String code, String message) {
        super(code, message);
    }
}
