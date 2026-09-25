package org.egov.fsm.web.model;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.egov.fsm.web.model.dso.Vendor;
import org.egov.fsm.web.model.location.Address;
import org.egov.fsm.web.model.user.User;
import org.egov.fsm.web.model.vehicle.Vehicle;
import org.egov.fsm.web.model.workflow.ProcessInstance;
import org.egov.tracer.annotations.CustomSafeHtml;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request schema of FSM application.  
 */
@Validated
@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-12-23T12:08:13.326Z[GMT]")

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FSM   {
  @JsonProperty("citizen")
  @Valid
  private User citizen ;

  @CustomSafeHtml
  @JsonProperty("id")
  private String id ;

  @NotNull
  @CustomSafeHtml
  @Size(min=2,max=64)
  @JsonProperty("tenantId")
  private String tenantId ;

  @CustomSafeHtml
  @JsonProperty("applicationNo")
  private String applicationNo ;

  @CustomSafeHtml
  @JsonProperty("description")
  private String description ;

  @CustomSafeHtml
  @JsonProperty("accountId")
  private String accountId ;

  @JsonProperty("additionalDetails")
  private Object additionalDetails ;

  @CustomSafeHtml
  @JsonProperty("applicationStatus")
  private String applicationStatus ;

  @CustomSafeHtml
  @JsonProperty("source")
  private String source ;

  @CustomSafeHtml
  @JsonProperty("sanitationtype")
  private String sanitationtype ;

  @CustomSafeHtml
  @JsonProperty("propertyUsage")
  private String propertyUsage ;
  
  @CustomSafeHtml
  @JsonProperty("vehicleType")
  private String vehicleType ;
 
  @JsonProperty("noOfTrips")
  private Integer noOfTrips ;
  
  @CustomSafeHtml
  @JsonProperty("vehicleCapacity")
  private String vehicleCapacity ;

  /**
   * Gets or Sets status
   */
  public enum StatusEnum {
    ACTIVE("ACTIVE"),
    
    INACTIVE("INACTIVE");

    private String value;

    StatusEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String text) {
      for (StatusEnum b : StatusEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("status")
  private StatusEnum status ;

  @CustomSafeHtml
  @JsonProperty("vehicleId")
  private String vehicleId ;
  
  @JsonProperty("vehicle")
  private Vehicle vehicle ;
  
  @CustomSafeHtml
  @JsonProperty("dsoId")
  private String dsoId ;
  
  @JsonProperty("dso")
  private Vendor dso ;
  
  @JsonProperty("possibleServiceDate")
  private Long possibleServiceDate ;

  @JsonProperty("pitDetail")
  @Valid
  private PitDetail pitDetail ;

  @Valid
  @JsonProperty("address")
  private Address address;

  @JsonProperty("auditDetails")
  private AuditDetails auditDetails ;
  
  @JsonProperty("wasteCollected")
  private Double wasteCollected ;
  

  @JsonProperty("completedOn")
  private Long completedOn ;
  
  @JsonProperty("advanceAmount")
  private BigDecimal advanceAmount=null;

  @JsonProperty("applicationType")
  private String applicationType ;
  
  @JsonProperty("oldApplicationNo")
  private String oldApplicationNo ;
  
  @JsonProperty("paymentPreference")
  private String paymentPreference ;

  @JsonProperty("processInstance")
private ProcessInstance processInstance;

  
 // @JsonProperty("receivedPayment")
 // private String receivedPayment;

}
