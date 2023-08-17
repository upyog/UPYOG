package digit.web.models;

import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
        @JsonProperty("id")
        private Long id = null;

        @JsonProperty("uuid")
        private String uuid = null;

        @JsonProperty("userName")
        private String userName = null;

        @JsonProperty("name")
        private String name = null;

        @JsonProperty("gender")
        private String gender = null;

        @JsonProperty("mobileNumber")
        private String mobileNumber = null;

        @JsonProperty("emailId")
        private String emailId = null;

        @JsonProperty("altContactNumber")
        private String altContactNumber = null;

        @JsonProperty("pan")
        private String pan = null;

        @JsonProperty("aadhaarNumber")
        private String aadhaarNumber = null;

        @JsonProperty("permanentAddress")
        private String permanentAddress = null;

        @JsonProperty("permanentPincode")
        private String permanentPincode = null;

        @JsonProperty("correspondencePincode")
        private String correspondencePincode = null;

        @JsonProperty("correspondenceAddress")
        private String correspondenceAddress = null;

        @JsonProperty("active")
        private Boolean active = null;

        @JsonProperty("createdBy")
        private Long createdBy = null;

        @JsonProperty("lastModifiedBy")
        private Long lastModifiedBy = null;

        @JsonProperty("tenantId")
        private String tenantId = null;
}
