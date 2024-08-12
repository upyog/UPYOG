package digit.web.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankDetails {
    private Long branchId;
    private Long userId;
    private String tenantId;
    private String name;
    private String branchName;
    private String ifsc;
    private String micr;
    private String accountNumber;
    private String createdBy;
    private String modifiedBy;
    private Long modifiedOn;


}
