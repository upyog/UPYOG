package digit.web.models.user;

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
public class UserWard {

     @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;

    @JsonProperty("zoneCode")
    private String zoneCode;

    @JsonProperty("blockCode")
    private String blockCode;

    @JsonProperty("i18nKey")
    private String i18nKey;


}
