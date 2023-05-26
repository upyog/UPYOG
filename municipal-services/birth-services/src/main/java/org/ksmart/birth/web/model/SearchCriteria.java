package org.ksmart.birth.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.ksmart.birth.utils.enums.SearchCriteriaCodes;

import javax.validation.Valid;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchCriteria {

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("id")
    private String id; // birthapplicant id

    @JsonProperty("applicationNumber")
    private List<String> applicationNumber;

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

    @JsonProperty("applicationType")
    private String applicationType;

    @JsonProperty("dateOfBirthFrom")
    @Valid
    private Long dateOfBirthFrom;

    @JsonProperty("dateOfBirthTo")
    @Valid
    private Long dateOfBirthTo;

    @JsonProperty("dateOfBirth")
    @Valid
    private String dateOfBirth;

    @JsonProperty("hospitalId")
    @Valid
    private String hospitalId;

    @JsonProperty("institutionId")
    @Valid
    private String institutionId;

    @JsonProperty("childName")
    private String childName;

    @JsonProperty("searchType")
    private String searchType = SearchCriteriaCodes.SEARCH_TYPE_MYAPP.getCode();

    @JsonProperty("nameOfFather")
    @Valid
    private String nameOfFather;

    @JsonProperty("nameOfMother")
    @Valid
    private String nameOfMother;

    @JsonProperty("sex")
    @Valid
    private String gender;

    @JsonProperty("wardCode")
    @Valid
    private String wardCode;

    @JsonProperty("offset")
    private Integer offset;

    @JsonProperty("limit")
    private Integer limit;

    @JsonProperty("sortBy")
    private SortBy sortBy;

    @JsonProperty("sortOrder")
    private SortOrder sortOrder;
    
    @JsonProperty("appNumber")
    private String appNumber;
    
    @JsonProperty("uuid")
    private String uuid;
   
    
    @JsonProperty("BusinessService")
    private String BusinessService;

    @JsonProperty("createdBy")
    @JsonIgnore
    private List<String> createdBy;

    public enum SortOrder {
        ASC,
        DESC
    }

    public enum SortBy {
        applicationType,
        applicationNumber,
        dateOfBirth,
        registrationNo,
        wardCode,
        institutionId,
        hospitalId,
        mother,
        gender,
        tenantId
    }

}
