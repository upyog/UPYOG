package org.ksmart.birth.web.model.correction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.ksmart.birth.common.model.AuditDetails;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CorrectionFieldValue {
    @JsonProperty("id")
    private String id;
    @JsonProperty("birthId")
    private String birthId;

    @JsonProperty("correctionId")
    private String correctionId;
    @JsonProperty("correctionFieldName")
    private String correctionFieldName;
    @JsonProperty("column")
    private String column;
    @JsonProperty("tableName")
    private String tableName;
    @JsonProperty("columnName")
    private String columnName;
    @JsonProperty("newValue")
    private String newValue;
    @JsonProperty("oldValue")
    private String oldValue;
    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
}
