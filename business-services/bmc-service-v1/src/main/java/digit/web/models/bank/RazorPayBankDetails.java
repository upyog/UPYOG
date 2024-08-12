package digit.web.models.bank;


import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class RazorPayBankDetails {

     @JsonProperty("MICR")
    private String micr;

    @JsonProperty("BRANCH")
    private String branch;

    @JsonProperty("ADDRESS")
    private String address;

    @JsonProperty("STATE")
    private String state;

    @JsonProperty("CONTACT")
    private String contact;

    @JsonProperty("UPI")
    private Boolean upi;

    @JsonProperty("RTGS")
    private Boolean rtgs;

    @JsonProperty("CITY")
    private String city;

    @JsonProperty("CENTRE")
    private String centre;

    @JsonProperty("DISTRICT")
    private String district;

    @JsonProperty("NEFT")
    private Boolean neft;

    @JsonProperty("IMPS")
    private Boolean imps;

    @JsonProperty("SWIFT")
    private String swift;

    @JsonProperty("ISO3166")
    private String iso3166;

    @JsonProperty("BANK")
    private String bank;

    @JsonProperty("BANKCODE")
    private String bankCode;

    @JsonProperty("IFSC")
    private String ifsc;

    private Long branchId;
}