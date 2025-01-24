package org.egov.feedback.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class FeedbackSubmission {
	
	private Long id;
    private String serviceId; // ID of the service
    private String applicationNo; // Application number
    private String email; // Email address of the user
    private String userUuid;// Unique identifier for the user
    private String mobileNo;//mobile number
    private String rating; // Feedback rating
    private String source; // Feedback source
    private String status;
    private String action;
    private String comment;
    private String feedbackType; // Type of feedback
    private JsonNode additionalDetail; // JSON field for additional details
    private String createdBy; // Creator of the feedback
    private String lastModifiedBy; // Last modifier of the feedback
    private Long createdTime; // Timestamp of creation
    private Long lastModifiedTime; // Timestamp of the last modification
    @JsonProperty("auditDetails")
    @Builder.Default
    private AuditDetails auditDetails = null; // Default value for the builder
}
