package org.egov.user.domain.exception;

import lombok.Getter;
import org.egov.user.domain.model.UserSearchCriteria;

public class DuplicateMobileNumberException extends RuntimeException {

    private static final long serialVersionUID = -6903761146294214595L;
    @Getter
    private UserSearchCriteria userSearchCriteria;
    
    private String message;
    
    private boolean success=false;
    public DuplicateMobileNumberException(UserSearchCriteria userSearchCriteria,String message) {
        this.userSearchCriteria = userSearchCriteria;
        this.message=message;
    }

}
