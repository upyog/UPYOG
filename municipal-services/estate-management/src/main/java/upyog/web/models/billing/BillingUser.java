package upyog.web.models.billing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingUser {
    private String uuid;
    private Long id;
    private String userName;
    private String type;
    private String name;
    private String mobileNumber;
    private String emailId;
    private String tenantId;
}
