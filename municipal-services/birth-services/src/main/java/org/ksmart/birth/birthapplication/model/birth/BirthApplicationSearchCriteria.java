package org.ksmart.birth.birthapplication.model.birth;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.Valid;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BirthApplicationSearchCriteria {

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("id")
    private String id; // birthapplicant id

    @JsonProperty("applicationNo")
    private String applicationNo;

    @JsonProperty("registrationNo")
    private String registrationNo;

    @JsonProperty("fileCode")
    private String fileCode;

    @JsonProperty("fromDate")
    private Long fromDate; // report date

    @JsonProperty("toDate")
    private Long toDate; // report date

    @JsonProperty("fromDateFile")
    private Long fromDateFile; // file date

    @JsonProperty("toDateFile")
    private Long toDateFile; // file date

    @JsonProperty("aadhaarNo")
    private String aadhaarNo;

    @JsonProperty("offset")
    private Integer offset;

    @JsonProperty("limit")
    private Integer limit;

    @JsonProperty("dateOfBirth")
    @Valid
    private String dateOfBirth;
}
