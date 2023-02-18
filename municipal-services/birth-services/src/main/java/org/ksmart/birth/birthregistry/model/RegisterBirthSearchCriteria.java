package org.ksmart.birth.birthregistry.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterBirthSearchCriteria {
    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("id")
    private String id; // birthapplicant id
    @JsonProperty("ackNo")
    private String ackNo;

    @JsonProperty("registrationNo")
    private String registrationNo;
    @JsonProperty("fromDate")
    private Long fromDate; // report date

    @JsonProperty("toDate")
    private Long toDate; // report date

    @JsonProperty("fromDateReg")
    private Long fromDateReg; // Registration date

    @JsonProperty("toDateReg")
    private Long toDateReg; // Registration date

    @JsonProperty("aadhaarNo")
    private String aadhaarNo;

    @JsonProperty("offset")
    private Integer offset;

    @JsonProperty("limit")
    private Integer limit;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("dob")
    @Valid
    private LocalDate dob;
    @JsonProperty("mother")
    @Valid
    private String nameOfMother;

    @JsonProperty("sex")
    @Valid
    private String gender;
}
