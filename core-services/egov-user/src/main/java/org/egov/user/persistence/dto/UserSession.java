package org.egov.user.persistence.dto;

import lombok.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {
    private UUID id;
    private String userUuid;
    private Long userId;
    private String userName;
    private String userType;
    private LocalDateTime  loginTime;
    private LocalDateTime  logoutTime;
    private String ipAddress;
    private Boolean isCurrentlyLoggedIn;
    private Boolean isautologout;

}
