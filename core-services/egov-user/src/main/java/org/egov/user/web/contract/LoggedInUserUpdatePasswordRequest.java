package org.egov.user.web.contract;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.user.config.PasswordChangeProperties;
import org.egov.user.config.UserServiceConstants;
import org.egov.user.domain.model.enums.UserType;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class
LoggedInUserUpdatePasswordRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;
    private String existingPassword;
    private String newPassword;

    @Pattern(regexp = UserServiceConstants.PATTERN_TENANT)
    @Size(max = 256)
    private String tenantId;
    private UserType type;
    private boolean selfUpdate= true;
    private String userName;
 
    public org.egov.user.domain.model.LoggedInUserUpdatePasswordRequest toDomain() {
        return org.egov.user.domain.model.LoggedInUserUpdatePasswordRequest.builder()
                .existingPassword(existingPassword)
                .newPassword(newPassword)
                .userName(getUsername())
                .tenantId(tenantId)
                .selfUpdate(selfUpdate)
                .type(type)
                .requestInfo(requestInfo)
                .build();
    }

    private String getUsername() {
    	String retUserName = null;
    	//update for other user in case of department updates specifice user eg. admin
    	if(selfUpdate)	
    		retUserName =  requestInfo == null || requestInfo.getUserInfo() == null ? null : requestInfo.getUserInfo().getUserName();
    	else {
    		retUserName =  userName ==  null ? null: userName ;
    	}
    	return retUserName;
    }
}

