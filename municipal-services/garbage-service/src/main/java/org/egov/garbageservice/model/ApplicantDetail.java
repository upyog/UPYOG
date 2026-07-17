package org.egov.garbageservice.model;

import org.egov.tracer.annotations.CustomSafeHtml;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
/**
 * Applicant person details from the new payload structure (applicantDetails array).
 * Captured on GarbageAccount; richer than garbageSpecification (DOB, guardian, relationship).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicantDetail {

    @CustomSafeHtml
    private String applicantName;

    @CustomSafeHtml
    private String name;

    @CustomSafeHtml
    private String mobileNumber;

    @CustomSafeHtml
    private String alternateNumber;

    @CustomSafeHtml
    private String gender;

    @CustomSafeHtml
    private String dateOfBirth;

    @CustomSafeHtml
    private String relationShipType;

    @CustomSafeHtml
    private String guardianName;

    @CustomSafeHtml
    private String emailId;
}
