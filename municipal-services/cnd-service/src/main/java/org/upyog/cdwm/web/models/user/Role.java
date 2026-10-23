package org.upyog.cdwm.web.models.user;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = {"code", "tenantId"})
public class Role {
    private static final String CITIZEN = "CITIZEN";
    private String name;
    private String code;
    private String tenantId;

    public static Role getCitizenRole() {
        return Role.builder().code(CITIZEN).build();
    }
}