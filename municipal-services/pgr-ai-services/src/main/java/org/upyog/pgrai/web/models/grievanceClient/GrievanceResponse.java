package org.upyog.pgrai.web.models.grievanceClient;

import lombok.*;

import java.util.Map;

/**
 * GrievanceResponse is a class that represents the response from the grievance fast api service.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrievanceResponse {
    private Map<String, Object> _shards;
    private String result;
}
