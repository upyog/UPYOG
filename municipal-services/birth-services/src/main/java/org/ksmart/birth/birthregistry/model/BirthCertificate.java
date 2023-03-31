package org.ksmart.birth.birthregistry.model;

import java.sql.Timestamp;
import java.util.List;

import org.ksmart.birth.common.model.Amount;
import org.ksmart.birth.common.model.AuditDetails;
import org.egov.common.contract.request.User;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
//import org.ksmart.birth.birthregistry.calculation.Calculation;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BirthCertificate {

  @JsonProperty("citizen")
  private User citizen = null;

  @JsonProperty("id")
  private String id = null;

  @JsonProperty("tenantId")
  private String tenantId;

  @JsonProperty("businessService")
  private String businessService = null;
  @JsonProperty("registrtionNo")
  private String registrtionNo = null;

  @JsonProperty("birthCertificateNo")
  private String birthCertificateNo;

  @JsonProperty("additionalDetail")
  private Object additionalDetail = null;

  @JsonProperty("source")
  private String source = null;
  
  @JsonProperty("taxPeriodFrom")
  private Long taxPeriodFrom = null;

  @JsonProperty("taxPeriodTo")
  private Long taxPeriodTo = null;

  @JsonProperty("amount")
  private List<Amount> amount;

  @JsonProperty("registrationId")
  private String registrationId = null;

    @Size(max = 64)
    @JsonProperty("applicationId")
    private String applicationId;

    @Size(max = 64)
    @JsonProperty("applicationNumber")
    private String applicationNumber;

  
  @JsonProperty("filestoreid")
  private String filestoreid = null;

  @JsonProperty("auditDetails")
  private AuditDetails auditDetails = null;

  @JsonProperty("gender")
  private String gender = null;

  @JsonProperty("birthPlace")
  private String birthPlace = null;

  @JsonProperty("state")
  private String state = null;

  @JsonProperty("ward")
  private String ward = null;

  @JsonProperty("district")
  private String district = null;

    @Size(max = 45)
    @JsonProperty("applicationType")
    private String applicationType;

    public enum StatusEnum {
	  ACTIVE("ACTIVE"),
	  
	  CANCELLED("CANCELLED"),
	  
	  FREE_DOWNLOAD("FREE_DOWNLOAD"),

	  PAID_DOWNLOAD("PAID_DOWNLOAD"),

	  PAID_PDF_GENERATED("PAID_PDF_GENERATED"),
	  
	  PAID("PAID");

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

  @JsonProperty("applicationStatus")
  private StatusEnum applicationStatus = null;
  
  @JsonProperty("counter")
  private Integer counter ;
  
  private String embeddedUrl;
  
  private Long dateofissue;

  private Timestamp dateofbirth;

  private Timestamp dateofreport;

  private String year;
}
