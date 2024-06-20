package org.egov.web.models;

import java.time.LocalDate;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
//import digit.web.models.Address;
//import digit.web.models.AuditDetails;
//import digit.web.models.FatherApplicant;
//import digit.web.models.MotherApplicant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * A Object holds the basic data for a Birth Registration Application
 */
//@ApiModel(description = "A Object holds the basic data for a Birth Registration Application")
@Validated
//@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2022-08-16T15:34:24.436+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchemeCreationApplication   {

    
    

	@JsonProperty("id")
    private Long id;
	@JsonProperty("name")
    private String name;
	@JsonProperty("startDate")
    private LocalDate startDate;
	@JsonProperty("endDate")
    private LocalDate endDate;
	@JsonProperty("schemeDescription")
    private String schemeDescription;


}

