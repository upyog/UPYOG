package digit.repository;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserSearchCriteria {
    @JsonProperty("Option")
    private String option;

    @JsonProperty("UserID")
    private Long userId;

    @JsonProperty("TenantID")
    private String tenantId;


}


