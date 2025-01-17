package org.egov.feedback.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "egov_feedback_submission",
       indexes = {
           @Index(name = "idx_mobile_no", columnList = "mobileNo"),
           @Index(name = "idx_application_no", columnList = "applicationNo")
       })
@Data
public class FeedbackSubmissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_eg_feedback_submission_no")
    @SequenceGenerator(
        name = "seq_eg_feedback_submission_no",
        sequenceName = "seq_eg_feedback_submission_no",
        allocationSize = 1
    )
    private Long id;

    @Column(name = "serviceId", nullable = false, length = 100)
    @NotNull(message = "Service ID is mandatory")
    @Size(max = 100, message = "Service ID cannot exceed 100 characters")
    private String serviceId;

    @Column(name = "applicationNo", nullable = false, length = 255)
    @NotNull(message = "Application number is mandatory")
    @Size(max = 255, message = "Application number cannot exceed 255 characters")
    private String applicationNo;

    @Column(name = "email", length = 150)
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    @Column(name = "mobileNo", nullable = false, length = 150)
    @NotNull(message = "Mobile number is mandatory")
    @Size(max = 150, message = "Mobile number cannot exceed 150 characters")
    private String mobileNo;

    @Column(name = "userUuid", length = 150)
    private String userUuid;

    @Column(name = "rating", length = 150)
    private String rating;

    @Column(name = "source", length = 150)
    private String source;

    @Column(name = "feedbackType", length = 150)
    private String feedbackType;

    @Column(name = "status", length = 150)
    private String status;

    @Column(name = "action", length = 150)
    private String action;

    @Convert(converter = JsonNodeConverter.class)
    @Column(name = "additionaldetail", columnDefinition = "jsonb")
    private JsonNode additionalDetail = new ObjectMapper().createObjectNode();

    @Column(name = "createdBy", length = 150)
    private String createdBy;

    @Column(name = "lastModifiedBy", length = 150)
    private String lastModifiedBy;

    @Column(name = "createdTime")
    private Long createdTime;

    @Column(name = "lastModifiedTime")
    private Long lastModifiedTime;

    @PrePersist
    protected void onCreate() {
        this.createdTime = System.currentTimeMillis();
        this.lastModifiedTime = this.createdTime;
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedTime = System.currentTimeMillis();
    }
}
