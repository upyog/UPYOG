package org.egov.encryption.models;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditObject {

    private String id;

    private String userId;

    private Long timestamp;

    private JsonNode data;

}
