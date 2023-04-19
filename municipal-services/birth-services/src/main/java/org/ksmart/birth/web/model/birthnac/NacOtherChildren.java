package org.ksmart.birth.web.model.birthnac;

import java.util.List;

import javax.validation.constraints.Size;

import org.ksmart.birth.common.model.AuditDetails;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NacOtherChildren {

	@Size(max = 64)
    @JsonProperty("id")
    private String id;
    @Size(max = 1000)
    @JsonProperty("childNameEn")
    private String childNameEn;
    @Size(max = 1000)
    @JsonProperty("childNameMl")
    private String childNameMl;
    @Size(max = 12)
    @JsonProperty("sex")
    private String sex;     
    @JsonProperty("nacorderofChildren")
    private Integer orderOfBirth;
    @Size(max = 2500)
    @JsonProperty("dob")
    private Long dob;
    @Size(max = 64)
    @JsonProperty("isAlive")
    private Boolean isAlive;  
    
    @Size(max = 64)
   	@JsonProperty("parentBrthDtlId")
   	private String parentBrthDtlId;
    
    
    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;


}
