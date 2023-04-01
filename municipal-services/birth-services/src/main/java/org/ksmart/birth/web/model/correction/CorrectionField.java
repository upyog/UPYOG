package org.ksmart.birth.web.model.correction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.ksmart.birth.birthcommon.model.DocumentDetails;
import org.ksmart.birth.common.model.AuditDetails;

import javax.validation.constraints.Size;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CorrectionField {
    @JsonProperty("id")
    private String id;

    @JsonProperty("birthId")
    private String birthId;
    @JsonProperty("correctionFieldName")
    private String correctionFieldName;

    @JsonProperty("conditionCode")
    private String conditionCode;

    @JsonProperty("specificCondition")
    private String specificCondition;

    @JsonProperty("correctionFieldValue")
    private List<CorrectionFieldValue> correctionFieldValue;
    @JsonProperty("CorrectionDocument")
    private List<CorrectionDocument> correctionDocument;
    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;


}
