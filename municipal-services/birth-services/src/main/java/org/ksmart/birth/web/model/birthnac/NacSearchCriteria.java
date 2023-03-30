package org.ksmart.birth.web.model.birthnac;


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
public class NacSearchCriteria {
	  @JsonProperty("tenantId")
	    private String tenantId;

	    @JsonProperty("id")
	    private String id; // birthapplicant id
	    @JsonProperty("applicationNumber")
	    private String applicationNumber;
	    @JsonProperty("childName")
	    private String childName;

	    @JsonProperty("nameOfFather")
	    @Valid
	    private String nameOfFather;

	    @JsonProperty("registrationNo")
	    private String registrationNo;

	    @JsonProperty("registrationDate")
	    private Long registrationDate;
	    @JsonProperty("birthDate")
	    private Long birthDate;
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
	    @JsonProperty("nameOfMother")
	    @Valid
	    private String nameOfMother;
	    @JsonProperty("hospitalId")
	    @Valid
	    private String hospitalId;
	    @JsonProperty("institutionId")
	    @Valid
	    private String institutionId;
	    @JsonProperty("gender")
	    @Valid
	    private String gender;
	    @JsonProperty("wardCode")
	    @Valid
	    private String wardCode;
	    @JsonProperty("sortBy")
	    private SortBy sortBy;

	    @JsonProperty("sortOrder")
	    private SortOrder sortOrder;

	    public enum SortOrder {
	        ASC,
	        DESC
	    }

	    public enum SortBy {
	        applicationNumber,
	        registrationDate,
	        birthDate,
	        registrationNo,
	        nameOfMother,
	        gender,
	        tenantId,
	        institutionId,
	        hospitalId,
	        wardCode,
	        childName,
	        nameOfFather
	    }

}
