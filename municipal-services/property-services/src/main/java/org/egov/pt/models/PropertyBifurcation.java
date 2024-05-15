package org.egov.pt.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.egov.pt.models.enums.Channel;
import org.egov.pt.models.enums.CreationReason;
import org.egov.pt.models.enums.Source;
import org.egov.pt.models.enums.Status;
import org.egov.pt.models.workflow.ProcessInstance;
import org.egov.pt.web.contracts.PropertyRequest;
import org.hibernate.validator.constraints.SafeHtml;
import org.javers.core.metamodel.annotation.DiffIgnore;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Property
 */

@ToString
@Getter
@Setter
@NoArgsConstructor

@Builder
public class PropertyBifurcation  {

	public PropertyBifurcation(Integer id, JsonNode propertyDetails, String parentPropertyId,Integer createdTime, 
			PropertyRequest request,Integer maxBifurcation, boolean status,String childpropertyuuid) {
		super();
		this.id = id;
		this.propertyDetails = propertyDetails;
		this.parentPropertyId = parentPropertyId;
		this.propertyRequest = request;
		this.createdTime= createdTime;
		this.maxBifurcation =  maxBifurcation;
		this.status = status;
		this.childpropertyuuid = childpropertyuuid;
		
	}


	@JsonProperty("id")
	private Integer id;
	
	@DiffIgnore
	@JsonProperty("propertyDetails")
	private JsonNode propertyDetails;
	
	
	@JsonProperty("parentPropertyId")
	private String  parentPropertyId;
	
	
	private Integer createdTime;
	
	private PropertyRequest propertyRequest;
	private Integer maxBifurcation;
	
	
	private boolean status;
	private String childpropertyuuid;
	
	
	
	


}
