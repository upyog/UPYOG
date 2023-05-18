package org.ksmart.birth.birthcommon.model.certificate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificateCriteria {

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("id")
    private String id;

    @JsonProperty("applicationNumber")
    private String applicationNumber;

    @JsonProperty("applicationId")
    private String applicationId;

    @JsonProperty("registrationId")
    private String registrationId;

    @JsonProperty("registrationNo")
    private String registrationNo;

    @JsonProperty("offset")
    private Integer offset;

    @JsonProperty("limit")
    private Integer limit;

    public enum SortOrder {
        ASC,
        DESC
    }
}
