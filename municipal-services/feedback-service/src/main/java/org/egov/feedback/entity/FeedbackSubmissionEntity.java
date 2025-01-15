package org.egov.feedback.entity;

import javax.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "egov_feedback_submission")
@Data // Lombok annotation to generate getters, setters, toString, equals, and hashCode
public class FeedbackSubmissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_eg_feedback_submission_no")
    @SequenceGenerator(
        name = "seq_eg_feedback_submission_no", 
        sequenceName = "seq_eg_feedback_submission_no", 
        allocationSize = 1 // Ensure this matches the sequence's increment value
    )
    private Long id;

    @Column(name = "serviceId", nullable = false, length = 100)
    private String serviceId;

    @Column(name = "applicationNo", nullable = false, length = 255)
    private String applicationNo;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "mobileNo", length = 150, nullable = false) // Ensure mobileNo is not nullable as per table structure
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
    private String status; // Added this field, assuming you want it as per your DB structure

    @Column(name = "action", length = 150)
    private String action; // Added this field, assuming you want it as per your DB structure

    @Type(type = "json")
    @Column(name = "json_column", columnDefinition = "jsonb")
    private JsonNode additionalDetail;

    @Column(name = "createdBy", length = 150)
    private String createdBy;

    @Column(name = "lastModifiedBy", length = 150)
    private String lastModifiedBy;

    @Column(name = "createdTime")
    private Long createdTime;

    @Column(name = "lastModifiedTime")
    private Long lastModifiedTime;
}
