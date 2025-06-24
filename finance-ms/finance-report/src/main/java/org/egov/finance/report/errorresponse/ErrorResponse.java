/**
 * 
 * 
 * @author Surya
 */
package org.egov.finance.report.errorresponse;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;

@Data
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private Map<String, String> errors;

    public ErrorResponse(LocalDateTime timestamp, int status, Map<String, String> errors) {
        this.timestamp = timestamp;
        this.status = status;
        this.errors = errors;
    }
}

