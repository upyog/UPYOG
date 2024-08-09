package org.egov.demand.model ;

import lombok.*;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionUser {

    private Long id;

    private String uuid;

    @NotNull
    private String name;

    private String userName;

    @NotNull
    private String mobileNumber;

    private String emailId;

    @NotNull
    private String tenantId;

    public TransactionUser(org.egov.common.contract.request.User user) {
        this.id = user.getId();
        this.uuid = user.getUuid();
        this.name = user.getName();
        this.userName = user.getUserName();
        this.mobileNumber = user.getMobileNumber();
        this.emailId = user.getEmailId();
        this.tenantId = user.getTenantId();
    }

}
