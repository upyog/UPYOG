package digit.web.models.user;

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
public class UserAddressDetails {

    @JsonProperty("house")
    private String houseNoBldgApt;

    @JsonProperty("subDistrict")
    private String subDistrict;

    @JsonProperty("postOffice")
    private String postOffice;

    @JsonProperty("landmark")
    private String landmark;

    @JsonProperty("country")
    private String country;

    @JsonProperty("type")
    private String type;

    @JsonProperty("street")
    private String streetRoadLine;

    @JsonProperty("city")
    private String cityTownVillage;

    @JsonProperty("locality")
    private String areaLocalitySector;

    @JsonProperty("district")
    private String district;

    @JsonProperty("state")
    private String state;

    @JsonProperty("pinCode")
    private String pincode;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("version")
    private Long version;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("userId")
    private Long userId;

    private String zoneName;
    private String blockName;
    private UserWard wardName;


}
